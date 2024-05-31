package com.eygraber.virtue.session.state

public expect class VirtueSessionStateManager {
  public operator fun contains(key: String): Boolean

  public fun getBoolean(key: String): Boolean?
  public fun putBoolean(key: String, value: Boolean)

  public fun getByte(key: String): Byte?
  public fun putByte(key: String, value: Byte)

  public fun getDouble(key: String): Double?
  public fun putDouble(key: String, value: Double)

  public fun getFloat(key: String): Float?
  public fun putFloat(key: String, value: Float)

  public fun getInt(key: String): Int?
  public fun putInt(key: String, value: Int)

  public fun getShort(key: String): Short?
  public fun putShort(key: String, value: Short)

  public fun getString(key: String): String?
  public fun putString(key: String, value: String)

  public fun remove(key: String)
}
