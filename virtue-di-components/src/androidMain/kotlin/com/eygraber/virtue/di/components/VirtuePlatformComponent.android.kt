package com.eygraber.virtue.di.components

import me.tatarka.inject.annotations.Component

@Component
public actual abstract class VirtuePlatformComponent(
  @Component public val systemServiceComponent: AndroidSystemServiceComponent,
) {
  public actual companion object
}
