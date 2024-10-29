package com.eygraber.virtue.samples.auth.shared

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.eygraber.vice.loadable.ViceLoadable
import com.eygraber.vice.nav.LocalSharedTransitionScope
import com.eygraber.virtue.samples.auth.shared.login.LoginStrings
import com.eygraber.virtue.samples.auth.shared.login.LoginView
import com.eygraber.virtue.samples.auth.shared.login.LoginViewState
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class OnboardingPhoneNumberScreenshotTest {
  @get:Rule
  val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL,
  )

  @TestParameter
  private var isDarkMode: Boolean = false

  @OptIn(ExperimentalSharedTransitionApi::class)
  @Test
  fun screenshot() {
    paparazzi.snapshot {
      SharedTransitionLayout {
        CompositionLocalProvider(
          LocalSharedTransitionScope provides this
        ) {
          AuthPreviewTheme(
            colorScheme = if(isDarkMode) AuthTheme.darkColorScheme else AuthTheme.lightColorScheme,
          ) {
            LoginView(
              state = LoginViewState(
                token = rememberTextFieldState(),
                isTokenEnabled = true,
                isLoginButtonEnabled = true,
                strings = ViceLoadable.Loaded(
                  LoginStrings(
                    title = "Title",
                    tokenLabel = "Token",
                    button = "Button",
                  )
                ),
              ),
              onIntent = {}
            )
          }
        }
      }
    }
  }
}
