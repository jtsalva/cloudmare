package dev.jtsalva.cloudmare.api

import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting

inline fun Boolean.toApiValue(): String = if (this) ZoneSetting.VALUE_ON else ZoneSetting.VALUE_OFF
