package dev.jtsalva.cloudmare.api.dns

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class DNSRecordResponse(
    success: Boolean,
    errors: List<Error> = listOf(),
    messages: List<Message> = listOf(),
    override val result: DNSRecord? = null
) : Response(success, errors, messages)