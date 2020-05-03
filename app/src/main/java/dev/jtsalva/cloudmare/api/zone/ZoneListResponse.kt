package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class ZoneListResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<Message> = emptyList(),
    override val result: List<Zone>? = null
) : Response(success, errors, messages)
