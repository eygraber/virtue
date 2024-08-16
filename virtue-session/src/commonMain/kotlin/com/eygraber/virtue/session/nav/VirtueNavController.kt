package com.eygraber.virtue.session.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.eygraber.virtue.platform.CurrentPlatform
import com.eygraber.virtue.platform.Platform
import com.eygraber.virtue.session.history.History
import com.eygraber.virtue.session.history.rememberHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

@Composable
internal fun <VR : VirtueRoute> rememberVirtueNavController(
  routeClass: KClass<VR>,
  initialRoute: VR,
  vararg navigators: Navigator<out NavDestination>,
): InternalVirtueNavController<VR> {
  val navController = rememberNavController(*navigators)
  val history = rememberHistory(initialRoute, routeClass)
  val scope = rememberCoroutineScope()
  return remember {
    VirtueNavControllerImpl(
      navController = navController,
      history = history,
      scope = scope,
    )
  }
}

/**
 * Gets the current navigation back stack entry as a [MutableState]. When the given navController
 * changes the back stack due to a [VirtueNavController.navigate] or [VirtueNavController.popBackStack] this
 * will trigger a recompose and return the top entry on the back stack.
 *
 * @return a mutable state of the current back stack entry
 */
@Composable
public fun <VR : VirtueRoute> VirtueNavController<VR>.currentBackStackEntryAsState(): State<NavBackStackEntry?> =
  (this as InternalVirtueNavController).navController.currentBackStackEntryAsState()

public interface VirtueNavController<in VR : VirtueRoute> {
  public val visibleEntries: StateFlow<List<NavBackStackEntry>>
  public val currentBackStack: StateFlow<List<NavBackStackEntry>>
  public val currentBackStackEntry: NavBackStackEntry?
  public val currentBackStackEntryFlow: Flow<NavBackStackEntry>
  public val previousBackStackEntry: NavBackStackEntry?

  public val canGoBack: Boolean
  public val canGoForward: Boolean

  public fun navigate(route: VR)

  public fun navigate(route: VR, builder: NavOptionsBuilder.() -> Unit)

  public fun navigate(
    route: VR,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
  )

  public fun navigateUp(): Boolean

  public fun moveForward(): Boolean

  public fun popBackStack(): Boolean

  public fun popBackStack(
    route: VR,
    inclusive: Boolean,
    saveState: Boolean = false,
  ): Boolean

  public fun clearBackStack(route: VR): Boolean

  public fun getBackStackEntry(route: VR): NavBackStackEntry
}

internal interface InternalVirtueNavController<VR : VirtueRoute> : VirtueNavController<VR> {
  val navController: NavHostController

  fun CoroutineScope.syncWithPlatformHistory()
}

public val VirtueNavController<*>.currentBackstackWithoutGraphs: Flow<List<NavBackStackEntry>>
  get() = currentBackStack.map { stackWithGraphs ->
    stackWithGraphs.filterNot { it.destination is NavGraph }
  }

public inline fun <reified VR : VirtueRoute> VirtueNavController<VR>.popBackStack(
  inclusive: Boolean,
  saveState: Boolean = false,
): Boolean = when(this) {
  is VirtueNavControllerImpl<VR> -> popBackStack<VR>(inclusive = inclusive, saveState = saveState)
  else -> false
}

public inline fun <reified VR : VirtueRoute> VirtueNavController<VR>.clearBackStack(): Boolean = when(this) {
  is VirtueNavControllerImpl<VR> -> clearBackStack<VR>()
  else -> false
}

public inline fun <reified VR : VirtueRoute> VirtueNavController<VR>.getBackStackEntry(): NavBackStackEntry =
  when(this) {
    is VirtueNavControllerImpl<VR> -> getBackStackEntry<VR>()
    else -> error("No underlying NavController")
  }

@PublishedApi
internal class VirtueNavControllerImpl<VR : VirtueRoute>(
  override val navController: NavHostController,
  @PublishedApi internal val history: History<VR>,
  @PublishedApi internal val scope: CoroutineScope,
) : InternalVirtueNavController<VR> {
  override val visibleEntries: StateFlow<List<NavBackStackEntry>>
    get() = navController.visibleEntries

  override val currentBackStack: StateFlow<List<NavBackStackEntry>>
    get() = navController.currentBackStack

  override val currentBackStackEntry: NavBackStackEntry?
    get() = navController.currentBackStackEntry

  override val currentBackStackEntryFlow: Flow<NavBackStackEntry>
    get() = navController.currentBackStackEntryFlow

  override val previousBackStackEntry: NavBackStackEntry?
    get() = navController.previousBackStackEntry

  override val canGoBack: Boolean
    get() = history.canMoveBack

  override val canGoForward: Boolean
    get() = history.canMoveForward

  override fun navigate(route: VR) {
    syncWithHistory(pushedRoute = route) {
      navController.navigate(route)
    }
  }

  override fun navigate(route: VR, builder: NavOptionsBuilder.() -> Unit) {
    navigate(route, navOptions(builder))
  }

  override fun navigate(route: VR, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?) {
    syncWithHistory(pushedRoute = route, navOptions = navOptions) {
      navController.navigate(route, navOptions, navigatorExtras)
    }
  }

  override fun navigateUp(): Boolean {
    val currentIndex = history.currentEntry.index
    val currentRoute = history.currentEntry.route

    val previousRoute = (currentIndex - 1).takeIf { it >= 0 }?.let { history[it].route }
    val upRoutes = currentRoute.upRoutes<VR>()
    val upRoute = upRoutes.firstOrNull()

    return if(previousRoute != null && upRoute == previousRoute) {
      popBackStack()
    }
    else if(upRoute != null) {
      when(CurrentPlatform) {
        is Platform.Web.Js, is Platform.Jvm, is Platform.Web.Wasm -> {
          navigate(upRoute)
          true
        }

        is Platform.Android, is Platform.Ios -> {
          popBackStack(route = history[0].route, inclusive = false)
          when(history[0].route) {
            upRoutes.last() -> upRoutes.dropLast(1)
            else -> upRoutes
          }.reversed().forEach { route ->
            navigate(route)
          }
          true
        }
      }
    }
    else {
      false
    }
  }

  override fun moveForward(): Boolean =
    history.canMoveForward.also { canMoveForward ->
      if(canMoveForward) {
        history.isIgnoringPlatformChanges = true
        val currentIndex = history.currentEntry.index
        navController.navigate(history[currentIndex + 1].route)
        history.move(1)
      }
    }

  override fun popBackStack(): Boolean =
    syncWithHistory {
      navController.popBackStack()
    }

  override fun popBackStack(route: VR, inclusive: Boolean, saveState: Boolean): Boolean =
    syncWithHistory {
      navController.popBackStack(route, inclusive = inclusive, saveState = saveState)
    }

  override fun clearBackStack(route: VR): Boolean =
    navController.clearBackStack(route)

  override fun getBackStackEntry(route: VR): NavBackStackEntry =
    navController.getBackStackEntry(route)

  override fun CoroutineScope.syncWithPlatformHistory() {
    launch {
      while(isActive) {
        when(val change = history.awaitChange()) {
          is History.Change.Navigate ->
            change.range.map { history[it].route }.forEach { route ->
              navController.navigate(route)
            }

          is History.Change.Pop ->
            repeat(change.count.absoluteValue) {
              navController.popBackStack()
            }

          is History.Change.PendingAction -> launch {
            change.pending()
          }

          History.Change.Empty -> {}
        }
      }
    }

    // this is here out of convenience, not because it belongs here
    launch {
      history.currentEntryFlow.collectLatest { currentEntry ->
        currentEntry.route.title()?.let(history::updateTitle)
      }
    }
  }

  @PublishedApi
  internal inline fun <reified VR : VirtueRoute> popBackStack(inclusive: Boolean, saveState: Boolean): Boolean =
    syncWithHistory {
      navController.popBackStack<VR>(inclusive = inclusive, saveState = saveState)
    }

  @PublishedApi
  internal inline fun <reified VR : VirtueRoute> clearBackStack(): Boolean =
    navController.clearBackStack<VR>()

  @PublishedApi
  internal inline fun <reified VR : VirtueRoute> getBackStackEntry(): NavBackStackEntry =
    navController.getBackStackEntry<VR>()

  @PublishedApi
  internal inline fun <R> syncWithHistory(
    pushedRoute: VR? = null,
    navOptions: NavOptions? = null,
    op: () -> R,
  ): R {
    val isSingleTop = navOptions?.shouldLaunchSingleTop() == true
    val before = navController.currentBackstackWithoutGraphs
    val ret = op()
    val after = navController.currentBackstackWithoutGraphs
    val lastEqualIndex = findLastEqualIndex(before, after)

    val delta = lastEqualIndex + 1 - before.size
    if(delta == -1 && !history.canMoveBack && pushedRoute != null) {
      history.replaceFirst(pushedRoute)
    }
    else {
      if(delta < 0) {
        history.isIgnoringPlatformChanges = true
        history.move(delta)
      }

      if(pushedRoute != null) {
        scope.launch {
          // need to await the history event because History
          // doesn't seem to like a move followed immediately by a push
          if(delta < 0) {
            history.awaitChangeNoOp()
          }

          val isPushRequired = !isSingleTop || history.currentEntry.route != pushedRoute
          if(isPushRequired) {
            history.push(pushedRoute)
          }
          else {
            history.clearForwardNavigation()
          }
        }
      }
    }

    return ret
  }
}

@PublishedApi internal fun findLastEqualIndex(left: List<NavBackStackEntry>, right: List<NavBackStackEntry>): Int {
  var i = 0
  while(i < left.size && i < right.size) {
    if(left[i].id != right[i].id) {
      return i - 1
    }

    i++
  }

  return i - 1
}

public val NavController.currentBackstackWithoutGraphs: List<NavBackStackEntry>
  get() = currentBackStack.value.filterNot { it.destination is NavGraph }
