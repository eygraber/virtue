package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.storage.kv.VirtueKeyValueStorage.Editable

internal abstract class EditableKeyValueStorage(
  map: MutableMap<String, String?>,
) : Editable {
  private val map = mutableMapOf<String, String?>().apply {
    putAll(map)
  }

  override suspend fun contains(key: String): Boolean = key in map

  override suspend fun getBoolean(key: String, default: Boolean): Boolean = map[key]?.toBooleanStrictOrNull() ?: default

  override suspend fun getByte(key: String, default: Byte): Byte = map[key]?.toByteOrNull() ?: default

  override suspend fun getDouble(key: String, default: Double): Double = map[key]?.toDoubleOrNull() ?: default

  override suspend fun getFloat(key: String, default: Float): Float = map[key]?.toFloatOrNull() ?: default

  override suspend fun getLong(key: String, default: Long): Long = map[key]?.toLongOrNull() ?: default

  override suspend fun getInt(key: String, default: Int): Int = map[key]?.toIntOrNull() ?: default

  override suspend fun getShort(key: String, default: Short): Short = map[key]?.toShortOrNull() ?: default

  override suspend fun getString(key: String, default: String?): String? = map[key] ?: default

  override suspend fun putBoolean(key: String, value: Boolean): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putByte(key: String, value: Byte): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putDouble(key: String, value: Double): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putFloat(key: String, value: Float): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putLong(key: String, value: Long): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putInt(key: String, value: Int): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putShort(key: String, value: Short): Editable = this.apply {
    map[key] = value.toString()
  }

  override suspend fun putString(key: String, value: String?): Editable = this.apply {
    map[key] = value
  }

  override suspend fun remove(key: String): Editable = this.apply {
    map -= key
  }

  override fun clear(): Editable = this.apply {
    map.clear()
  }

  override suspend fun commit() {
    commit(map)
  }

  protected abstract suspend fun commit(storage: Map<String, String?>)
}
