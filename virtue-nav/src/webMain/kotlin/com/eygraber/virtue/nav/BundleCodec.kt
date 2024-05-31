package com.eygraber.virtue.nav

import androidx.core.bundle.Bundle
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.BooleanArraySerializer
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.CharArraySerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.ShortArraySerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalSerializationApi::class, ExperimentalEncodingApi::class)
internal object BundleCodec {
  private val cbor = Cbor {
    serializersModule = SerializersModule {
      contextual(Bundle::class, BundleSerializer)
      contextual(ValueWithType::class, ValueWithTypeSerializer)
    }
  }

  fun encodeToString(value: Bundle): String {
    val cborBytes = cbor.encodeToByteArray(BundleSerializer, value)
    return Base64.UrlSafe.encode(cborBytes)
  }

  fun decodeFromString(base64String: String): Bundle {
    val cborBytes = Base64.UrlSafe.decode(base64String)
    return cbor.decodeFromByteArray(BundleSerializer, cborBytes)
  }
}

@Serializable(with = ValueWithTypeSerializer::class)
private class ValueWithType(
  val type: BundleType,
  val value: Any?,
)

@Suppress("OPT_IN_USAGE", "UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
private object ValueWithTypeSerializer : KSerializer<ValueWithType> {
  override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ValueWithType") {
    element<BundleType>("type")
    element("value", buildSerialDescriptor("kotlin.Any?", SerialKind.CONTEXTUAL))
  }

  override fun serialize(encoder: Encoder, value: ValueWithType) {
    val enc = encoder.beginStructure(descriptor)
    enc.encodeSerializableElement(descriptor, 0, BundleType.serializer(), value.type)
    when(value.type) {
      BundleType.Boolean -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Boolean.serializer().nullable,
        value.value as Boolean?,
      )

      BundleType.Byte -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Int.serializer().nullable,
        (value.value as? Byte)?.toInt(),
      )

      BundleType.Char -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Char.serializer().nullable,
        value.value as Char?,
      )

      BundleType.Short -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Int.serializer().nullable,
        (value.value as? Short)?.toInt(),
      )

      BundleType.Int -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Int.serializer().nullable,
        value.value as Int?,
      )

      BundleType.Long -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Long.serializer().nullable,
        value.value as Long?,
      )

      BundleType.Float -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Double.serializer().nullable,
        (value.value as? Float)?.toDouble(),
      )

      BundleType.Double -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        Double.serializer().nullable,
        value.value as Double?,
      )

      BundleType.String -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        String.serializer().nullable,
        value.value as String?,
      )

      BundleType.CharSequence -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        String.serializer().nullable,
        value.value as String?,
      ) // Assuming CharSequence is a String

      BundleType.IntArrayList -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ListSerializer(Int.serializer()).nullable,
        value.value as List<Int>?,
      )

      BundleType.StringArrayList -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ListSerializer(String.serializer()).nullable,
        value.value as List<String>?,
      )

      BundleType.BooleanArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        BooleanArraySerializer().nullable,
        value.value as BooleanArray?,
      )

      BundleType.ByteArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ByteArraySerializer().nullable,
        value.value as ByteArray?,
      )

      BundleType.ShortArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ShortArraySerializer().nullable,
        value.value as ShortArray?,
      )

      BundleType.CharArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        CharArraySerializer().nullable,
        value.value as CharArray?,
      )

      BundleType.IntArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        IntArraySerializer().nullable,
        value.value as IntArray?,
      )

      BundleType.LongArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        LongArraySerializer().nullable,
        value.value as LongArray?,
      )

      BundleType.FloatArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        FloatArraySerializer().nullable,
        value.value as FloatArray?,
      )

      BundleType.DoubleArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        DoubleArraySerializer().nullable,
        value.value as DoubleArray?,
      )

      BundleType.StringArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ArraySerializer(String.serializer()).nullable,
        value.value as Array<String>?,
      )

      BundleType.CharSequenceArray -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        ArraySerializer(String.serializer()).nullable,
        value.value as Array<String>?,
      ) // Assuming CharSequenceArray is Array<String>

      BundleType.Bundle -> enc.encodeNullableSerializableElement(
        descriptor,
        1,
        BundleSerializer.nullable,
        value.value as Bundle?,
      )
    }
    enc.endStructure(descriptor)
  }

  override fun deserialize(decoder: Decoder): ValueWithType {
    val dec = decoder.beginStructure(descriptor)
    var type: BundleType? = null
    var value: Any? = null
    while(true) {
      when(val index = dec.decodeElementIndex(descriptor)) {
        CompositeDecoder.DECODE_DONE -> break
        0 -> type = dec.decodeSerializableElement(descriptor, index, BundleType.serializer())
        1 -> type?.let {
          value = when(it) {
            BundleType.Boolean -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              Boolean.serializer().nullable,
            )

            BundleType.Byte -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              Int.serializer().nullable,
            )?.toByte()

            BundleType.Char -> dec.decodeNullableSerializableElement(descriptor, index, Char.serializer().nullable)

            BundleType.Short -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              Int.serializer().nullable,
            )?.toShort()

            BundleType.Int -> dec.decodeNullableSerializableElement(descriptor, index, Int.serializer().nullable)
            BundleType.Long -> dec.decodeNullableSerializableElement(descriptor, index, Long.serializer().nullable)

            BundleType.Float -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              Double.serializer().nullable,
            )?.toFloat()

            BundleType.Double -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              Double.serializer().nullable,
            )

            BundleType.String -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              String.serializer().nullable,
            )

            BundleType.CharSequence -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              String.serializer().nullable,
            ) // Assuming CharSequence is a String

            BundleType.IntArrayList -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ListSerializer(Int.serializer()).nullable,
            )

            BundleType.StringArrayList -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ListSerializer(String.serializer()).nullable,
            )

            BundleType.BooleanArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              BooleanArraySerializer().nullable,
            )

            BundleType.ByteArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ByteArraySerializer().nullable,
            )

            BundleType.ShortArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ShortArraySerializer().nullable,
            )

            BundleType.CharArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              CharArraySerializer().nullable,
            )

            BundleType.IntArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              IntArraySerializer().nullable,
            )

            BundleType.LongArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              LongArraySerializer().nullable,
            )

            BundleType.FloatArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              FloatArraySerializer().nullable,
            )

            BundleType.DoubleArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              DoubleArraySerializer().nullable,
            )

            BundleType.StringArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ArraySerializer(String.serializer()).nullable,
            )

            BundleType.CharSequenceArray -> dec.decodeNullableSerializableElement(
              descriptor,
              index,
              ArraySerializer(String.serializer()).nullable,
            ) // Assuming CharSequenceArray is Array<String>

            BundleType.Bundle -> dec.decodeNullableSerializableElement(descriptor, index, BundleSerializer.nullable)
          }
        }

        else -> throw SerializationException("Unknown index $index")
      }
    }
    dec.endStructure(descriptor)
    return ValueWithType(type!!, value)
  }
}

@Serializable
private enum class BundleType {
  Boolean,
  Byte,
  Char,
  Short,
  Int,
  Long,
  Float,
  Double,
  String,
  CharSequence,
  IntArrayList,
  StringArrayList,
  BooleanArray,
  ByteArray,
  ShortArray,
  CharArray,
  IntArray,
  LongArray,
  FloatArray,
  DoubleArray,
  StringArray,
  CharSequenceArray,
  Bundle,
}

private val Any.bundleType
  get() = when(this) {
    is Boolean -> BundleType.Boolean
    is Byte -> BundleType.Byte
    is Char -> BundleType.Char
    is Short -> BundleType.Short
    is Int -> BundleType.Int
    is Long -> BundleType.Long
    is Float -> BundleType.Float
    is Double -> BundleType.Double
    is String -> BundleType.String
    is CharSequence -> BundleType.CharSequence
    is ArrayList<*> -> when {
      all { it is Int } -> BundleType.IntArrayList
      all { it is String } -> BundleType.StringArrayList
      else -> throw IllegalArgumentException("$this is not a valid ArrayList type")
    }

    is BooleanArray -> BundleType.BooleanArray
    is ByteArray -> BundleType.ByteArray
    is ShortArray -> BundleType.ShortArray
    is CharArray -> BundleType.CharArray
    is IntArray -> BundleType.IntArray
    is LongArray -> BundleType.LongArray
    is FloatArray -> BundleType.FloatArray
    is DoubleArray -> BundleType.DoubleArray
    is Array<*> -> when {
      all { it is String } -> BundleType.StringArray
      all { it is CharSequence } -> BundleType.CharSequenceArray
      else -> throw IllegalArgumentException("$this is not a valid Array type")
    }

    is Bundle -> BundleType.Bundle
    else -> throw IllegalArgumentException("$this is not a valid Bundle type")
  }

@Serializable
private data class NullableKey(
  val key: String?,
)

@Suppress("UNCHECKED_CAST", "DEPRECATION")
private object BundleSerializer : KSerializer<Bundle> {
  override val descriptor: SerialDescriptor =
    buildClassSerialDescriptor("Bundle") {
      element<Map<NullableKey, ValueWithType?>>("map")
    }

  override fun serialize(encoder: Encoder, value: Bundle) {
    val map = value.keySet().mapNotNull { key ->
      NullableKey(key) to ValueWithType(
        type = value[key]?.bundleType ?: BundleType.String,
        value = value[key],
      )
    }.toMap()

    encoder.encodeSerializableValue(MapSerializer(NullableKey.serializer(), ValueWithType.serializer().nullable), map)
  }

  override fun deserialize(decoder: Decoder): Bundle {
    val map =
      decoder.decodeSerializableValue(MapSerializer(NullableKey.serializer(), ValueWithType.serializer().nullable))
    val bundle = Bundle()

    for((nullableKey, valueWithType) in map) {
      val key = nullableKey.key
      if(valueWithType == null) {
        bundle.putString(key, null)
      }
      else {
        when(valueWithType.type) {
          BundleType.Boolean -> if(valueWithType.value is Boolean) bundle.putBoolean(key, valueWithType.value)
          BundleType.Byte -> if(valueWithType.value is Byte) bundle.putByte(key, valueWithType.value)
          BundleType.Char -> if(valueWithType.value is Char) bundle.putChar(key, valueWithType.value)
          BundleType.Short -> if(valueWithType.value is Short) bundle.putShort(key, valueWithType.value)
          BundleType.Int -> if(valueWithType.value is Int) bundle.putInt(key, valueWithType.value)
          BundleType.Long -> if(valueWithType.value is Long) bundle.putLong(key, valueWithType.value)
          BundleType.Float -> if(valueWithType.value is Float) bundle.putFloat(key, valueWithType.value)
          BundleType.Double -> if(valueWithType.value is Double) bundle.putDouble(key, valueWithType.value)

          BundleType.String -> when(valueWithType.value) {
            is String -> bundle.putString(key, valueWithType.value)
            null -> bundle.putString(key, null)
          }

          BundleType.CharSequence -> if(valueWithType.value is CharSequence) {
            bundle.putCharSequence(
              key,
              valueWithType.value,
            )
          }

          BundleType.IntArrayList -> if(valueWithType.value is List<*>) {
            bundle.putIntegerArrayList(
              key,
              valueWithType.value as ArrayList<Int>,
            )
          }

          BundleType.StringArrayList -> if(valueWithType.value is List<*>) {
            bundle.putStringArrayList(
              key,
              valueWithType.value as ArrayList<String>,
            )
          }

          BundleType.BooleanArray -> if(valueWithType.value is BooleanArray) {
            bundle.putBooleanArray(
              key,
              valueWithType.value,
            )
          }

          BundleType.ByteArray -> if(valueWithType.value is ByteArray) bundle.putByteArray(key, valueWithType.value)
          BundleType.ShortArray -> if(valueWithType.value is ShortArray) bundle.putShortArray(key, valueWithType.value)
          BundleType.CharArray -> if(valueWithType.value is CharArray) bundle.putCharArray(key, valueWithType.value)
          BundleType.IntArray -> if(valueWithType.value is IntArray) bundle.putIntArray(key, valueWithType.value)
          BundleType.LongArray -> if(valueWithType.value is LongArray) bundle.putLongArray(key, valueWithType.value)
          BundleType.FloatArray -> if(valueWithType.value is FloatArray) bundle.putFloatArray(key, valueWithType.value)

          BundleType.DoubleArray -> if(valueWithType.value is DoubleArray) {
            bundle.putDoubleArray(
              key,
              valueWithType.value,
            )
          }

          BundleType.StringArray -> if(valueWithType.value is Array<*>) {
            bundle.putStringArray(
              key,
              valueWithType.value as Array<String>,
            )
          }

          BundleType.CharSequenceArray -> if(valueWithType.value is Array<*>) {
            bundle.putCharSequenceArray(
              key,
              valueWithType.value as Array<CharSequence>,
            )
          }

          BundleType.Bundle -> if(valueWithType.value is Bundle) bundle.putBundle(key, valueWithType.value)
        }
      }
    }
    return bundle
  }
}
