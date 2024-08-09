package com.eygraber.virtue.samples.todo.shared

import com.eygraber.vice.ViceCompositor
import com.eygraber.vice.ViceEffects
import com.eygraber.virtue.di.scopes.DestinationSingleton
import com.eygraber.virtue.session.VirtueDestination
import com.eygraber.virtue.session.VirtueDestinationComponent
import me.tatarka.inject.annotations.Provides
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
abstract class TodoDestination<R, I, C, S> : VirtueDestination<R, I, C, ViceEffects, S, TodoSessionComponent>()
  where I : Any, C : ViceCompositor<I, S>, S : Any

interface TodoDestinationComponent<R, I, C, S> :
  VirtueDestinationComponent<R, I, C, ViceEffects, S, TodoSessionComponent>
  where C : ViceCompositor<I, S> {
  @DestinationSingleton @Provides fun provideEffects(): ViceEffects = ViceEffects.None
}
