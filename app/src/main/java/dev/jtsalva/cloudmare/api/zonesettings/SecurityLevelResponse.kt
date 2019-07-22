package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class SecurityLevelResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<String> = emptyList(),
    override val result: SecurityLevel? = null
) : Response(success, errors, messages)