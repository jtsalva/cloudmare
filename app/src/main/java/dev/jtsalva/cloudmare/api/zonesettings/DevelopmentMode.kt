package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json

data class DevelopmentMode(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "editable") val editable: Boolean,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "time_remaining") val timeRemaining: Int
) {
    companion object Value {
        const val OFF = "off"
        const val ON = "on"
    }
}