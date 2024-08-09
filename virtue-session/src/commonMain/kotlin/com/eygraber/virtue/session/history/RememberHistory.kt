package com.eygraber.virtue.session.history

import androidx.compose.runtime.Composable
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlin.reflect.KClass

@Composable
internal expect fun <VR : VirtueRoute> rememberHistory(
  initialRoute: VR,
  routeClass: KClass<VR>,
): History<VR>
