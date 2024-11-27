package com.eygraber.virtue.session.history

import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.coroutines.flow.Flow

@PublishedApi
internal interface History<VR : VirtueRoute> {
  data class Entry<VR : VirtueRoute>(
    val index: Int,
    val route: VR,
  )

  sealed interface Change {
    data object Empty : Change
    data class Navigate(val range: IntRange) : Change
    data class Pop(val count: Int) : Change
    data class PendingAction(val pending: () -> Unit) : Change
  }

  val currentEntry: Entry<VR>
  val currentEntryFlow: Flow<Entry<VR>>

  val canMoveBack: Boolean
  val canMoveForward: Boolean

  var isIgnoringPlatformChanges: Boolean

  operator fun get(index: Int): Entry<VR>

  fun push(route: VR): Entry<VR>
  suspend fun clearForwardNavigation()
  fun replaceFirst(route: VR)
  fun move(delta: Int)

  fun updateTitle(title: String)

  suspend fun awaitChangeNoOp()
  suspend fun awaitChange(): Change
}
