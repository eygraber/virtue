package com.eygraber.virtue.di.components

import android.content.Context
import com.eygraber.virtue.android.ActivityContext
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
public actual abstract class VirtuePlatformSessionComponent(
  @get:Provides public val context: @ActivityContext Context,
) {
  public actual companion object
}
