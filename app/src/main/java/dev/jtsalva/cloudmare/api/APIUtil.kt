package dev.jtsalva.cloudmare.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

const val BASE_URL = "https://api.cloudflare.com/client/v4"
const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"

val DateTimeFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

inline fun String.toDate(): Date =
    DateTimeFormat.parse(this) ?: Calendar.getInstance().time

inline fun Date.toString(): String =
    DateTimeFormat.format(this)

inline fun <T : Any> getAdapter(type: KClass<T>, vararg adapters: Any): JsonAdapter<T> =
    Moshi.Builder().apply { adapters.forEach { add(it) } }.build().adapter<T>(type.java)
