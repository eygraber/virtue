package com.eygraber.virtue.samples.todo.shared.home

import com.eygraber.virtue.samples.todo.shared.TodoItem

data class HomeViewState(
  val items: List<TodoItem>,
)
