package com.eygraber.virtue.session.history

import com.eygraber.uri.Url
import kotlinx.serialization.Serializable

@PublishedApi
internal interface History {
  @Serializable
  data class Entry(
    val index: Int,
    val display: String = "",
  )

  sealed interface Change {
    data object Empty : Change
    data class Navigate(val urlRoutes: List<Url>) : Change
    data class Pop(val count: Int) : Change
  }

  var isEnabled: Boolean

  val currentEntry: Entry?

  val canGoBack: Boolean
  val canGoForward: Boolean

  fun initialize()

  fun push(): Entry
  fun updateCurrent(display: String): Entry
  fun move(delta: Int): Change

  suspend fun awaitChange(): Change
}

internal fun History.moveForward(): History.Change = move(1)
