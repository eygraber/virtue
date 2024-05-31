package com.eygraber.virtue.session.state

import com.eygraber.virtue.di.scopes.SessionSingleton
import me.tatarka.inject.annotations.Inject
import org.w3c.dom.WindowSessionStorage
import org.w3c.dom.get
import org.w3c.dom.set

@SessionSingleton
@Inject
public actual class VirtueSessionStateManager(
  windowSessionStorage: WindowSessionStorage,
) {
  private val sessionStorage = windowSessionStorage.sessionStorage

  public actual operator fun contains(key: String): Boolean = sessionStorage.getItem(key) != null

  public actual fun getBoolean(key: String): Boolean? = sessionStorage[key]?.toBooleanStrictOrNull()

  public actual fun putBoolean(key: String, value: Boolean) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getByte(key: String): Byte? = sessionStorage[key]?.toByteOrNull()

  public actual fun putByte(key: String, value: Byte) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getDouble(key: String): Double? = sessionStorage[key]?.toDoubleOrNull()

  public actual fun putDouble(key: String, value: Double) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getFloat(key: String): Float? = sessionStorage[key]?.toFloatOrNull()

  public actual fun putFloat(key: String, value: Float) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getInt(key: String): Int? = sessionStorage[key]?.toIntOrNull()

  public actual fun putInt(key: String, value: Int) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getShort(key: String): Short? = sessionStorage[key]?.toShortOrNull()

  public actual fun putShort(key: String, value: Short) {
    sessionStorage[key] = value.toString()
  }

  public actual fun getString(key: String): String? = sessionStorage[key]

  public actual fun putString(key: String, value: String) {
    sessionStorage[key] = value
  }

  public actual fun remove(key: String) {
    sessionStorage.removeItem(key)
  }
}
