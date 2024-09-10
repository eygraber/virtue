package com.eygraber.virtue.samples.auth.shared.logged_in

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.vice.loadable.ViceLoadable
import com.eygraber.vice.loadable.isLoading
import com.eygraber.vice.nav.LocalAnimatedVisibilityScope
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.vice.nav.rememberSharedContentState
import com.eygraber.vice.nav.sharedBounds
import com.eygraber.virtue.samples.auth.shared.AuthPreviewTheme
import com.eygraber.virtue.samples.auth.shared.Res
import com.eygraber.virtue.samples.auth.shared.logged_in_content
import com.eygraber.virtue.samples.auth.shared.logged_in_logout_button
import com.eygraber.virtue.samples.auth.shared.logged_in_title
import org.jetbrains.compose.resources.stringResource

internal typealias LoggedInView = @Composable (LoggedInViewState, (LoggedInIntent) -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun LoggedInView(
  state: LoggedInViewState,
  onIntent: (LoggedInIntent) -> Unit,
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
          Text(
            text = state.strings.value.title,
            modifier = Modifier.placeholder(
              visible = state.strings.isLoading,
            ),
          )
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
      Text(
        text = state.strings.value.content,
        modifier = Modifier.placeholder(
          visible = state.strings.isLoading,
        ),
      )

      Spacer(Modifier.height(8.dp))

      Button(
        onClick = { onIntent(LoggedInIntent.Logout) },
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.error,
          contentColor = MaterialTheme.colorScheme.onError,
        ),
      ) {
        Text(
          text = state.strings.value.logoutButton,
          modifier = Modifier.placeholder(
            visible = state.strings.isLoading,
          ),
        )
      }
    }
  }
}

@Preview
@Composable
private fun LoggedInPreview() {
  AuthPreviewTheme {
    LoggedInView(
      state = LoggedInViewState(
        strings = ViceLoadable.Loaded(
          LoggedInStrings(
            title = stringResource(Res.string.logged_in_title),
            content = stringResource(Res.string.logged_in_content),
            logoutButton = stringResource(Res.string.logged_in_logout_button),
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
