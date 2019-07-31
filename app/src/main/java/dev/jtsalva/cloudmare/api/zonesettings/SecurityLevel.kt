package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.api.DateString

data class SecurityLevel(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "editable") val editable: Boolean,
    @field:Json(name = "modified_on") val modifiedOn: DateString
) {
    enum class Value {
        ESSENTIALLY_OFF,
        LOW,
        MEDIUM,
        HIGH,
        UNDER_ATTACK;

        override fun toString(): String = super.toString().toLowerCase()
    }
}