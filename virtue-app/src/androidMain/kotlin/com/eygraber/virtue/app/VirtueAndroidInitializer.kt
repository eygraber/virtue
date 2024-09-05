package com.eygraber.virtue.app

import android.content.Context
import androidx.startup.Initializer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

public class VirtueAndroidInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    val app = requireNotNull(context.applicationContext as? VirtueApplication<*>) {
      "The Application must descend from VirtueApplication"
    }

    with(app.appComponent) {
      @OptIn(DelicateCoroutinesApi::class)
      GlobalScope.launch {
        themeSettings.initialize(
          default = app.defaultThemeSetting,
        )
      }

      initializer.initialize()
    }
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
