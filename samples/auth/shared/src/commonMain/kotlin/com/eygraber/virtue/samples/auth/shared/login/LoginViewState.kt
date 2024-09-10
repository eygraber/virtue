package com.eygraber.virtue.samples.auth.shared.login

import androidx.compose.foundation.text.input.TextFieldState
import com.eygraber.vice.loadable.ViceLoadable

data class LoginStrings(
  val title: String,
  val tokenLabel: String,
  val button: String,
)

data class LoginViewState(
  val token: TextFieldState,
  val isTokenEnabled: Boolean,
  val isLoginButtonEnabled: Boolean,
  val strings: ViceLoadable<LoginStrings>,
)
