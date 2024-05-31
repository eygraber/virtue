package com.eygraber.virtue.session.state

import android.os.Bundle
import com.eygraber.virtue.di.scopes.SessionSingleton
import me.tatarka.inject.annotations.Inject

@SessionSingleton
@Inject
public actual class VirtueSessionStateManager {
  private val state = Bundle()

  public actual operator fun contains(key: String): Boolean = state.containsKey(key)

  public actual fun getBoolean(key: String): Boolean? = when {
    state.containsKey(key) -> state.getBoolean(key)
    else -> null
  }

  public actual fun putBoolean(key: String, value: Boolean) {
    state.putBoolean(key, value)
  }

  public actual fun getByte(key: String): Byte? = when {
    state.containsKey(key) -> state.getByte(key)
    else -> null
  }

  public actual fun putByte(key: String, value: Byte) {
    state.putByte(key, value)
  }

  public actual fun getDouble(key: String): Double? = when {
    state.containsKey(key) -> state.getDouble(key)
    else -> null
  }

  public actual fun putDouble(key: String, value: Double) {
    state.putDouble(key, value)
  }

  public actual fun getFloat(key: String): Float? = when {
    state.containsKey(key) -> state.getFloat(key)
    else -> null
  }

  public actual fun putFloat(key: String, value: Float) {
    state.putFloat(key, value)
  }

  public actual fun getInt(key: String): Int? = when {
    state.containsKey(key) -> state.getInt(key)
    else -> null
  }

  public actual fun putInt(key: String, value: Int) {
    state.putInt(key, value)
  }

  public actual fun getShort(key: String): Short? = when {
    state.containsKey(key) -> state.getShort(key)
    else -> null
  }

  public actual fun putShort(key: String, value: Short) {
    state.putShort(key, value)
  }

  public actual fun getString(key: String): String? = state.getString(key)

  public actual fun putString(key: String, value: String) {
    state.putString(key, value)
  }

  public actual fun remove(key: String) {
    state.remove(key)
  }

  public fun onSaveState(outState: Bundle) {
    outState.putBundle(KEY, state)
  }

  public fun onRestoreState(savedInstanceState: Bundle) {
    savedInstanceState.getBundle(KEY)?.let(state::putAll)
  }
}

private const val KEY = "com.eygraber.virtue.session.state.VirtueSessionStateManager"
