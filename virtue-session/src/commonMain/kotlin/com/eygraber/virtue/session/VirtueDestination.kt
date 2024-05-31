package com.eygraber.virtue.session

import com.eygraber.vice.ViceCompositor
import com.eygraber.vice.ViceEffects
import com.eygraber.vice.nav.ViceDestination
import me.tatarka.inject.annotations.Provides

public abstract class VirtueDestination<R, I, C, E, S, out ParentComponent> : ViceDestination<I, C, E, S>()
  where C : ViceCompositor<I, S>,
        E : ViceEffects {
  protected abstract val parentComponent: ParentComponent
  protected abstract val component: VirtueDestinationComponent<R, I, C, E, S, ParentComponent>

  final override val compositor: C by lazy { component.compositor }
  final override val effects: E by lazy { component.effects }
}

public interface VirtueDestinationComponent<R, I, C, E, S, out ParentComponent>
  where C : ViceCompositor<I, S>, E : ViceEffects {
  public val parentComponent: ParentComponent

  public val compositor: C
  public val effects: E

  @get:Provides public val route: R
}
