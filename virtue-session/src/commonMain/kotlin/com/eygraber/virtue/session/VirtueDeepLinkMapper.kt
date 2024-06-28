package com.eygraber.virtue.session

import com.eygraber.uri.Uri
import com.eygraber.virtue.nav.VirtueRoute

public interface VirtueDeepLinkMapper {
  public fun mapToRoute(deepLink: Uri): VirtueRoute?
}
