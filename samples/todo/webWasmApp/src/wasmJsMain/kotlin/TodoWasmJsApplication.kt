import com.eygraber.virtue.app.virtueApplication
import com.eygraber.virtue.config.WasmVirtueConfig
import com.eygraber.virtue.samples.todo.shared.Routes
import com.eygraber.virtue.samples.todo.shared.TodoAppComponent
import com.eygraber.virtue.samples.todo.shared.TodoAppInfo
import com.eygraber.virtue.samples.todo.shared.TodoSessionComponent
import com.eygraber.virtue.samples.todo.shared.TodoSessionParams
import com.eygraber.virtue.samples.todo.shared.create

fun main() = virtueApplication(
  appComponentFactory = { virtuePlatformComponent, config ->
    TodoAppComponent.create(
      platformComponent = virtuePlatformComponent,
      config = config,
    )
  },
  sessionComponentFactory = { appComponent, virtuePlatformSessionComponent ->
    TodoSessionComponent.create(
      appComponent = appComponent,
      virtuePlatformSessionComponent = virtuePlatformSessionComponent,
    )
  },
  config = WasmVirtueConfig(
    appInfo = TodoAppInfo,
  ),
  sessionParams = TodoSessionParams,
  initialRouteProvider = { uri ->
    Routes.fromUri(uri) ?: TodoSessionParams.initialRoute
  },
)
