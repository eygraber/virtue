package com.eygraber.virtue.samples.todo.shared

import com.eygraber.uri.Uri
import com.eygraber.virtue.session.VirtueDeepLinkMapper
import me.tatarka.inject.annotations.Inject

@Inject
class TodoDeepLinkMapper : VirtueDeepLinkMapper {
  override fun mapToRoute(deepLink: Uri) = Routes.fromUri(deepLink)
}
