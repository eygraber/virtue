package com.eygraber.virtue.samples.auth.shared.logout

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.vice.loadable.ViceLoadable
import com.eygraber.vice.loadable.isLoading
import com.eygraber.virtue.samples.auth.shared.AuthPreviewTheme
import com.eygraber.virtue.samples.auth.shared.Res
import com.eygraber.virtue.samples.auth.shared.logout_title
import org.jetbrains.compose.resources.stringResource

internal typealias LogoutView = @Composable (LogoutViewState, (LogoutIntent) -> Unit) -> Unit

@Composable
internal fun LogoutView(
  state: LogoutViewState,
  @Suppress("UNUSED_PARAMETER") onIntent: (LogoutIntent) -> Unit = {},
) {
  Surface(
    modifier = Modifier.fillMaxSize(),
  ) {
    Box(
      contentAlignment = Alignment.Center,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = state.strings.value.title,
          modifier = Modifier.placeholder(
            visible = state.strings.isLoading,
          ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        CircularProgressIndicator(
          modifier = Modifier.size(24.dp),
        )
      }
    }
  }
}

@Preview
@Composable
private fun LogoutViewPreview() {
  AuthPreviewTheme {
    LogoutView(
      LogoutViewState(
        strings = ViceLoadable.Loaded(
          LogoutStrings(
            title = stringResource(Res.string.logout_title),
          ),
        ),
      ),
    )
  }
}
