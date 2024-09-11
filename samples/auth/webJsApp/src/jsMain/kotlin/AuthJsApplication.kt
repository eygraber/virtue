import com.eygraber.virtue.app.virtueApplication
import com.eygraber.virtue.config.JsVirtueConfig
import com.eygraber.virtue.samples.auth.shared.AuthAppComponent
import com.eygraber.virtue.samples.auth.shared.AuthAppInfo
import com.eygraber.virtue.samples.auth.shared.AuthSessionComponent
import com.eygraber.virtue.samples.auth.shared.AuthSessionParams
import com.eygraber.virtue.samples.auth.shared.Routes
import com.eygraber.virtue.samples.auth.shared.create

fun main() = virtueApplication(
  appComponentFactory = { virtuePlatformComponent, config ->
    AuthAppComponent.create(
      platformComponent = virtuePlatformComponent,
      config = config,
    )
  },
  sessionComponentFactory = { appComponent, virtuePlatformSessionComponent ->
    AuthSessionComponent.create(
      appComponent = appComponent,
      virtuePlatformSessionComponent = virtuePlatformSessionComponent,
    )
  },
  config = JsVirtueConfig(
    appInfo = AuthAppInfo,
  ),
  sessionParams = AuthSessionParams,
  initialRouteProvider = { uri ->
    Routes.fromUri(uri) ?: AuthSessionParams.initialRoute
  },
)
