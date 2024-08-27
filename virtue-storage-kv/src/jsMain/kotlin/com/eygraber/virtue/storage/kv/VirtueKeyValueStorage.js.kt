package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.paths.VirtuePaths
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf

internal actual fun createStore(
  paths: VirtuePaths,
  name: String,
): KStore<List<KeyValue>> = storeOf(name)
