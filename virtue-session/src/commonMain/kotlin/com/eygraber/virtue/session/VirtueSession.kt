package com.eygraber.virtue.session

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.jvm.JvmOverloads
import kotlin.reflect.KClass

public typealias VirtueSessionTheme = @Composable (
  isApplicationInDarkMode: Boolean,
  content: @Composable () -> Unit,
) -> Unit

@SessionSingleton
@Inject
public class VirtueSession {
  public class Params<T : VirtueSessionComponent, VR : VirtueRoute>(
    public val initialRoute: VR,
    public val routeClass: KClass<VR>,
    public val navGraphBuilder: VirtueNavGraphBuilder<T, VR>,
    public val deepLinksFlow: MutableSharedFlow<VirtueDeepLink<VR>> = MutableSharedFlow(),
    public val theme: VirtueSessionTheme = { isApplicationInDarkMode, content ->
      VirtueMaterialTheme(isApplicationInDarkMode, content)
    },
    public val navHostParams: VirtueNavHostParams = VirtueNavHostParams(),
  ) {
    @JvmOverloads
    public fun copy(
      initialRoute: VR = this.initialRoute,
      navGraphBuilder: VirtueNavGraphBuilder<T, VR> = this.navGraphBuilder,
      deepLinksFlow: MutableSharedFlow<VirtueDeepLink<VR>> = this.deepLinksFlow,
      theme: VirtueSessionTheme = this.theme,
      navHostParams: VirtueNavHostParams = this.navHostParams,
    ): Params<T, VR> = Params(
      initialRoute = initialRoute,
      navGraphBuilder = navGraphBuilder,
      routeClass = routeClass,
      deepLinksFlow = deepLinksFlow,
      theme = theme,
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
    isApplicationInDarkTheme: Boolean,
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

    params.theme(isApplicationInDarkTheme) {
      PlatformNavigation(
        navController = navController,
      )

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

@Composable
private fun VirtueMaterialTheme(
  isApplicationInDarkMode: Boolean,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = when {
      isApplicationInDarkMode -> darkColorScheme()
      else -> lightColorScheme()
    },
    content = content,
  )
}
