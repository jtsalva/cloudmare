package dev.jtsalva.cloudmare.api

inline fun Boolean.toApiValue(): String = if (this) "on" else "off"