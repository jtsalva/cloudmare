package dev.jtsalva.cloudmare.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.KClass
import org.json.JSONObject

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

private inline fun <T : Any> getAdapter(type: KClass<T>): JsonAdapter<T> =
    Moshi.Builder().build().adapter(type.java)

internal inline fun <reified T : Any> T.toJson(): JSONObject =
    JSONObject(toJsonString())

internal inline fun <reified T : Any> T.toJsonString(): String =
    getAdapter(T::class).toJson(this)

internal inline fun <reified T : Response> JSONObject?.fromJson(): T =
    getAdapter(T::class).fromJson(toString()) ?: Response(success = false) as T
