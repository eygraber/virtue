package com.eygraber.virtue.samples.auth.shared.login

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.eygraber.virtue.samples.auth.shared.login_button
import com.eygraber.virtue.samples.auth.shared.login_title
import com.eygraber.virtue.samples.auth.shared.login_token_label
import org.jetbrains.compose.resources.stringResource

typealias LoginView = @Composable (LoginViewState, (LoginIntent) -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun LoginView(
  state: LoginViewState,
  onIntent: (LoginIntent) -> Unit,
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
      verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = state.strings.value.tokenLabel,
        modifier = Modifier.placeholder(
          visible = state.strings.isLoading,
        ),
      )

      BasicSecureTextField(
        state = state.token,
        enabled = state.isTokenEnabled,
        decorator = { inner ->
          Card {
            inner()
          }
        },
      )

      Button(
        onClick = { onIntent(LoginIntent.Login(state.token)) },
        enabled = state.isLoginButtonEnabled,
      ) {
        Text(
          text = state.strings.value.button,
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
private fun LoginPreview() {
  AuthPreviewTheme {
    LoginView(
      state = LoginViewState(
        token = rememberTextFieldState(initialText = "super secret!"),
        isTokenEnabled = false,
        isLoginButtonEnabled = true,
        strings = ViceLoadable.Loaded(
          LoginStrings(
            title = stringResource(Res.string.login_title),
            tokenLabel = stringResource(Res.string.login_token_label),
            button = stringResource(Res.string.login_button),
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
