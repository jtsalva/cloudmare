package dev.jtsalva.cloudmare.api

import kotlin.math.round

inline val Double.roundedHours: Double get() = round(this / (60 * 60))
