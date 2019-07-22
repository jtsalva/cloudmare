package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class DevelopmentModeResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<String> = emptyList(),
    override val result: DevelopmentMode? = null
) : Response(success, errors, messages)