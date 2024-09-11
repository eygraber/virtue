package com.eygraber.virtue.samples.auth.shared

import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.session.VirtueSessionComponent
import me.tatarka.inject.annotations.Component

@Component
abstract class AuthSessionComponent(
  @Component override val appComponent: AuthAppComponent,
  @Component override val virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
) : VirtueSessionComponent {
  companion object
}
