package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.paths.VirtuePaths
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path
import kotlinx.io.files.SystemPathSeparator
import me.tatarka.inject.annotations.Inject

@Inject
public actual class VirtueKStoreProvider {
  internal actual fun createStore(
    paths: VirtuePaths,
    name: String,
  ): KStore<List<KeyValue>> = storeOf(Path(paths.projectConfigDir + SystemPathSeparator + name))
}
