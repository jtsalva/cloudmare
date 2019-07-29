package dev.jtsalva.cloudmare.api.zonesettings

import dev.jtsalva.cloudmare.api.DateString

data class ZoneSetting(
    val id: String,
    val value: String,
    val editable: Boolean,
    val modifiedOn: DateString
)