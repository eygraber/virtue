package com.eygraber.virtue.back.press.dispatch

import androidx.compose.runtime.Composable

@Composable
public expect fun BackHandler(enabled: Boolean, onBackPress: () -> Unit)
