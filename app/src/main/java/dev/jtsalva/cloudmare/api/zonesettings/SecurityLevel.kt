package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json

data class SecurityLevel(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "editable") val editable: Boolean,
    @field:Json(name = "modified_on") val modifiedOn: String
) {
    companion object Value {
        const val ESSENTIALLY_OFF = "essentially_off"
        const val LOW = "low"
        const val MEDIUM = "medium"
        const val HIGH = "high"
        const val UNDER_ATTACK = "under_attack"
    }
}