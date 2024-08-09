package com.eygraber.virtue.samples.todo.shared.settings

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.vice.nav.rememberSharedContentState
import com.eygraber.vice.nav.sharedBounds
import com.eygraber.vice.nav.sharedElement

internal typealias SettingsView = @Composable (SettingsViewState, (SettingsIntent) -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun SettingsView(
  @Suppress("UNUSED_PARAMETER") state: SettingsViewState,
  onIntent: (SettingsIntent) -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(
        modifier = Modifier.sharedBounds(
          sharedTransitionScope = LocalSharedTransitionScope.current,
          animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
          sharedContentState = rememberSharedContentState("topBar"),
        ),
        title = {
          Text("Settings")
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.sharedElement(
              sharedTransitionScope = LocalSharedTransitionScope.current,
              animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
              state = rememberSharedContentState("backArrow"),
            ),
            onClick = { onIntent(SettingsIntent.Close) },
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
          }
        },
      )
    },
  ) { contentPadding ->
    Column(
      modifier = Modifier
        .consumeWindowInsets(contentPadding)
        .padding(contentPadding)
        .fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("This is the settings page")

      Spacer(Modifier.height(8.dp))

      TextButton(
        onClick = { onIntent(SettingsIntent.NavigateToAboutUs) },
      ) {
        Text("About Us")
      }
    }
  }
}

@Preview
@Composable
private fun SettingsPreview() {
  SettingsView(
    state = SettingsViewState,
    onIntent = {},
  )
}
