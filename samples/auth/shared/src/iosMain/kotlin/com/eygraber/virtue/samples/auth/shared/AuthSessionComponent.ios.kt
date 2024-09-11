package com.eygraber.virtue.samples.auth.shared

import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import me.tatarka.inject.annotations.KmpComponentCreate

@KmpComponentCreate
expect fun AuthSessionComponent.Companion.createKmp(
  appComponent: AuthAppComponent,
  virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
): AuthSessionComponent
