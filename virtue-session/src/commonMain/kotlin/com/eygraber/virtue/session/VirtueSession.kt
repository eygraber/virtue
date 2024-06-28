package com.eygraber.virtue.session

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.eygraber.vice.nav.LocalSharedTransitionScopes
import com.eygraber.vice.nav.SharedTransitionScopes
import com.eygraber.virtue.back.press.dispatch.BackHandler
import com.eygraber.virtue.back.press.dispatch.PlatformNavigationHandler
import com.eygraber.virtue.di.scopes.SessionSingleton
import com.eygraber.virtue.nav.VirtueDeepLink
import com.eygraber.virtue.nav.currentBackstackWithoutGraphs
import com.eygraber.virtue.nav.rememberVirtueNavController
import com.eygraber.virtue.session.history.History
import com.eygraber.virtue.session.history.moveForward
import com.eygraber.virtue.session.history.rememberHistory
import com.eygraber.virtue.session.history.syncWithHistory
import com.eygraber.virtue.theme.ThemeSettings
import com.eygraber.virtue.theme.compose.isApplicationInDarkTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmOverloads

@Composable
public fun VirtueAnimatedContentScope(
  content: @Composable AnimatedContentScope.() -> Unit
) {
  LocalSharedTransitionScopes.current.run {
    val animated = animated
    if(animated != null) {
      with(animated) {
        content()
      }
    }
  }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
public fun Modifier.virtueSharedBounds(
  key: Any,
  enter: EnterTransition = EnterTransition.None,
  exit: ExitTransition = ExitTransition.None,
): Modifier = then(
  LocalSharedTransitionScopes.current.run {
    val shared = shared
    val animated = animated
    if(shared != null && animated != null) {
      with(shared) {
        Modifier.sharedBounds(
          rememberSharedContentState(key),
          animated,
          enter = enter,
          exit = exit,
        )
      }
    }
    else {
      Modifier
    }
  }
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
public fun Modifier.virtueSharedElement(
  key: Any,
): Modifier = then(
  LocalSharedTransitionScopes.current.run {
    val shared = shared
    val animated = animated
    if(shared != null && animated != null) {
      with(shared) {
        Modifier.sharedElement(
          rememberSharedContentState(key),
          animated
        )
      }
    }
    else {
      Modifier
    }
  }
)

@SessionSingleton
@Inject
public class VirtueSession(
  private val themeSettings: ThemeSettings,
  private val deepLinkMapper: VirtueDeepLinkMapper,
) {
  public class Params<T : GenericVirtueSessionComponent>(
    public val startDestination: Any,
    public val navGraphBuilder: VirtueNavGraphBuilder<T>,
    public val deepLinksFlow: MutableSharedFlow<VirtueDeepLink> = MutableSharedFlow(),
    public val darkColorScheme: ColorScheme = androidx.compose.material3.darkColorScheme(),
    public val lightColorScheme: ColorScheme = androidx.compose.material3.lightColorScheme(),
    public val navHostParams: VirtueNavHostParams = VirtueNavHostParams(),
  ) {
    @JvmOverloads
    public fun copy(
      startDestination: Any = this.startDestination,
      navGraphBuilder: VirtueNavGraphBuilder<T> = this.navGraphBuilder,
      deepLinksFlow: MutableSharedFlow<VirtueDeepLink> = this.deepLinksFlow,
      darkColorScheme: ColorScheme = this.darkColorScheme,
      lightColorScheme: ColorScheme = this.lightColorScheme,
      navHostParams: VirtueNavHostParams = this.navHostParams,
    ): Params<T> = Params(
      startDestination = startDestination,
      navGraphBuilder = navGraphBuilder,
      deepLinksFlow = deepLinksFlow,
      darkColorScheme = darkColorScheme,
      lightColorScheme = lightColorScheme,
      navHostParams = navHostParams,
    )
  }

  private var isBackHandlerEnabled by mutableStateOf(false)

  @OptIn(ExperimentalSharedTransitionApi::class)
  @Suppress("ModifierMissing")
  @Composable
  public fun <T : GenericVirtueSessionComponent> SessionUi(
    sessionComponent: T,
    params: Params<T>,
  ) {
    val history = rememberHistory()
    val navController = rememberVirtueNavController()

    HistoryInitEffect(history)
    BackstackChangeEffect(navController)

    SyncHistoryAndBackstackEffect(
      navController,
      history,
    )

    HandleDeepLinks(
      navController,
      params.deepLinksFlow,
    )

    // on Android this should be preempted by the BackHandler in NavHost
    // once there's support for that in CMP we should be able to remove this
    // TODO: Remove VirtueSession BackHandler once CMP supports BackHandler internally
    BackHandler(enabled = isBackHandlerEnabled) {
      isBackHandlerEnabled = navController.popBackStack()
    }

    MaterialTheme(
      colorScheme = when {
        themeSettings.isApplicationInDarkTheme() -> params.darkColorScheme
        else -> params.lightColorScheme
      },
    ) {
      PlatformNavigation(
        history = history,
        navController = navController,
      )

      Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
      ) {
        SharedTransitionLayout {
          CompositionLocalProvider(
            LocalSharedTransitionScopes provides SharedTransitionScopes(
              shared = this,
              animated = null
            )
          ) {
            VirtueNavHost(
              navController = navController,
              startDestination = params.startDestination,
              params = params.navHostParams,
            ) {
              with(params.navGraphBuilder) {
                buildGraph(
                  displayRoute = { history.updateCurrent(it.display) },
                  sessionComponent = sessionComponent,
                  navController = navController,
                )
              }
            }
          }
        }
      }
    }
  }

  @Composable
  private fun HistoryInitEffect(history: History) {
    DisposableEffect(history) {
      history.initialize()

      onDispose {}
    }
  }

  @Composable
  private fun BackstackChangeEffect(navController: NavController) {
    LaunchedEffect(navController) {
      launch {
        navController.currentBackstackWithoutGraphs.collect {
          isBackHandlerEnabled = it.size > 1
        }
      }
    }
  }

  @Composable
  private fun SyncHistoryAndBackstackEffect(
    navController: NavController,
    history: History,
  ) {
    LaunchedEffect(history, navController) {
      launch {
        navController.syncWithHistory(
          history = history,
          deepLinkMapper = deepLinkMapper::mapToRoute,
          debug = true,
        )
      }
    }
  }

  @Composable
  private fun HandleDeepLinks(
    navController: NavController,
    deepLinksFlow: Flow<VirtueDeepLink>,
  ) {
    LaunchedEffect(deepLinksFlow) {
      launch {
        deepLinksFlow.collect { deepLink ->
          deepLinkMapper.mapToRoute(deepLink.uri)?.let { deepLinkUri ->
            navController.navigate(
              route = deepLinkUri,
              navOptions = deepLink.navOptions,
              navigatorExtras = deepLink.navigatorExtras,
            )
          }
        }
      }
    }
  }

  @Composable
  private fun PlatformNavigation(
    history: History,
    navController: NavHostController,
  ) {
    PlatformNavigationHandler(
      onForwardPress = {
        if(history.canGoForward) {
          val change = history.moveForward()
          if(change is History.Change.Navigate) {
            change.urlRoutes.forEach { urlRoute ->
              // disable history so SyncNavAndHistory doesn't get confused
              // it will re-enable it when it is ready
              history.isEnabled = false
              deepLinkMapper.mapToRoute(urlRoute)?.let(navController::navigate)
            }
          }
        }
      },
    )
  }
}
