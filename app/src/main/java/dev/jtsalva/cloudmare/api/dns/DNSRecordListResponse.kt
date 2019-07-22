package dev.jtsalva.cloudmare.api.dns

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class DNSRecordListResponse(
    success: Boolean,
    errors: List<Error> = listOf(),
    messages: List<String> = listOf(),
    override val result: List<DNSRecord>? = null
) : Response(success, errors, messages)