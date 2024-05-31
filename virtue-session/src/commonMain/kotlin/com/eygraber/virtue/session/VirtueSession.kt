package com.eygraber.virtue.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.eygraber.uri.Url
import com.eygraber.virtue.back.press.dispatch.BackHandler
import com.eygraber.virtue.back.press.dispatch.PlatformNavigationHandler
import com.eygraber.virtue.di.scopes.SessionSingleton
import com.eygraber.virtue.nav.VirtueDeepLink
import com.eygraber.virtue.nav.rememberVirtueNavController
import com.eygraber.virtue.nav.virtueNavigate
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
      deepLinkMapper::mapToRoute,
    )

    HandleDeepLinks(
      navController,
      deepLinkMapper,
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
      Box {
        PlatformNavigation(history)

        Box(
          modifier = Modifier.fillMaxSize(),
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
        navController.currentBackStack.collect {
          // need to account for the root graph in the size
          isBackHandlerEnabled = it.size > 2
        }
      }
    }
  }

  @Composable
  private fun SyncHistoryAndBackstackEffect(
    navController: NavController,
    history: History,
    deepLinkToRoute: (Url) -> Any,
  ) {
    val updatedUrlToRoute by rememberUpdatedState(deepLinkToRoute)
    LaunchedEffect(history, navController) {
      launch {
        navController.syncWithHistory(
          history = history,
          urlToRoute = updatedUrlToRoute,
          debug = true,
        )
      }
    }
  }

  @Composable
  private fun HandleDeepLinks(
    navController: NavController,
    deepLinkMapper: VirtueDeepLinkMapper,
    deepLinksFlow: Flow<VirtueDeepLink>,
  ) {
    LaunchedEffect(deepLinksFlow) {
      launch {
        deepLinksFlow.collect { deepLink ->
          navController.virtueNavigate(
            route = deepLinkMapper.mapToRoute(deepLink.uri),
            navOptions = deepLink.navOptions,
            navigatorExtras = deepLink.navigatorExtras,
          )
        }
      }
    }
  }

  @Composable
  private fun PlatformNavigation(history: History) {
    PlatformNavigationHandler(
      onForwardPress = {
        if(history.canGoForward) {
          history.moveForward()
        }
      },
    )
  }
}
