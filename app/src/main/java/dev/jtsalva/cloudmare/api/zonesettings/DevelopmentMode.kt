package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.api.DateString

data class DevelopmentMode(
    val id: String,
    val value: String,
    val editable: Boolean,
    val modifiedOn: DateString,
    val timeRemaining: Int
) {
    enum class Value {
        OFF,
        ON;

        override fun toString(): String = super.toString().toLowerCase()
    }
}