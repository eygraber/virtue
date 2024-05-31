package com.eygraber.virtue.samples.todo.shared

data class TodoItem(
  val id: String,
  val isCompleted: Boolean,
  val title: String,
  val description: String = "",
)
