package com.eygraber.virtue.di.components

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Looper
import com.eygraber.virtue.android.AppContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
public abstract class AndroidComponent(
  @get:Provides public val context: @AppContext Context,
) {
  @Provides public fun assetManager(): AssetManager = context.assets
  @Provides public fun contentResolver(): ContentResolver = context.contentResolver
  @Provides public fun mainLooper(): Looper = context.mainLooper
  @Provides public fun packageManager(): PackageManager = context.packageManager
  @Provides public fun resources(): Resources = context.resources
  @Provides public fun sharedPreferences(@Assisted name: String): SharedPreferences =
    context.getSharedPreferences(
      name,
      Context.MODE_PRIVATE,
    )

  public companion object
}
