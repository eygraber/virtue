package com.eygraber.virtue.storage.kv

import com.eygraber.virtue.storage.kv.VirtueKeyValueStorage.Editable
import kotlinx.coroutines.flow.Flow

public interface VirtueKeyValueStorage {
  public val changes: Flow<Unit>

  public interface Editable {
    public suspend fun contains(key: String): Boolean
    public suspend fun getBoolean(key: String, default: Boolean): Boolean
    public suspend fun getByte(key: String, default: Byte): Byte
    public suspend fun getDouble(key: String, default: Double): Double
    public suspend fun getFloat(key: String, default: Float): Float
    public suspend fun getLong(key: String, default: Long): Long
    public suspend fun getInt(key: String, default: Int): Int
    public suspend fun getShort(key: String, default: Short): Short
    public suspend fun getString(key: String, default: String?): String?

    public suspend fun putBoolean(key: String, value: Boolean): Editable
    public suspend fun putByte(key: String, value: Byte): Editable
    public suspend fun putDouble(key: String, value: Double): Editable
    public suspend fun putFloat(key: String, value: Float): Editable
    public suspend fun putLong(key: String, value: Long): Editable
    public suspend fun putInt(key: String, value: Int): Editable
    public suspend fun putShort(key: String, value: Short): Editable
    public suspend fun putString(key: String, value: String?): Editable

    public suspend fun remove(key: String): Editable
    public fun clear(): Editable

    public suspend fun commit()
  }

  public suspend fun contains(key: String): Boolean
  public suspend fun getBoolean(key: String, default: Boolean): Boolean
  public suspend fun getByte(key: String, default: Byte): Byte
  public suspend fun getDouble(key: String, default: Double): Double
  public suspend fun getFloat(key: String, default: Float): Float
  public suspend fun getLong(key: String, default: Long): Long
  public suspend fun getInt(key: String, default: Int): Int
  public suspend fun getShort(key: String, default: Short): Short
  public suspend fun getString(key: String, default: String?): String?

  public fun edit(): Editable
}

public suspend inline fun VirtueKeyValueStorage.edit(block: Editable.() -> Unit) {
  edit().apply(block).commit()
}

public suspend fun Editable.flip(key: String, default: Boolean = false): Editable =
  putBoolean(key, !getBoolean(key, default))

public suspend fun Editable.increment(key: String, value: Byte = 1, default: Byte = 0): Editable =
  putByte(key, (getByte(key, default) + value).toByte())

public suspend fun Editable.increment(key: String, value: Double = 1.0, default: Double = 0.0): Editable =
  putDouble(key, getDouble(key, default) + value)

public suspend fun Editable.increment(key: String, value: Float = 1F, default: Float = 0F): Editable =
  putFloat(key, getFloat(key, default) + value)

public suspend fun Editable.increment(key: String, value: Long = 1L, default: Long = 0L): Editable =
  putLong(key, getLong(key, default) + value)

public suspend fun Editable.increment(key: String, value: Int = 1, default: Int = 0): Editable =
  putInt(key, getInt(key, default) + value)

public suspend fun Editable.increment(key: String, value: Short = 1, default: Short = 0): Editable =
  putShort(key, (getShort(key, default) + value).toShort())

public suspend fun Editable.decrement(key: String, value: Byte = 1, default: Byte = 0): Editable =
  putByte(key, (getByte(key, default) - value).toByte())

public suspend fun Editable.decrement(key: String, value: Double = 1.0, default: Double = 0.0): Editable =
  putDouble(key, getDouble(key, default) - value)

public suspend fun Editable.decrement(key: String, value: Float = 1F, default: Float = 0F): Editable =
  putFloat(key, getFloat(key, default) - value)

public suspend fun Editable.decrement(key: String, value: Long = 1L, default: Long = 0L): Editable =
  putLong(key, getLong(key, default) - value)

public suspend fun Editable.decrement(key: String, value: Int = 1, default: Int = 0): Editable =
  putInt(key, getInt(key, default) - value)

public suspend fun Editable.decrement(key: String, value: Short = 1, default: Short = 0): Editable =
  putShort(key, (getShort(key, default) - value).toShort())

public suspend operator fun Editable.minusAssign(key: String) {
  remove(key)
}

public suspend operator fun Editable.minusAssign(keys: Iterable<String>) {
  keys.forEach { remove(it) }
}
