package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import com.eygraber.virtue.di.scopes.SessionSingleton
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import me.tatarka.inject.annotations.Inject

public interface OnBackPressedCallback {
  public var isEnabled: Boolean

  public fun onBackPressed()
}

@SessionSingleton
@Inject
public class OnBackPressedDispatcher {
  private val callbacks = atomic<List<OnBackPressedCallback>>(emptyList())

  /**
   * Returns `true` if there are any [OnBackPressedCallback] that are enabled.
   * If [ignoreFirst] is `true` then the first callback will not be considered.
   */
  public fun hasEnabledCallbacks(ignoreFirst: Boolean = true): Boolean =
    callbacks
      .value
      .let { if(ignoreFirst) it.drop(1) else it }
      .any { it.isEnabled }

  public fun addCallback(callback: OnBackPressedCallback) {
    callbacks.update {
      it + callback
    }
  }

  public fun removeCallback(callback: OnBackPressedCallback) {
    callbacks.update { callbacks ->
      callbacks.filter { it != callback }
    }
  }

  public fun onBackPressed() {
    callbacks
      .value
      .lastOrNull { it.isEnabled }
      ?.onBackPressed()
  }
}

public object LocalOnBackPressedDispatcher {
  @Suppress("MemberNameEqualsClassName")
  private val LocalOnBackPressedDispatcher =
    compositionLocalOf<OnBackPressedDispatcher?> { null }

  public val current: OnBackPressedDispatcher?
    @Composable
    get() = LocalOnBackPressedDispatcher.current

  public infix fun provides(dispatcherOwner: OnBackPressedDispatcher):
    ProvidedValue<OnBackPressedDispatcher?> = LocalOnBackPressedDispatcher.provides(dispatcherOwner)
}
