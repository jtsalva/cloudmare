package dev.jtsalva.cloudmare.api.dns

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
data class DNSRecordResponse(
    override val success: Boolean,
    override val errors: List<Error>,
    override val messages: List<String>,
    override val result: DNSRecord?
) : Response()