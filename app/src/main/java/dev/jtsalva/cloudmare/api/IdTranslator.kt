package dev.jtsalva.cloudmare.api

class IdTranslator<T>(private val idToReadableString: Map<T, String>) {
    fun indexOfId(value: T): Int =
        idToReadableString.run {
            var index = 0
            forEach {
                if (it.key == value) return index
                index += 1
            }

            return -1
        }

    fun getReadable(id: T): String =
        idToReadableString.getValue(id)

    fun getId(readable: String): T =
        idToReadableString.filterValues { it == readable }.keys.first()
}