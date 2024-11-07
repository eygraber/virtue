package com.eygraber.virtue.samples.auth.android

import com.eygraber.uri.Uri
import com.eygraber.virtue.app.VirtueActivity
import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionParams
import com.eygraber.virtue.samples.auth.shared.Routes
import com.eygraber.virtue.samples.auth.shared.create
import com.eygraber.virtue.session.nav.VirtueDeepLink

class AuthAndroidActivity : VirtueActivity<AuthAppComponent, AuthSessionComponent, Routes>() {
  override val sessionParams = AuthSessionParams

  override fun createDeepLink(deepLink: Uri) = VirtueDeepLink(Routes.fromUri(deepLink))

  override fun createSessionComponent(
    appComponent: AuthAppComponent,
    virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
  ) = AuthSessionComponent.create(
    appComponent = appComponent,
    virtuePlatformSessionComponent = virtuePlatformSessionComponent,
  )
}
