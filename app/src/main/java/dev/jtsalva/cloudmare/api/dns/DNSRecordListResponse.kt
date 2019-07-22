package dev.jtsalva.cloudmare.api.dns

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class DNSRecordListResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<String> = emptyList(),
    override val result: List<DNSRecord>? = null
) : Response(success, errors, messages)