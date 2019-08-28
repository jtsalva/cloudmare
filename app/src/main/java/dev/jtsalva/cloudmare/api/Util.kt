package dev.jtsalva.cloudmare.api

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://api.cloudflare.com/client/v4/"
const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"

fun endpointUrl(vararg endpoints: String): String {
    var url = BASE_URL
    for (endpoint in endpoints)
        url += "$endpoint/"
    return url
}


typealias DateString = String

val DateTimeFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

fun DateString.toDate(): Date =
    DateTimeFormat.parse(this) ?: Calendar.getInstance().time

//fun dateToString(datetime: Date): String = DateTimeFormat.format(datetime)

fun <T> getAdapter(type: Class<T>): JsonAdapter<T> =
    Moshi.Builder().build().adapter<T>(type)