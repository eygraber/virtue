package com.eygraber.virtue.samples.todo.shared.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.todo.shared.TodoRepo
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class HomeCompositor(
  private val repo: TodoRepo,
  private val navigator: HomeNavigator,
) : ViceCompositor<HomeIntent, HomeViewState> {
  @Composable
  override fun composite() = HomeViewState(
    items = repo.items.collectAsState().value,
  )

  override suspend fun onIntent(intent: HomeIntent) {
    when(intent) {
      HomeIntent.AddItem -> navigator.onNavigateToCreateItem()
      is HomeIntent.ToggleItemCompletion -> repo.updateItem(
        newItem = intent.item.copy(
          isCompleted = !intent.item.isCompleted,
        ),
      )

      is HomeIntent.NavigateToDetails -> navigator.onNavigateToUpdateItem(intent.id)
      HomeIntent.NavigateToSettings -> navigator.onNavigateToSettings()
    }
  }
}
