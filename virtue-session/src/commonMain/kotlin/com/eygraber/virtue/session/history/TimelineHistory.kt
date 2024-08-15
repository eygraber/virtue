package com.eygraber.virtue.session.history

import androidx.compose.runtime.saveable.Saver
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

internal class TimelineHistory<VR : VirtueRoute> private constructor(
  timeline: Timeline<VR>,
) : History<VR> {
  private data class Timeline<VR : VirtueRoute>(
    val entries: List<History.Entry<VR>>,
    val current: Int,
  ) : List<History.Entry<VR>> by entries {
    val currentItem get() = entries[current]

    inline fun mutate(mutate: Timeline<VR>.() -> Timeline<VR>): Timeline<VR> = this.mutate()
  }

  constructor(initialRoute: VR) : this(
    Timeline(entries = listOf(History.Entry(0, initialRoute)), current = 0),
  )

  private val timeline = atomic(timeline)

  override val currentEntry: History.Entry<VR> get() = timeline.value.currentItem

  override val canMoveBack: Boolean get() = timeline.value.current > 0
  override val canMoveForward: Boolean get() = timeline.value.current < timeline.value.lastIndex

  override fun get(index: Int): History.Entry<VR> = timeline.value.entries[index]

  override fun push(route: VR): History.Entry<VR> {
    val newTimeline = mutateTimeline {
      val newCurrent = current + 1

      val newEntry = History.Entry(
        index = current + 1,
        route = route,
      )

      if(current == entries.lastIndex) {
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

    return newTimeline.currentItem
  }

  override fun replaceFirst(route: VR) {
    mutateTimeline {
      copy(
        entries = listOf(History.Entry(0, route)),
      )
    }
  }

  override fun move(delta: Int) {
    mutateTimeline {
      when(val moveToIndex = current + delta) {
        in 0..entries.lastIndex -> copy(
          current = moveToIndex,
        )

        else -> this
      }
    }
  }

  override suspend fun awaitChangeNoOp() {}

  override suspend fun awaitChange(): History.Change = suspendCancellableCoroutine {}

  override fun toString(): String = timeline.value.toString()

  private inline fun mutateTimeline(mutate: Timeline<VR>.() -> Timeline<VR>): Timeline<VR> {
    timeline.update { prev ->
      prev.mutate(mutate)
    }

    return timeline.value
  }

  internal companion object {
    @Serializable
    private data class SerializedTimeline(
      val current: Int,
      val n: Int,
      val serializedRoutes: String,
    )

    @OptIn(InternalSerializationApi::class)
    fun <VR : VirtueRoute> Saver(
      routeClass: KClass<VR>,
    ) = Saver<TimelineHistory<VR>, String>(
      save = { history ->
        val timeline = history.timeline.value
        val current = timeline.current
        val n = timeline.entries.size
        val serializedRoutes = Json.encodeToString(
          serializer = ListSerializer(routeClass.serializer()),
          value = timeline.entries.map { it.route },
        )

        Json.encodeToString(
          SerializedTimeline(
            current = current,
            n = n,
            serializedRoutes = serializedRoutes,
          ),
        )
      },
      restore = { encodedTimeline ->
        val serializedTimeline = Json.decodeFromString<SerializedTimeline>(encodedTimeline)
        val routes = Json.decodeFromString(
          ListSerializer(routeClass.serializer()),
          serializedTimeline.serializedRoutes,
        )

        TimelineHistory(
          Timeline(
            entries = List(serializedTimeline.n) { index ->
              History.Entry(
                index = index,
                route = routes[index],
              )
            },
            current = serializedTimeline.current,
          ),
        )
      },
    )
  }
}
