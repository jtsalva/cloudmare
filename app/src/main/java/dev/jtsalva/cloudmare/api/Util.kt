package dev.jtsalva.cloudmare.api

import java.text.SimpleDateFormat
import java.util.*

const val BASE_URL = "https://api.cloudflare.com/client/v4/"

fun endpointUrl(vararg endpoints: String): String {
    var url = BASE_URL
    for (endpoint in endpoints)
        url += "$endpoint/"
    return url
}


typealias DateString = String

typealias ResponseListener<Response> = (Response) -> Unit


val DateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH)

fun DateString.toDate(): Date =
    DateTimeFormat.parse(this) ?: Calendar.getInstance().time

//fun dateToString(datetime: Date): String = DateTimeFormat.format(datetime)