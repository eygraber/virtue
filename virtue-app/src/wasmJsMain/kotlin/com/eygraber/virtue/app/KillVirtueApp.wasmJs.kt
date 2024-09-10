package com.eygraber.virtue.app

import kotlinx.browser.window

public actual fun killVirtueApp() {
  window.location.reload()
}
