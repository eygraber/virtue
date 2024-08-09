package com.eygraber.virtue.samples.todo.shared

import com.eygraber.uri.Uri
import com.eygraber.virtue.session.nav.NestedVirtueRoute
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes : VirtueRoute {
  @Serializable
  data object Home : Routes {
    override fun display(): String = "/todo"
  }

  @Serializable
  sealed interface Details : Routes {
    @Serializable
    data object Create : Details {
      override fun display(): String = "/todo/create"
    }

    @Serializable
    data class Update(val id: String) : Details {
      override fun display(): String = "/todo/update?id=$id"
    }
  }

  @Serializable
  data object Settings : Routes {
    override fun display(): String = "/settings"
    override fun up(): VirtueRoute = Routes.Home

    interface Nested : NestedVirtueRoute

    @Serializable
    data object Home : Routes, Nested {
      override fun display(): String = "/settings"
      override fun up(): VirtueRoute = Routes.Home
      override fun parent(): VirtueRoute = Settings
    }

    @Serializable
    data object AboutUs : Routes, Nested {
      override fun display(): String = "/settings/about-us"
      override fun up(): VirtueRoute = Home
      override fun parent(): VirtueRoute = Settings
    }
  }

  companion object {
    fun fromUri(uri: Uri) = when(uri.path?.removeSuffix("/")) {
      "/todo/create" -> Details.Create
      "/todo/update" -> Details.Update(uri.getQueryParameter("id") ?: "-1")
      "/", "/todo" -> Home
      "/settings/about-us" -> Settings.AboutUs
      "/settings" -> Settings.Home
      else -> null
    }
  }
}
