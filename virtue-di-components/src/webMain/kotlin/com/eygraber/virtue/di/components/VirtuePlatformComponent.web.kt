package com.eygraber.virtue.di.components

import me.tatarka.inject.annotations.Component

@Component
public actual abstract class VirtuePlatformComponent(
  @Component public val webComponent: VirtueWebPlatformComponent,
) {
  public actual companion object
}
