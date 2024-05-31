package com.eygraber.virtue.samples.todo.shared.about

sealed interface AboutUsIntent {
  data object Close : AboutUsIntent
}
