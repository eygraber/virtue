package com.eygraber.virtue.samples.todo.shared

import com.eygraber.uri.Uri
import com.eygraber.virtue.nav.DisplayableRoute
import kotlinx.serialization.Serializable

sealed interface Routes : DisplayableRoute {
  @Serializable
  data object Home : Routes {
    override val display: String = "/todo"
  }

  sealed interface Details : Routes {
    @Serializable
    data object Create : Details {
      override val display: String = "/todo/create"
    }

    @Serializable
    data class Update(val id: String) : Details {
      override val display: String = "/todo/update?id=$id"
    }
  }

  @Serializable
  data object Settings : Routes {
    override val display: String = "/settings"

    @Serializable
    data object Home : Routes {
      override val display: String = "/settings"
    }

    @Serializable
    data object AboutUs : Routes {
      override val display: String = "/settings/about-us"
    }
  }

  companion object {
    fun fromUri(uri: Uri) = when(uri.path?.removeSuffix("/")) {
      "/todo/create" -> Details.Create
      "/todo/update" -> Details.Update(uri.getQueryParameter("id") ?: "-1")
      "/", "/todo" -> Home
      "/settings/about-us" -> Settings.AboutUs
      "/settings" -> Settings.Home
      else -> Home
    }
  }
}
