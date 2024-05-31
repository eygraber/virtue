package com.eygraber.virtue.samples.todo.shared

import com.eygraber.virtue.di.components.VirtuePlatformSessionComponent
import me.tatarka.inject.annotations.KmpComponentCreate

@KmpComponentCreate
expect fun TodoSessionComponent.Companion.createKmp(
  appComponent: TodoAppComponent,
  virtuePlatformSessionComponent: VirtuePlatformSessionComponent,
): TodoSessionComponent
