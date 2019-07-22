package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class SecurityLevelResponse(
    success: Boolean,
    errors: List<Error> = listOf(),
    messages: List<String> = listOf(),
    override val result: SecurityLevel? = null
) : Response(success, errors, messages)