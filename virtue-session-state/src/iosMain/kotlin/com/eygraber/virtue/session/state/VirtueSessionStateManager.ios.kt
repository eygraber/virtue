package com.eygraber.virtue.session.state

import com.eygraber.virtue.di.scopes.SessionSingleton
import me.tatarka.inject.annotations.Inject

@SessionSingleton
@Inject
public actual class VirtueSessionStateManager {
  private val storage = mutableMapOf<String, Any>()

  public actual operator fun contains(key: String): Boolean = key in storage

  public actual fun getBoolean(key: String): Boolean? = storage[key] as? Boolean

  public actual fun putBoolean(key: String, value: Boolean) {
    storage[key] = value
  }

  public actual fun getByte(key: String): Byte? = storage[key] as? Byte

  public actual fun putByte(key: String, value: Byte) {
    storage[key] = value
  }

  public actual fun getDouble(key: String): Double? = storage[key] as? Double

  public actual fun putDouble(key: String, value: Double) {
    storage[key] = value
  }

  public actual fun getFloat(key: String): Float? = storage[key] as? Float

  public actual fun putFloat(key: String, value: Float) {
    storage[key] = value
  }

  public actual fun getInt(key: String): Int? = storage[key] as? Int

  public actual fun putInt(key: String, value: Int) {
    storage[key] = value
  }

  public actual fun getShort(key: String): Short? = storage[key] as? Short

  public actual fun putShort(key: String, value: Short) {
    storage[key] = value
  }

  public actual fun getString(key: String): String? = storage[key] as? String

  public actual fun putString(key: String, value: String) {
    storage[key] = value
  }

  public actual fun remove(key: String) {
    storage.remove(key)
  }
}
