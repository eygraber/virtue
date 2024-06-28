package com.eygraber.virtue.session.history

import androidx.compose.runtime.saveable.Saver
import com.eygraber.uri.Url
import com.eygraber.virtue.nav.VirtueRoute
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue

internal class TimelineHistory private constructor(
  timeline: Timeline,
) : History {
  @Serializable
  private data class Timeline(
    val entries: List<History.Entry>,
    val current: Int,
  ) : List<History.Entry> by entries {
    val currentItem get() = if(current == -1) null else entries[current]

    inline fun mutate(mutate: Timeline.() -> Timeline): Timeline = this.mutate()
  }

  constructor() : this(
    Timeline(entries = listOf(History.Entry(0)), current = 0),
  )

  private val timeline = atomic(timeline)

  internal val timelineDisplays: List<String> get() = timeline.value.entries.map { it.display }

  override var isEnabled: Boolean by atomic(false)

  override val currentEntry: History.Entry? get() = timeline.value.currentItem

  override val canGoBack: Boolean get() = timeline.value.current > 0
  override val canGoForward: Boolean get() = timeline.value.current < timeline.value.lastIndex

  override fun initialize() {}

  override fun push(): History.Entry {
    val newTimeline = mutateTimeline {
      val newCurrent = current + 1

      val newEntry = History.Entry(
        index = current + 1,
      )

      if(current == -1 || current == entries.lastIndex) {
        copy(
          entries = entries + newEntry,
          current = newCurrent,
        )
      }
      else {
        val newItems = List(newCurrent + 1) { index ->
          if(index < newCurrent) {
            entries[index]
          }
          else {
            newEntry
          }
        }

        copy(
          entries = newItems,
          current = newCurrent,
        )
      }
    }

    return requireNotNull(newTimeline.currentItem) {
      "currentItem shouldn't be able to be null"
    }
  }

  override fun updateCurrent(
    display: String,
  ): History.Entry {
    val updatedTimeline = mutateTimeline {
      println("Timeline - Updating $current with $display")
      copy(
        entries = entries.mapIndexed { index, item ->
          if(index != current) {
            item
          }
          else {
            item.copy(display = display)
          }
        },
      )
    }

    return requireNotNull(updatedTimeline.currentItem) {
      "currentItem shouldn't be able to be null"
    }
  }

  override fun move(delta: Int): History.Change {
    var change: History.Change = History.Change.Empty

    mutateTimeline {
      change = when {
        delta < 0 -> History.Change.Pop(delta.absoluteValue)
        delta > 0 -> History.Change.Navigate(
          timelineDisplays
            .slice(current + 1..current + delta)
            .map { Url.parse("${VirtueRoute.INTERNAL_SCHEME}$it") }
        )
        else -> History.Change.Empty
      }

      when(val moveToIndex = current + delta) {
        in 0..entries.lastIndex -> copy(
          current = moveToIndex,
        )

        else -> this
      }
    }

    return change
  }

  override suspend fun awaitChange(): History.Change = History.Change.Empty

  override fun toString(): String = timeline.value.toString()

  private inline fun mutateTimeline(mutate: Timeline.() -> Timeline): Timeline {
    timeline.update { prev ->
      prev.mutate(mutate)
    }

    return timeline.value
  }

  internal companion object {
    fun Saver() = Saver<TimelineHistory, Any>(
      save = { Json.encodeToString(it.timeline.value) },
      restore = { encodedTimeline ->
        if(encodedTimeline is String) {
          TimelineHistory(
            Json.decodeFromString<Timeline>(encodedTimeline),
          )
        }
        else {
          null
        }
      },
    )
  }
}
