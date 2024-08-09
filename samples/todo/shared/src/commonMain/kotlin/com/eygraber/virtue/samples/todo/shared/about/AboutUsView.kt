package com.eygraber.virtue.samples.todo.shared.about

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.eygraber.virtue.back.press.dispatch.BackHandler

typealias AboutUsView = @Composable (AboutUsViewState, (AboutUsIntent) -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun AboutUsView(
  state: AboutUsViewState,
  onIntent: (AboutUsIntent) -> Unit,
) {
  BackHandler(enabled = state.isBackHandlerEnabled) {
    onIntent(AboutUsIntent.BackPress)
  }

  Scaffold(
    topBar = {
      TopAppBar(
        modifier = Modifier.sharedBounds(
          sharedTransitionScope = LocalSharedTransitionScope.current,
          animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
          sharedContentState = rememberSharedContentState("topBar"),
        ),
        title = {
          Text("About Us")
        },
        navigationIcon = {
          IconButton(
            modifier = Modifier.sharedElement(
              sharedTransitionScope = LocalSharedTransitionScope.current,
              animatedVisibilityScope = LocalAnimatedVisibilityScope.current,
              state = rememberSharedContentState("backArrow"),
            ),
            onClick = { onIntent(AboutUsIntent.Close) },
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
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("This is the about us page")

      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Checkbox(
          checked = state.isBackHandlerEnabled,
          onCheckedChange = { checked ->
            onIntent(AboutUsIntent.BackHandlerEnableChange(checked))
          },
        )

        Text(
          text = "Enable Back Handling",
          modifier = Modifier.padding(start = 4.dp),
        )
      }

      Text("${state.backPressesHandled} back presses handled")
    }
  }
}

@Preview
@Composable
private fun AboutUsPreview() {
  AboutUsView(
    state = AboutUsViewState(
      isBackHandlerEnabled = false,
      backPressesHandled = 0,
    ),
    onIntent = {},
  )
}
