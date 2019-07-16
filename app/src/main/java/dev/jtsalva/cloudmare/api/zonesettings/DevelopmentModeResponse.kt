package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
data class DevelopmentModeResponse(
    override val success: Boolean,
    override val errors: List<Error>,
    override val messages: List<String>,
    override val result: DevelopmentMode?
) : Response()