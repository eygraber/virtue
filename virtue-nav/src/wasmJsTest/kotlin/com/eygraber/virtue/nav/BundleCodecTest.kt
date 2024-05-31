package com.eygraber.virtue.nav

import androidx.core.bundle.Bundle
import kotlin.test.DefaultAsserter.assertNotEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BundleCodecTest {
  @Test
  fun testBundleEncodingDecoding() {
    val bundleA = createTestBundle()
    val encoded = bundleA.encode()
    val decoded = encoded.decode()

    assertTrue(bundleA isEquals decoded, "Bundle A should be equal to the decoded bundle.")
  }

  @Test
  fun testDifferentBundlesNotEqualAfterEncoding() {
    val bundleA = createTestBundle()
    val bundleB = createDifferentTestBundle()
    val encodedA = bundleA.encode()
    val encodedB = bundleB.encode()

    assertNotEquals(encodedA, encodedB, "Encoded Bundle A should not be equal to encoded Bundle B.")
  }

  @Test
  fun testDifferentBundlesNotEqualAfterDecoding() {
    val bundleA = createTestBundle()
    val bundleB = createDifferentTestBundle()
    val encodedB = bundleB.encode()
    val decodedB = encodedB.decode()

    assertFalse(bundleA isEquals bundleB, "Bundle A should not be equal to the different Bundle B.")
    assertFalse(bundleA isEquals decodedB, "Bundle A should not be equal to the decoded different Bundle B.")
  }

  private fun createTestBundle() = Bundle().apply {
    putBoolean("boolean", true)
    putByte("byte", 1)
    putChar("char", 'a')
    putShort("short", 1)
    putInt("int", 1)
    putLong("long", 1L)
    putFloat("float", 1.0f)
    putDouble("double", 1.0)
    putString("string", "test")
    putCharSequence("charSequence", "test")
    putIntegerArrayList("intArrayList", arrayListOf(1, 2, 3))
    putStringArrayList("stringArrayList", arrayListOf("one", "two", "three"))
    putBooleanArray("booleanArray", booleanArrayOf(true, false))
    putByteArray("byteArray", byteArrayOf(1, 2))
    putShortArray("shortArray", shortArrayOf(1, 2))
    putCharArray("charArray", charArrayOf('a', 'b'))
    putIntArray("intArray", intArrayOf(1, 2))
    putLongArray("longArray", longArrayOf(1L, 2L))
    putFloatArray("floatArray", floatArrayOf(1.0f, 2.0f))
    putDoubleArray("doubleArray", doubleArrayOf(1.0, 2.0))
    putStringArray("stringArray", arrayOf("one", "two"))
    putCharSequenceArray("charSequenceArray", arrayOf("one", "two"))
    putBundle("bundle", Bundle().apply { putString("nested", "bundle") })

    putString("nullString", null)
    putBundle(null, Bundle().apply { putString("nullKeyNested", "nullKeyBundle") })
    putString(null, null)
  }

  private fun createDifferentTestBundle() = Bundle().apply {
    putBoolean("boolean", false)
    putByte("byte", 2)
    putChar("char", 'b')
    putShort("short", 2)
    putInt("int", 2)
    putLong("long", 2L)
    putFloat("float", 2.0f)
    putDouble("double", 2.0)
    putString("string", "different")
    putCharSequence("charSequence", "different")
    putIntegerArrayList("intArrayList", arrayListOf(4, 5, 6))
    putStringArrayList("stringArrayList", arrayListOf("four", "five", "six"))
    putBooleanArray("booleanArray", booleanArrayOf(false, true))
    putByteArray("byteArray", byteArrayOf(3, 4))
    putShortArray("shortArray", shortArrayOf(3, 4))
    putCharArray("charArray", charArrayOf('c', 'd'))
    putIntArray("intArray", intArrayOf(3, 4))
    putLongArray("longArray", longArrayOf(3L, 4L))
    putFloatArray("floatArray", floatArrayOf(3.0f, 4.0f))
    putDoubleArray("doubleArray", doubleArrayOf(3.0, 4.0))
    putStringArray("stringArray", arrayOf("three", "four"))
    putCharSequenceArray("charSequenceArray", arrayOf("three", "four"))
    putBundle("bundle", Bundle().apply { putString("nested", "differentBundle") })

    putString("nullString", null)
    putBundle(null, Bundle().apply { putString("nullKeyNested", "nullKeyDifferentBundle") })
    putString(null, null)
  }
}

private fun Bundle.encode() = BundleCodec.encodeToString(this)

private fun String.decode() = BundleCodec.decodeFromString(this)

@Suppress("DEPRECATION", "ReturnCount")
infix fun Bundle.isEquals(other: Bundle): Boolean {
  val keySetA = keySet()
  val keySetB = other.keySet()

  if(keySetA.intersect(keySetB).size != keySetA.size) return false

  keySetA.forEach { key ->
    val valueA = get(key)
    val valueB = other[key]

    val isEqual = when(valueA) {
      is BooleanArray -> valueB is BooleanArray && valueA.contentEquals(valueB)
      is ByteArray -> valueB is ByteArray && valueA.contentEquals(valueB)
      is ShortArray -> valueB is ShortArray && valueA.contentEquals(valueB)
      is CharArray -> valueB is CharArray && valueA.contentEquals(valueB)
      is IntArray -> valueB is IntArray && valueA.contentEquals(valueB)
      is LongArray -> valueB is LongArray && valueA.contentEquals(valueB)
      is FloatArray -> valueB is FloatArray && valueA.contentEquals(valueB)
      is DoubleArray -> valueB is DoubleArray && valueA.contentEquals(valueB)
      is Array<*> -> valueB is Array<*> && valueA.contentEquals(valueB)
      is Bundle -> valueB is Bundle && valueA isEquals valueB
      else -> valueA == valueB
    }

    if(!isEqual) return false
  }

  return true
}
