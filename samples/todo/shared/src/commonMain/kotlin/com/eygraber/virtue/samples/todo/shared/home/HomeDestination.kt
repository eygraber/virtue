package com.eygraber.virtue.samples.todo.shared.home

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoDestination
import com.eygraber.virtue.samples.todo.shared.TodoDestinationComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@DestinationSingleton
class HomeNavigator(
  val onNavigateToCreateItem: () -> Unit,
  val onNavigateToUpdateItem: (String) -> Unit,
  val onNavigateToSettings: () -> Unit,
)

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class HomeDestination(
  onNavigateToCreateItem: () -> Unit,
  onNavigateToUpdateItem: (String) -> Unit,
  onNavigateToSettings: () -> Unit,
  override val parentComponent: TodoSessionComponent,
) : TodoDestination<Routes.Home, HomeIntent, HomeCompositor, HomeViewState>() {
  override val view: HomeView = { state, onIntent -> HomeView(state, onIntent) }

  override val component = HomeComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.Home,
    navigator = HomeNavigator(
      onNavigateToCreateItem = onNavigateToCreateItem,
      onNavigateToUpdateItem = onNavigateToUpdateItem,
      onNavigateToSettings = onNavigateToSettings,
    ),
  )
}

@DestinationSingleton
@Component
abstract class HomeComponent(
  @Component override val parentComponent: TodoSessionComponent,
  override val route: Routes.Home,
  @get:Provides val navigator: HomeNavigator,
) : TodoDestinationComponent<Routes.Home, HomeIntent, HomeCompositor, HomeViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun HomeComponent.Companion.createKmp(
  parentComponent: TodoSessionComponent,
  route: Routes.Home,
  navigator: HomeNavigator,
): HomeComponent
