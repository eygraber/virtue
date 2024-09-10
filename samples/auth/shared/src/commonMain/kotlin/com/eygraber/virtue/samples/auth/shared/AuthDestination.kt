package com.eygraber.virtue.samples.auth.shared

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
abstract class AuthDestination<R, I, C, S> : VirtueDestination<R, I, C, ViceEffects, S, AuthSessionComponent>()
  where I : Any, C : ViceCompositor<I, S>, S : Any

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
abstract class AuthDestinationWithEffects<R, I, C, E, S> : VirtueDestination<R, I, C, E, S, AuthSessionComponent>()
  where I : Any, C : ViceCompositor<I, S>, E : ViceEffects, S : Any

interface AuthDestinationComponent<R, I, C, S> :
  VirtueDestinationComponent<R, I, C, ViceEffects, S, AuthSessionComponent>
  where C : ViceCompositor<I, S> {
  @DestinationSingleton @Provides fun provideEffects(): ViceEffects = ViceEffects.None
}

interface AuthDestinationComponentWithEffects<R, I, C, E, S> :
  VirtueDestinationComponent<R, I, C, E, S, AuthSessionComponent>
  where C : ViceCompositor<I, S>, E : ViceEffects
