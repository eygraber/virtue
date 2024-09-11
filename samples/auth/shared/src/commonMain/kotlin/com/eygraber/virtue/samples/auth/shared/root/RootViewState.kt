package com.eygraber.virtue.samples.auth.shared.root

import com.eygraber.vice.loadable.ViceLoadable

data class RootStrings(
  val errorDialogTitle: String,
  val errorDialogText: String,
  val errorDialogRestart: String,
  val errorDialogClearData: String,
)

data class RootViewState(
  val isErrorDialogShowing: Boolean,
  val strings: ViceLoadable<RootStrings>,
)
