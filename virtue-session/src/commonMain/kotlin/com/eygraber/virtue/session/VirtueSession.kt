package com.eygraber.virtue.session

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.virtue.back.press.dispatch.BackHandler
import com.eygraber.virtue.back.press.dispatch.PlatformNavigationHandler
import com.eygraber.virtue.di.scopes.SessionSingleton
import com.eygraber.virtue.session.nav.InternalVirtueNavController
import com.eygraber.virtue.session.nav.VirtueDeepLink
import com.eygraber.virtue.session.nav.VirtueNavController
import com.eygraber.virtue.session.nav.VirtueRoute
import com.eygraber.virtue.session.nav.currentBackstackWithoutGraphs
import com.eygraber.virtue.session.nav.rememberVirtueNavController
import com.eygraber.virtue.theme.ThemeSettings
import com.eygraber.virtue.theme.compose.isApplicationInDarkTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

@SessionSingleton
@Inject
public class VirtueSession(
  private val themeSettings: ThemeSettings,
) {
  public class Params<T : VirtueSessionComponent, VR : VirtueRoute>(
    public val initialRoute: VR,
    public val routeClass: KClass<VR>,
    public val navGraphBuilder: VirtueNavGraphBuilder<T, VR>,
    public val deepLinksFlow: MutableSharedFlow<VirtueDeepLink<VR>> = MutableSharedFlow(),
    public val darkColorScheme: ColorScheme = androidx.compose.material3.darkColorScheme(),
    public val lightColorScheme: ColorScheme = androidx.compose.material3.lightColorScheme(),
    public val navHostParams: VirtueNavHostParams = VirtueNavHostParams(),
  ) {
    @JvmOverloads
    public fun copy(
      initialRoute: VR = this.initialRoute,
      navGraphBuilder: VirtueNavGraphBuilder<T, VR> = this.navGraphBuilder,
      deepLinksFlow: MutableSharedFlow<VirtueDeepLink<VR>> = this.deepLinksFlow,
      darkColorScheme: ColorScheme = this.darkColorScheme,
      lightColorScheme: ColorScheme = this.lightColorScheme,
      navHostParams: VirtueNavHostParams = this.navHostParams,
    ): Params<T, VR> = Params(
      initialRoute = initialRoute,
      navGraphBuilder = navGraphBuilder,
      routeClass = routeClass,
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
  public fun <T : VirtueSessionComponent, VR : VirtueRoute> SessionUi(
    sessionComponent: T,
    params: Params<T, VR>,
  ) {
    val navController = rememberVirtueNavController(params.routeClass, params.initialRoute)

    BackstackChangeEffect(navController)

    SyncPlatformHistory(navController)

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
        navController = navController,
      )

      Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
      ) {
        SharedTransitionLayout {
          CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
          ) {
            VirtueNavHost(
              navController = navController.navController,
              startDestination = params.initialRoute,
              params = params.navHostParams,
            ) {
              with(params.navGraphBuilder) {
                buildGraph(
                  sessionComponent = sessionComponent,
                  initialRoute = params.initialRoute,
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
  private fun BackstackChangeEffect(navController: VirtueNavController<*>) {
    LaunchedEffect(navController) {
      launch {
        navController.currentBackstackWithoutGraphs.collect {
          isBackHandlerEnabled = it.size > 1
        }
      }
    }
  }

  @Composable
  private fun <VR : VirtueRoute> SyncPlatformHistory(
    navController: InternalVirtueNavController<VR>,
  ) {
    LaunchedEffect(navController) {
      launch {
        with(navController) {
          syncWithPlatformHistory()
        }
      }
    }
  }

  @Composable
  private fun <VR : VirtueRoute> HandleDeepLinks(
    navController: VirtueNavController<VR>,
    deepLinksFlow: Flow<VirtueDeepLink<VR>>,
  ) {
    LaunchedEffect(deepLinksFlow) {
      launch {
        deepLinksFlow.collect { deepLink ->
          navController.navigate(
            route = deepLink.route,
            navOptions = deepLink.navOptions,
            navigatorExtras = deepLink.navigatorExtras,
          )
        }
      }
    }
  }

  @Composable
  private fun PlatformNavigation(
    navController: VirtueNavController<*>,
  ) {
    PlatformNavigationHandler(
      onForwardPress = {
        navController.moveForward()
      },
    )
  }
}
