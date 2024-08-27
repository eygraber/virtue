package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.storage.kv.VirtueKeyValueStorage.Editable

internal abstract class EditableKeyValueStorage(
  map: MutableMap<String, String?>,
) : Editable {
  private val map = mutableMapOf<String, String?>().apply {
    putAll(map)
  }

  override fun contains(key: String): Boolean = key in map

  override fun getBoolean(key: String, default: Boolean): Boolean = map[key]?.toBooleanStrictOrNull() ?: default

  override fun getByte(key: String, default: Byte): Byte = map[key]?.toByteOrNull() ?: default

  override fun getDouble(key: String, default: Double): Double = map[key]?.toDoubleOrNull() ?: default

  override fun getFloat(key: String, default: Float): Float = map[key]?.toFloatOrNull() ?: default

  override fun getLong(key: String, default: Long): Long = map[key]?.toLongOrNull() ?: default

  override fun getInt(key: String, default: Int): Int = map[key]?.toIntOrNull() ?: default

  override fun getShort(key: String, default: Short): Short = map[key]?.toShortOrNull() ?: default

  override fun getString(key: String, default: String?): String? = map[key] ?: default

  override fun putBoolean(key: String, value: Boolean): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putByte(key: String, value: Byte): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putDouble(key: String, value: Double): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putFloat(key: String, value: Float): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putLong(key: String, value: Long): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putInt(key: String, value: Int): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putShort(key: String, value: Short): Editable = this.apply {
    map[key] = value.toString()
  }

  override fun putString(key: String, value: String?): Editable = this.apply {
    map[key] = value
  }

  override fun remove(key: String): Editable = this.apply {
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

public suspend inline fun VirtueKeyValueStorage.edit(block: Editable.() -> Unit) {
  edit().apply(block).commit()
}

public operator fun Editable.set(key: String, value: Boolean): Editable = putBoolean(key, value)
public operator fun Editable.set(key: String, value: Byte): Editable = putByte(key, value)
public operator fun Editable.set(key: String, value: Double): Editable = putDouble(key, value)
public operator fun Editable.set(key: String, value: Float): Editable = putFloat(key, value)
public operator fun Editable.set(key: String, value: Long): Editable = putLong(key, value)
public operator fun Editable.set(key: String, value: Int): Editable = putInt(key, value)
public operator fun Editable.set(key: String, value: Short): Editable = putShort(key, value)
public operator fun Editable.set(key: String, value: String?): Editable = putString(key, value)

public fun Editable.flip(key: String, default: Boolean = false): Editable = putBoolean(key, !getBoolean(key, default))

public fun Editable.increment(key: String, value: Byte = 1, default: Byte = 0): Editable =
  putByte(key, (getByte(key, default) + value).toByte())

public fun Editable.increment(key: String, value: Double = 1.0, default: Double = 0.0): Editable =
  putDouble(key, getDouble(key, default) + value)

public fun Editable.increment(key: String, value: Float = 1F, default: Float = 0F): Editable =
  putFloat(key, getFloat(key, default) + value)

public fun Editable.increment(key: String, value: Long = 1L, default: Long = 0L): Editable =
  putLong(key, getLong(key, default) + value)

public fun Editable.increment(key: String, value: Int = 1, default: Int = 0): Editable =
  putInt(key, getInt(key, default) + value)

public fun Editable.increment(key: String, value: Short = 1, default: Short = 0): Editable =
  putShort(key, (getShort(key, default) + value).toShort())

public fun Editable.decrement(key: String, value: Byte = 1, default: Byte = 0): Editable =
  putByte(key, (getByte(key, default) - value).toByte())

public fun Editable.decrement(key: String, value: Double = 1.0, default: Double = 0.0): Editable =
  putDouble(key, getDouble(key, default) - value)

public fun Editable.decrement(key: String, value: Float = 1F, default: Float = 0F): Editable =
  putFloat(key, getFloat(key, default) - value)

public fun Editable.decrement(key: String, value: Long = 1L, default: Long = 0L): Editable =
  putLong(key, getLong(key, default) - value)

public fun Editable.decrement(key: String, value: Int = 1, default: Int = 0): Editable =
  putInt(key, getInt(key, default) - value)

public fun Editable.decrement(key: String, value: Short = 1, default: Short = 0): Editable =
  putShort(key, (getShort(key, default) - value).toShort())

public operator fun Editable.minusAssign(key: String) {
  remove(key)
}

public operator fun Editable.minusAssign(keys: Iterable<String>) {
  keys.forEach(::remove)
}
