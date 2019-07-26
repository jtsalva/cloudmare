package dev.jtsalva.cloudmare.adapter

fun fitText(string: String, maxLength: Int = 18) =
    if (string.length > maxLength) "${string.subSequence(0, maxLength - 4)}..." else string