package com.eygraber.virtue.di.components

import kotlinx.browser.window
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.w3c.dom.History
import org.w3c.dom.Location
import org.w3c.dom.Window
import org.w3c.dom.WindowLocalStorage
import org.w3c.dom.WindowSessionStorage

@Component
public actual abstract class VirtueWebPlatformComponent {
  @get:Provides public val browserHistory: History get() = browserWindow.history
  @get:Provides public val browserLocalStorage: WindowLocalStorage get() = browserWindow
  @get:Provides public val browserLocation: Location get() = browserWindow.location
  @get:Provides public val browserSessionStorage: WindowSessionStorage get() = browserWindow
  @get:Provides public val browserWindow: Window get() = window

  public actual companion object
}
