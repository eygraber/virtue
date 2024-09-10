package com.eygraber.virtue.samples.auth.shared.logged_in

import com.eygraber.vice.loadable.ViceLoadable

data class LoggedInStrings(
  val title: String,
  val content: String,
  val logoutButton: String,
)

data class LoggedInViewState(
  val strings: ViceLoadable<LoggedInStrings>,
)
