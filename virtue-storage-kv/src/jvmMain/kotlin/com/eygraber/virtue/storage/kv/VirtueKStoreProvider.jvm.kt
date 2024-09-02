package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.paths.VirtuePaths
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toPath

@Inject
public actual class VirtueKStoreProvider {
  internal actual fun createStore(
    paths: VirtuePaths,
    name: String,
  ): KStore<List<KeyValue>> = storeOf(paths.projectDataDir.toPath() / name.toPath())
}
