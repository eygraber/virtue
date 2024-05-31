package com.eygraber.virtue.session.history

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.eygraber.uri.Url
import com.eygraber.virtue.nav.virtueNavigate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold

internal suspend fun NavController.syncWithHistory(
  history: History,
  urlToRoute: (Url) -> Any,
  debug: Boolean = false,
) {
  val backStackChanges = currentBackStack.map { stackWithGraphs ->
    stackWithGraphs.filterNot { it.destination is NavGraph }
  }.map { stack ->
    stack.map { it.id }
  }

  backStackChanges.withPrevious().collectLatest { (previousBackstack, currentBackstack) ->
    val lastEqualIndex = findLastEqualIndex(previousBackstack, currentBackstack)
    if(debug) println("prev=${previousBackstack.size} new=${currentBackstack.size}, lastEqualIndex=$lastEqualIndex")
    if(history.isEnabled) {
      val newItems = currentBackstack.size - (lastEqualIndex + 1)
      val isPushRequired = previousBackstack.isNotEmpty() && newItems > 0

      val delta = lastEqualIndex + 1 - previousBackstack.size
      if(delta < 0) {
        history.move(delta)

        // need to await the history event because
        // History doesn't seem to like a move followed immediately by a push
        // we need to consume the event anyways even if we're not about to push
        history.awaitChange()
      }

      if(isPushRequired) {
        repeat(newItems) {
          history.push()
        }
      }
    }
    else {
      if(debug) println("History wasn't enabled")
    }

    history.isEnabled = true

    if(debug) println("Awaiting history change")
    when(val change = history.awaitChange()) {
      History.Change.Empty -> if(debug) println("Empty")

      is History.Change.Navigate -> {
        history.isEnabled = false
        change.urlRoutes.forEach { urlRoute ->
          virtueNavigate(urlToRoute(urlRoute))
        }.also {
          if(debug) println("Navigate(${change.urlRoutes.size})")
        }
      }

      is History.Change.Pop -> {
        history.isEnabled = false

        repeat(change.count) {
          popBackStack()
        }.also {
          if(debug) println("Pop(${change.count})")
        }
      }
    }
  }
}

private fun Flow<List<String>>.withPrevious() = runningFold(
  emptyList<String>() to emptyList<String>(),
) { (_, previous), new ->
  previous to new
}

private fun findLastEqualIndex(left: List<String>, right: List<String>): Int {
  var i = 0
  while(i < left.size && i < right.size) {
    if(left[i] != right[i]) {
      return i - 1
    }

    i++
  }

  return i - 1
}
