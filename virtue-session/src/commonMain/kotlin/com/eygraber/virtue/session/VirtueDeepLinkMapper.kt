package com.eygraber.virtue.session

import com.eygraber.uri.Uri

public interface VirtueDeepLinkMapper {
  public fun mapToRoute(deepLink: Uri): Any
}
