package com.eygraber.virtue.samples.todo.shared.details

data class DetailsViewState(
  val title: String,
  val titleError: String?,
  val description: String,
  val descriptionError: String?,
  val isDoneEnabled: Boolean,
)
