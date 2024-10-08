package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.session.VirtueSessionComponent
import me.tatarka.inject.annotations.Component

@Component
abstract class TodoSessionComponent(
  @Component override val appComponent: TodoAppComponent,
  @Component override val virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
) : VirtueSessionComponent {
  companion object
}
