package com.eygraber.virtue.auth

import com.eygraber.virtue.di.scopes.AppSingleton
import com.eygraber.virtue.storage.kv.EncryptedDeviceKeyValueStorage
import com.eygraber.virtue.storage.kv.edit
import com.eygraber.virtue.utils.runCatchingCoroutine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

@AppSingleton
@Inject
public class VirtueAuth(
  private val deviceStorage: EncryptedDeviceKeyValueStorage,
) {
  @Serializable
  public class Token(
    public val token: String,
    internal val expirationPolicy: ExpirationPolicy = ExpirationPolicy.Expire(),
    internal val defaultExtensionPolicy: ExtensionPolicy? = null,
  ) {
    public fun isExpired(): Boolean = expirationPolicy.at < Clock.System.now()
  }

  @Serializable
  public sealed interface ExpirationPolicy {
    public val at: Instant

    @Serializable
    public data class Refresh(
      val refreshToken: String,
      override val at: Instant = Instant.DISTANT_FUTURE,
    ) : ExpirationPolicy

    @Serializable
    public data class Expire(
      override val at: Instant = Instant.DISTANT_FUTURE,
    ) : ExpirationPolicy
  }

  @Serializable
  public sealed interface ExtensionPolicy {
    public fun extendExpiration(currentExpiration: Instant): Instant

    @Serializable
    public data class ExtendFromNow(
      private val duration: Duration,
    ) : ExtensionPolicy {
      override fun extendExpiration(currentExpiration: Instant): Instant =
        Clock.System.now() + duration
    }

    @Serializable
    public data class ExtendFromExpiration(
      private val duration: Duration,
    ) : ExtensionPolicy {
      override fun extendExpiration(currentExpiration: Instant): Instant =
        currentExpiration + duration
    }
  }

  public sealed interface State {
    public data object LoggedIn : State

    public data object LoggedOut : State

    public sealed interface LoggingOut : State {
      public data object Automatically : LoggingOut
      public data object Manually : LoggingOut
    }

    public data object RefreshRequired : State

    public data object Error : State
  }

  private val state = MutableStateFlow<State?>(null)

  public val stateFlow: Flow<State> = state.filterNotNull()

  public suspend fun initialize() {
    val isLoggingOut = deviceStorage.getInt(LOG_OUT, IS_NOT_LOGGING_OUT)
    state.value = when {
      isLoggingOut != IS_NOT_LOGGING_OUT -> {
        when(isLoggingOut) {
          IS_LOGGING_OUT_AUTOMATICALLY -> State.LoggingOut.Automatically
          IS_LOGGING_OUT_MANUALLY -> State.LoggingOut.Manually
          IS_LOGGED_OUT -> State.LoggedOut
          else -> State.Error
        }
      }

      else -> when(val token = loadToken()) {
        null -> State.LoggedOut

        else -> {
          if(token.isExpired() && token.expirationPolicy is ExpirationPolicy.Expire) {
            startLogout(State.LoggingOut.Automatically)
            State.LoggingOut.Automatically
          }
          else {
            State.LoggedIn
          }
        }
      }
    }
  }

  public suspend fun login(
    token: Token,
  ) {
    storeToken(token)

    state.value = State.LoggedIn
  }

  public suspend fun extendTokenExpiration(
    token: Token,
    extensionPolicy: ExtensionPolicy? = token.defaultExtensionPolicy,
  ): Boolean = when(val extension = extensionPolicy ?: token.defaultExtensionPolicy) {
    null -> false

    else -> {
      val extendedToken = Token(
        token = token.token,
        expirationPolicy = when(token.expirationPolicy) {
          is ExpirationPolicy.Expire -> ExpirationPolicy.Expire(
            at = extension.extendExpiration(token.expirationPolicy.at),
          )

          is ExpirationPolicy.Refresh -> ExpirationPolicy.Refresh(
            refreshToken = token.expirationPolicy.refreshToken,
            at = extension.extendExpiration(token.expirationPolicy.at),
          )
        },
        defaultExtensionPolicy = token.defaultExtensionPolicy,
      )

      storeToken(extendedToken)

      true
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  public suspend fun loadToken(): Token? =
    deviceStorage.getString(TOKEN, null)?.let { tokenHex ->
      runCatchingCoroutine {
        Cbor.Default.decodeFromHexString<Token>(tokenHex)
      }.getOrNull()
    }

  public suspend fun isLoginValid(): Boolean =
    state.value == State.LoggedIn && loadToken()?.isExpired() == false

  /**
   * Will return `true` if there is no token
   */
  public suspend fun isTokenExpired(): Boolean = loadToken()?.isExpired() ?: true

  public suspend fun currentState(): State = stateFlow.first()

  public suspend fun awaitState(state: State): Boolean =
    stateFlow.filterIsInstance(state::class).firstOrNull() != null

  public suspend fun startLogout(
    reason: State.LoggingOut,
  ) {
    deviceStorage.edit {
      putInt(
        key = LOG_OUT,
        value = when(reason) {
          State.LoggingOut.Automatically -> IS_LOGGING_OUT_AUTOMATICALLY
          State.LoggingOut.Manually -> IS_LOGGING_OUT_MANUALLY
        },
      )
    }

    state.value = reason
  }

  public suspend fun onError() {
    deviceStorage.edit {
      putInt(LOG_OUT, IS_ERROR)
      remove(TOKEN)
    }

    state.value = State.Error
  }

  public suspend fun onLogoutSucceeded() {
    deviceStorage.edit {
      putInt(LOG_OUT, IS_LOGGED_OUT)
      remove(TOKEN)
    }

    state.value = State.LoggedOut
  }

  @OptIn(ExperimentalSerializationApi::class)
  private suspend fun storeToken(token: Token) {
    deviceStorage.edit {
      runCatchingCoroutine {
        remove(LOG_OUT)
        putString(TOKEN, Cbor.Default.encodeToHexString(token))
      }
    }
  }

  public companion object {
    private const val LOG_OUT = "com.eygraber.virtue.auth.LOG_OUT"
    private const val IS_NOT_LOGGING_OUT = -1
    private const val IS_LOGGING_OUT_AUTOMATICALLY = 0
    private const val IS_LOGGING_OUT_MANUALLY = 1
    private const val IS_LOGGED_OUT = 2
    private const val IS_ERROR = 3

    private const val TOKEN = "com.eygraber.virtue.auth.TOKEN"
  }
}
