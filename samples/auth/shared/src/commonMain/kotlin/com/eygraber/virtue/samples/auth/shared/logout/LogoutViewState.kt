package com.eygraber.virtue.samples.auth.shared.logout

import com.eygraber.vice.loadable.ViceLoadable

data class LogoutStrings(
  val title: String,
)

data class LogoutViewState(
  val strings: ViceLoadable<LogoutStrings>,
)
