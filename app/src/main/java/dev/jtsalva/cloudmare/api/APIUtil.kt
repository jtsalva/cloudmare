package dev.jtsalva.cloudmare.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

const val BASE_URL = "https://api.cloudflare.com/client/v4"
const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"


val DateTimeFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

inline fun String.toDate(): Date =
    DateTimeFormat.parse(this) ?: Calendar.getInstance().time

inline fun String.toDateDay(): Int =
    Calendar.getInstance().let { calendar ->
        calendar.time = toDate()
        calendar.get(Calendar.DAY_OF_WEEK)
    }

inline fun String.toTimeAsFloat(): Float =
    toDate().time.toFloat()

inline fun Float.toDate(): Date =
    Date().apply { time = toLong() }

inline fun Float.toDateWeekDayAsInt(): Int =
    Calendar.getInstance().apply { time = toDate() }.get(Calendar.DAY_OF_WEEK)

inline fun Date.toDateString(): String =
    DateTimeFormat.format(this)

inline fun <T : Any> getAdapter(type: KClass<T>, vararg adapters: Any): JsonAdapter<T> =
    Moshi.Builder().apply { adapters.forEach { add(it) } }.build().adapter<T>(type.java)
