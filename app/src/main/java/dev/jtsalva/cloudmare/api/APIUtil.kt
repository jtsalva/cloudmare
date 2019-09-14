package dev.jtsalva.cloudmare.api

import android.os.Parcel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

const val BASE_URL = "https://api.cloudflare.com/client/v4"
const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"


val DateTimeFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

fun String.toDate(): Date =
    DateTimeFormat.parse(this) ?: Calendar.getInstance().time

fun Date.toString(): String =
    DateTimeFormat.format(this)

fun <T : Any> getAdapter(type: KClass<T>): JsonAdapter<T> =
    Moshi.Builder().build().adapter<T>(type.java)