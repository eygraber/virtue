package com.eygraber.virtue.session.history

import com.eygraber.virtue.session.nav.VirtueRoute

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
  }

  val currentEntry: Entry<VR>

  val canMoveBack: Boolean
  val canMoveForward: Boolean

  operator fun get(index: Int): Entry<VR>

  fun push(route: VR): Entry<VR>
  fun replaceFirst(route: VR)
  fun move(delta: Int)

  suspend fun awaitChangeNoOp()
  suspend fun awaitChange(): Change
}
