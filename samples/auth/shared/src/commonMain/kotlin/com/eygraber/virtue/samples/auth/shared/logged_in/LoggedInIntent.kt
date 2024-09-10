package com.eygraber.virtue.samples.auth.shared.logged_in

sealed interface LoggedInIntent {
  data object Logout : LoggedInIntent
}
