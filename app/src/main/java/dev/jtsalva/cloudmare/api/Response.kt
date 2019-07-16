package dev.jtsalva.cloudmare.api

abstract class Response {

    abstract val success: Boolean
    abstract val errors: List<Error>
    abstract val messages: List<String>
    abstract val result: Any?

    data class Error(
        val code: Int,
        val message: String
    )

}