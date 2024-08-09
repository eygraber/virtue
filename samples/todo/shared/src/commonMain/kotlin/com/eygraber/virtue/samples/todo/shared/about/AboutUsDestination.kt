package com.eygraber.virtue.samples.todo.shared.about

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
class AboutUsNavigator(
  val onNavigateBack: () -> Unit,
)

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class AboutUsDestination(
  onNavigateBack: () -> Unit,
  override val parentComponent: TodoSessionComponent,
) : TodoDestination<Routes.Settings.AboutUs, AboutUsIntent, AboutUsCompositor, AboutUsViewState>() {
  override val view: AboutUsView = { state, onIntent -> AboutUsView(state, onIntent) }

  override val component = AboutUsComponent.createKmp(
    parentComponent = parentComponent,
    route = Routes.Settings.AboutUs,
    navigator = AboutUsNavigator(
      onNavigateBack = onNavigateBack,
    ),
  )
}

@DestinationSingleton
@Component
abstract class AboutUsComponent(
  @Component override val parentComponent: TodoSessionComponent,
  override val route: Routes.Settings.AboutUs,
  @get:Provides val navigator: AboutUsNavigator,
) : TodoDestinationComponent<Routes.Settings.AboutUs, AboutUsIntent, AboutUsCompositor, AboutUsViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun AboutUsComponent.Companion.createKmp(
  parentComponent: TodoSessionComponent,
  route: Routes.Settings.AboutUs,
  navigator: AboutUsNavigator,
): AboutUsComponent
