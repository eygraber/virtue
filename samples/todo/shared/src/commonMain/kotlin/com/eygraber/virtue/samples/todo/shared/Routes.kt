package com.eygraber.virtue.samples.todo.shared

import com.eygraber.uri.Uri
import com.eygraber.virtue.session.nav.NestedVirtueRoute
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes : VirtueRoute {
  @Serializable
  data object Home : Routes {
    override fun title(): String = "Todo"
    override fun display(): String = "/todo"
  }

  @Serializable
  sealed interface Details : Routes {
    @Serializable
    data object Create : Details {
      override fun title(): String = "Add Todo Item"
      override fun display(): String = "/todo/create"
    }

    @Serializable
    data class Update(val id: String) : Details {
      override fun title(): String = "Todo Item $id"
      override fun display(): String = "/todo/update?id=$id"
    }
  }

  @Serializable
  data object Settings : Routes {
    override fun display(): String = "/settings"
    override fun up(): VirtueRoute = Routes.Home

    interface Nested : NestedVirtueRoute

    @Serializable
    data object Settings : Routes, Nested {
      override fun title(): String = "Settings"
      override fun display(): String = "/settings"
      override fun up(): VirtueRoute = Home
      override fun parent(): VirtueRoute = Routes.Settings
    }

    @Serializable
    data object AboutUs : Routes, Nested {
      override fun title(): String = "About Us"
      override fun display(): String = "/settings/about-us"
      override fun up(): VirtueRoute = Settings
      override fun parent(): VirtueRoute = Routes.Settings
    }
  }

  companion object {
    fun fromUri(uri: Uri) = when(uri.path?.removeSuffix("/")) {
      "/todo/create" -> Details.Create
      "/todo/update" -> Details.Update(uri.getQueryParameter("id") ?: "-1")
      "/", "/todo" -> Home
      "/settings/about-us" -> Settings.AboutUs
      "/settings" -> Settings.Settings
      else -> null
    }
  }
}
