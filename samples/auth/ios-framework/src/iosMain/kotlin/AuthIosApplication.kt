@file:Suppress("MissingPackageDeclaration")

import com.eygraber.virtue.app.VirtueApplication
import com.eygraber.virtue.config.IosVirtueConfig
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppInfo
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionParams
import com.eygraber.virtue.samples.auth.shared.Routes
import com.eygraber.virtue.samples.auth.shared.createKmp

class AuthIosApplication : VirtueApplication<AuthAppComponent, AuthSessionComponent, Routes>(
  config = IosVirtueConfig(
    appInfo = AuthAppInfo,
  ),
) {
  override fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
    config: IosVirtueConfig,
  ) = AuthAppComponent.createKmp(
    platformComponent = virtuePlatformComponent,
    config = config,
  )

  override fun createSessionComponent(
    appComponent: AuthAppComponent,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ) = AuthSessionComponent.createKmp(
    appComponent = appComponent,
    virtuePlatformSessionComponent = virtuePlatformSessionComponent,
  )

  override val sessionParams = AuthSessionParams

  override fun createViewController() = createVirtueViewController()
}
