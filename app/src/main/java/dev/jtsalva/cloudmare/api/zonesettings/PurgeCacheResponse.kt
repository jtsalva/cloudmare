package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class PurgeCacheResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<Message> = emptyList(),
    override val result: Map<String, String>? = null
) : Response(success, errors, messages)