package dev.jtsalva.cloudmare

import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting

inline fun List<ZoneSetting>.oneWithId(id: String) = find { it.id == id }!!

inline fun List<ZoneSetting>.valueAsString(id: String) =
    oneWithId(id).value as String

inline fun List<ZoneSetting>.valueAsDouble(id: String) =
    oneWithId(id).value as Double

inline fun List<ZoneSetting>.valueAsBoolean(id: String) =
    valueAsString(id) == ZoneSetting.VALUE_ON
