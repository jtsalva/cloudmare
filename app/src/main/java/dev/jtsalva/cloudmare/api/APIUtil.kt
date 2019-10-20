package dev.jtsalva.cloudmare.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

const val BASE_URL = "https://api.cloudflare.com/client/v4"
const val CLOUDFLARE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
const val DAY_DATE_FORMAT = "EEE"
const val HOURLY_DATE_FORMAT = "HH:mm"
const val MONTH_DATE_FORMAT = "MMM d"


val CloudflareDateTimeFormat = SimpleDateFormat(CLOUDFLARE_DATE_FORMAT, Locale.ENGLISH)
val DayDateTimeFormat = SimpleDateFormat(DAY_DATE_FORMAT, Locale.ENGLISH)
val HourlyDateTimeFormat = SimpleDateFormat(HOURLY_DATE_FORMAT, Locale.ENGLISH)
val MonthDateTimeFormat = SimpleDateFormat(MONTH_DATE_FORMAT, Locale.ENGLISH)

inline fun String.toDate(): Date =
    CloudflareDateTimeFormat.parse(this) ?: Calendar.getInstance().time

inline fun Float.toDateWeekDayAsString(): String =
    DayDateTimeFormat.format(this)

inline fun Float.toDateHourAsString(): String =
    HourlyDateTimeFormat.format(this)

inline fun Float.toDateMonthAsString(): String =
    MonthDateTimeFormat.format(this)

inline fun <T : Any> getAdapter(type: KClass<T>, vararg adapters: Any): JsonAdapter<T> =
    Moshi.Builder().apply { adapters.forEach { add(it) } }.build().adapter<T>(type.java)
