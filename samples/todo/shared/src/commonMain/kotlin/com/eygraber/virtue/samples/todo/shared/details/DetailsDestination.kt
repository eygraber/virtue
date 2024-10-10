package com.eygraber.virtue.samples.todo.shared.details

import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoDestination
import com.eygraber.virtue.samples.todo.shared.TodoDestinationComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@DestinationSingleton
class DetailsNavigator(
  val onNavigateBack: () -> Unit,
)

class DetailsDestination(
  op: Routes.Details,
  onNavigateBack: () -> Unit,
  override val parentComponent: TodoSessionComponent,
) : TodoDestination<Routes.Details, DetailsIntent, DetailsCompositor, DetailsViewState>() {
  override val view: DetailsView = { state, onIntent -> DetailsView(state, onIntent) }

  override val component = DetailsComponent.createKmp(
    parentComponent = parentComponent,
    route = op,
    navigator = DetailsNavigator(
      onNavigateBack = onNavigateBack,
    ),
  )
}

@DestinationSingleton
@Component
abstract class DetailsComponent(
  @Component override val parentComponent: TodoSessionComponent,
  override val route: Routes.Details,
  @get:Provides val navigator: DetailsNavigator,
) : TodoDestinationComponent<Routes.Details, DetailsIntent, DetailsCompositor, DetailsViewState> {
  companion object
}

@KmpComponentCreate
internal expect fun DetailsComponent.Companion.createKmp(
  parentComponent: TodoSessionComponent,
  route: Routes.Details,
  navigator: DetailsNavigator,
): DetailsComponent
