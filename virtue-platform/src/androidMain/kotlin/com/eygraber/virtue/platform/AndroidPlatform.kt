package com.eygraber.virtue.platform

import android.os.Build

public actual val CurrentPlatform: Platform =
  Platform.Android(
    sdkVersion = Build.VERSION.SDK_INT,
  )
