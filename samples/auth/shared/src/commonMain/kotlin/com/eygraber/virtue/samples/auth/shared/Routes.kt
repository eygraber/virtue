package com.eygraber.virtue.samples.auth.shared

import com.eygraber.uri.Uri
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.serialization.Serializable

@Serializable
sealed interface Routes : VirtueRoute {
  @Serializable
  data class Root(
    private val redirectLink: String? = null,
  ) : Routes {
    override fun title(): String = "Auth"
    override fun display(): String = ""

    val redirect: Routes get() = redirectLink?.let(::fromPath) ?: LoggedIn
  }

  @Serializable
  data object Login : Routes {
    override fun title(): String = "Login"
    override fun display(): String = "/login"
  }

  @Serializable
  data object LoggedIn : Routes {
    override fun title(): String = "Logged In"
    override fun display(): String = "/logged_in"
  }

  @Serializable
  data object Logout : Routes {
    override fun title(): String = "Logout"
    override fun display(): String = "/logout"
  }

  companion object {
    fun fromUri(uri: Uri) = fromPath(uri.path?.removeSuffix("/"))

    private fun fromPath(path: String?) = when(path) {
      "/login" -> Login
      "/logout" -> Logout
      else -> Root(path?.takeIf { it.isNotEmpty() })
    }
  }
}
