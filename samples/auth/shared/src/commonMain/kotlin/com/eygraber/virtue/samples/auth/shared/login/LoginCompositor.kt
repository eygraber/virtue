package com.eygraber.virtue.samples.auth.shared.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.auth.VirtueAuth
import com.eygraber.virtue.di.scopes.DestinationSingleton
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class LoginButtonEnabledSource {
  @Composable
  fun currentState(token: TextFieldState): Boolean {
    val isLoginButtonEnabled by remember {
      derivedStateOf {
        token.text.isNotEmpty()
      }
    }

    return isLoginButtonEnabled
  }
}

@DestinationSingleton
@Inject
class LoginCompositor(
  private val navigator: LoginNavigator,
  private val loginButtonEnabledSource: LoginButtonEnabledSource,
  private val auth: VirtueAuth,
  private val loginStringsSource: LoginStringsSource,
) : ViceCompositor<LoginIntent, LoginViewState> {
  private var isTokenEnabled by mutableStateOf(true)

  @Composable
  override fun composite(): LoginViewState {
    val token = rememberTextFieldState()

    return LoginViewState(
      token = token,
      isTokenEnabled = isTokenEnabled,
      isLoginButtonEnabled = loginButtonEnabledSource.currentState(token),
      strings = loginStringsSource.currentState(),
    )
  }

  override suspend fun onIntent(intent: LoginIntent) {
    when(intent) {
      is LoginIntent.Login -> {
        isTokenEnabled = false

        auth.login(
          token = VirtueAuth.Token(
            token = intent.token.text.toString(),
          ),
        )

        navigator.onNavigateToLoggedIn()
      }
    }
  }
}
