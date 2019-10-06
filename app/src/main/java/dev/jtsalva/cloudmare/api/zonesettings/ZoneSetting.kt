package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json

data class ZoneSetting(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "editable") val editable: Boolean? = null,
    @field:Json(name = "modified_on") val modifiedOn: String? = null
) {

    companion object {
        const val SSL_MODE_OFF = "off"
        const val SSL_MODE_FLEXIBLE = "flexible"
        const val SSL_MODE_FULL = "full"
        const val SSL_MODE_STRICT = "strict"
    }

}