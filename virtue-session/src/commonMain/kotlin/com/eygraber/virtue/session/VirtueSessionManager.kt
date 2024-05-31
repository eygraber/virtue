package com.eygraber.virtue.session

import com.eygraber.virtue.di.scopes.AppSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
public class VirtueSessionManager {
  public val sessions: StateFlow<List<Entry>> = MutableStateFlow(emptyList())

  public fun addSession(
    sessionComponent: GenericVirtueSessionComponent,
    params: VirtueSessionParams,
  ) {
    (sessions as MutableStateFlow).value = sessions.value + Entry(sessionComponent, params)
  }

  public fun removeSession(sessionComponent: GenericVirtueSessionComponent) {
    (sessions as MutableStateFlow).value = sessions.value.filterNot { it.sessionComponent == sessionComponent }
  }

  public class Entry(
    public val sessionComponent: GenericVirtueSessionComponent,
    public val params: VirtueSessionParams,
  )
}

public expect class VirtueSessionParams
