package com.eygraber.virtue.samples.todo.shared.settings

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoDestination
import com.eygraber.virtue.samples.todo.shared.TodoDestinationComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@DestinationSingleton
class SettingsNavigator(
  val onNavigateBack: () -> Unit,
  val onNavigateToAboutUs: () -> Unit,
)

class SettingsDestination(
  onNavigateBack: () -> Unit,
  onNavigateToAboutUs: () -> Unit,
  override val parentComponent: TodoSessionComponent,
) : TodoDestination<Routes.Settings.Home, SettingsIntent, SettingsCompositor, SettingsViewState>() {
  override val view: SettingsView = { state, onIntent -> SettingsView(state, onIntent) }

  override val component = SettingsComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.Settings.Home,
    navigator = SettingsNavigator(
      onNavigateBack = onNavigateBack,
      onNavigateToAboutUs = onNavigateToAboutUs,
    ),
  )
}

@DestinationSingleton
@Component
abstract class SettingsComponent(
  @Component override val parentComponent: TodoSessionComponent,
  override val route: Routes.Settings.Home,
  @get:Provides val navigator: SettingsNavigator,
) : TodoDestinationComponent<Routes.Settings.Home, SettingsIntent, SettingsCompositor, SettingsViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun SettingsComponent.Companion.createKmp(
  parentComponent: TodoSessionComponent,
  route: Routes.Settings.Home,
  navigator: SettingsNavigator,
): SettingsComponent
