package com.eygraber.virtue.session.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import com.eygraber.virtue.session.nav.VirtueRoute
import kotlin.reflect.KClass

@Composable
internal actual fun <VR : VirtueRoute> rememberHistory(
  initialRoute: VR,
  routeClass: KClass<VR>,
): History<VR> = rememberSaveable(
  saver = TimelineHistory.Saver(routeClass),
) {
  TimelineHistory(initialRoute)
}
