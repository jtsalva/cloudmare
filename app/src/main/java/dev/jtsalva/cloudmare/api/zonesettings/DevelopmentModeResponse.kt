package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class DevelopmentModeResponse(
    success: Boolean,
    errors: List<Error> = listOf(),
    messages: List<String> = listOf(),
    override val result: DevelopmentMode? = null
) : Response(success, errors, messages)