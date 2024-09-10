package com.eygraber.virtue.samples.auth.shared.root

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.vice.loadable.ViceLoadable
import com.eygraber.vice.loadable.isLoading
import com.eygraber.virtue.samples.auth.shared.AuthPreviewTheme
import com.eygraber.virtue.samples.auth.shared.Res
import com.eygraber.virtue.samples.auth.shared.auth_error_dialog_clear_data
import com.eygraber.virtue.samples.auth.shared.auth_error_dialog_restart
import com.eygraber.virtue.samples.auth.shared.auth_error_dialog_text
import com.eygraber.virtue.samples.auth.shared.auth_error_dialog_title
import org.jetbrains.compose.resources.stringResource

internal typealias RootView = @Composable (RootViewState, (RootIntent) -> Unit) -> Unit

@Composable
internal fun RootView(
  state: RootViewState,
  onIntent: (RootIntent) -> Unit,
) {
  Scaffold { contentPadding ->
    Box(
      modifier = Modifier
        .consumeWindowInsets(contentPadding)
        .padding(contentPadding)
        .fillMaxSize(),
    ) {
      if(state.isErrorDialogShowing) {
        ErrorDialog(
          onRestart = { onIntent(RootIntent.Restart) },
          onClearData = { onIntent(RootIntent.ClearData) },
          strings = state.strings,
        )
      }
    }
  }
}

@Composable
private fun ErrorDialog(
  onRestart: () -> Unit,
  onClearData: () -> Unit,
  strings: ViceLoadable<RootStrings>,
) {
  AlertDialog(
    onDismissRequest = {},
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false,
    ),
    title = {
      Text(
        text = strings.value.errorDialogTitle,
        modifier = Modifier.placeholder(
          visible = strings.isLoading,
        ),
      )
    },
    text = {
      Text(
        text = strings.value.errorDialogText,
        modifier = Modifier.placeholder(
          visible = strings.isLoading,
        ),
      )
    },
    icon = {
      Icon(Icons.Filled.Warning, contentDescription = "Warning")
    },
    confirmButton = {
      Button(
        onClick = onRestart,
      ) {
        Text(
          text = strings.value.errorDialogRestart,
          modifier = Modifier.placeholder(
            visible = strings.isLoading,
          ),
        )
      }
    },
    dismissButton = {
      Button(
        onClick = onClearData,
      ) {
        Text(
          text = strings.value.errorDialogClearData,
          modifier = Modifier.placeholder(
            visible = strings.isLoading,
          ),
        )
      }
    },
  )
}

@Preview
@Composable
private fun TodoListPreview() {
  AuthPreviewTheme {
    RootView(
      state = RootViewState(
        isErrorDialogShowing = false,
        strings = ViceLoadable.Loaded(
          RootStrings(
            errorDialogTitle = stringResource(Res.string.auth_error_dialog_title),
            errorDialogText = stringResource(Res.string.auth_error_dialog_text),
            errorDialogRestart = stringResource(Res.string.auth_error_dialog_restart),
            errorDialogClearData = stringResource(Res.string.auth_error_dialog_clear_data),
          ),
        ),
      ),
      onIntent = {},
    )
  }
}
