package com.eygraber.virtue.samples.auth.shared.login

import androidx.compose.foundation.text.input.TextFieldState

sealed interface LoginIntent {
  data class Login(val token: TextFieldState) : LoginIntent
}
