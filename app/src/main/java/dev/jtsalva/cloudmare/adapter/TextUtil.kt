package dev.jtsalva.cloudmare.adapter

inline fun String.fit(maxLength: Int = 26) =
    if (length > maxLength) "${subSequence(0, maxLength - 4)}…" else this