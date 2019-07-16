package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.api.DateString

data class SecurityLevel(
    val id: String,
    val value: String,
    val editable: Boolean,
    val modifiedOn: DateString
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