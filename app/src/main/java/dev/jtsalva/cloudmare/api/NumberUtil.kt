package dev.jtsalva.cloudmare.api

const val MINUTE = 60.0
const val HOUR = MINUTE * 60.0
const val DAY = HOUR * 24.0

inline fun Double.toTtlString(): String = when {
    this < HOUR -> "${this / MINUTE} minutes"

    this < DAY -> "${this / HOUR} hours"

    else -> "${this / DAY} days"
}
