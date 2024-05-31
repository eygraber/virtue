package com.eygraber.virtue.back.press.dispatch

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.onClick
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton
import com.eygraber.virtue.platform.CurrentPlatform
import com.eygraber.virtue.platform.Os
import com.eygraber.virtue.platform.Platform

@OptIn(ExperimentalFoundationApi::class)
@Suppress("ModifierMissing")
@Composable
public actual fun PlatformNavigationHandler(
  onForwardPress: () -> Unit,
) {
  val platform = CurrentPlatform

  if(platform is Platform.Jvm) {
    val backPressDispatcher = checkNotNull(LocalOnBackPressedDispatcher.current) {
      "No OnBackPressedDispatcher was provided via LocalOnBackPressedDispatcher"
    }

    Box(
      modifier = Modifier
        .fillMaxSize()
        .onClick(
          matcher = platform.os.pointerMatcherBack,
          onClick = {
            backPressDispatcher.onBackPressed()
          },
        )
        .onClick(
          matcher = platform.os.pointerMatcherForward,
          onClick = onForwardPress,
        ),
    )
  }
}

@OptIn(ExperimentalFoundationApi::class)
private val Os.pointerMatcherBack get() = PointerMatcher.mouse(PointerButton(if(this == Os.Linux) 5 else 3))

@OptIn(ExperimentalFoundationApi::class)
private val Os.pointerMatcherForward get() = PointerMatcher.mouse(PointerButton(if(this == Os.Linux) 6 else 4))
