package com.eygraber.virtue.samples.todo.shared.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.eygraber.vice.ViceCompositor
import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.storage.kv.EncryptedUserKeyValueStorage
import com.eygraber.virtue.storage.kv.edit
import com.eygraber.virtue.storage.kv.increment
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@DestinationSingleton
@Inject
class AboutUsCompositor(
  private val navigator: AboutUsNavigator,
  private val userStorage: EncryptedUserKeyValueStorage,
) : ViceCompositor<AboutUsIntent, AboutUsViewState> {
  private var isBackHandlerEnabled by mutableStateOf(false)

  @Composable
  override fun composite(): AboutUsViewState {
    val backPressesHandled by remember {
      userStorage.changes.map { userStorage.getInt("count", 0) }
    }.collectAsState(0)

    return AboutUsViewState(
      isBackHandlerEnabled = isBackHandlerEnabled,
      backPressesHandled = backPressesHandled,
    )
  }

  override suspend fun onIntent(intent: AboutUsIntent) {
    when(intent) {
      AboutUsIntent.BackPress -> userStorage.edit {
        increment("count", 1)
      }

      AboutUsIntent.Close -> navigator.onNavigateBack()

      is AboutUsIntent.BackHandlerEnableChange -> isBackHandlerEnabled = intent.isEnabled
    }
  }
}
