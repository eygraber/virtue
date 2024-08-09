package com.eygraber.virtue.session.nav

public interface VirtueRoute {
  public fun display(): String

  public fun up(): VirtueRoute? = null
}

public interface NestedVirtueRoute {
  public fun parent(): VirtueRoute
}

public fun <VR : VirtueRoute> VirtueRoute.upRoutes(): List<VR> = buildList {
  fun VirtueRoute.typed(): VR? =
    runCatching {
      @Suppress("UNCHECKED_CAST")
      this as? VR
    }.getOrElse {
      error(
        "Can't navigate up from ${this@upRoutes} because its upRoute $this doesn't match its VirtueRoute type",
      )
    }

  var r = up()?.typed()

  while(r != null) {
    add(r)
    r = r.up()?.typed()
  }
}
