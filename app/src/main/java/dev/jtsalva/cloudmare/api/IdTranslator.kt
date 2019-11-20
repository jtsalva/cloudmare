package dev.jtsalva.cloudmare.api

class IdTranslator<T>(private val idToReadableString: Map<T, String>) {
    fun getReadable(id: T): String =
        idToReadableString.getValue(id)

    fun getId(readable: String) =
        idToReadableString.filterValues { it == readable }.keys.first()
}