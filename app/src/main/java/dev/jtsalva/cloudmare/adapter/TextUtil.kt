package dev.jtsalva.cloudmare.adapter

fun fitText(string: String, maxLength: Int = 18) =
    if (string.length > maxLength) "${string.subSequence(0, maxLength - 4)}..." else string

fun String.fit(maxLength: Int = 18) =
    if (length > maxLength) "${subSequence(0, maxLength - 4)}..." else this