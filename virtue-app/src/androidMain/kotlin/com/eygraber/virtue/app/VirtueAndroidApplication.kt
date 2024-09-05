package com.eygraber.virtue.app

import android.app.Application
import com.eygraber.virtue.config.AndroidVirtueConfig
import com.eygraber.virtue.di.components.AndroidComponent
import com.eygraber.virtue.di.components.AndroidSystemServiceComponent
import com.eygraber.virtue.di.components.VirtueAppComponent
import com.eygraber.virtue.di.components.VirtuePlatformComponent
import com.eygraber.virtue.di.components.create

public abstract class VirtueAndroidApplication<A : VirtueAppComponent> : Application(), VirtueApplication<A> {
  private val androidComponent: AndroidComponent by lazy(LazyThreadSafetyMode.NONE) {
    AndroidComponent.create(applicationContext)
  }

  private val androidSystemServiceComponent: AndroidSystemServiceComponent by lazy(LazyThreadSafetyMode.NONE) {
    AndroidSystemServiceComponent.create(androidComponent)
  }

  final override val virtuePlatformComponent: VirtuePlatformComponent by lazy(LazyThreadSafetyMode.NONE) {
    VirtuePlatformComponent.create(
      systemServiceComponent = androidSystemServiceComponent,
    )
  }

  abstract override val config: AndroidVirtueConfig

  final override val appComponent: A by lazy(LazyThreadSafetyMode.NONE) {
    createAppComponent(
      virtuePlatformComponent = virtuePlatformComponent,
    )
  }

  protected abstract fun createAppComponent(
    virtuePlatformComponent: VirtuePlatformComponent,
  ): A

  // this gets called once per process
  // anything that shouldn't be done more than once should go in VirtueAndroidInitializer.initialize
  @Suppress("RedundantOverride")
  override fun onCreate() {
    super.onCreate()
  }
}
