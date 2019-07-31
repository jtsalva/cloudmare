package dev.jtsalva.cloudmare.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class Response(
    val success: Boolean,
    val errors: List<Error> = emptyList(),
    val messages: List<String> = emptyList()
) {

    companion object {
        fun withErrors(vararg errors: Error): String =
            getAdapter(Response::class.java).toJson(
                Response(success = false, errors = errors.run {
                    mutableListOf<Error>().apply {
                        for (err in this@run) add(err)
                    }
                })
            )
    }

    open val result: Any? = null

    val failure: Boolean get() = !success

    val firstErrorMessage: String get() = with(errors[0].mostRelevantError) {
        // Intercept and replace user obscure error messages
        when (code) {
            6103 -> "Invalid api key format"
            9103 -> "Invalid email or api key"
            9041 -> "This DNS record cannot be proxied"
            else -> message
        }
    }

    @JsonClass(generateAdapter = true)
    data class Error(
        val code: Int,
        val message: String,

        @field:Json(name = "error_chain")
        val errorChain: List<Error>? = null
    ) {

        val mostRelevantError: Error get() = errorChain?.get(0)?.mostRelevantError ?: this

    }

    enum class Status(
        val code: Int
    ) {
        OK(200),
        NOT_MODIFIED(304),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        TOO_MANY_REQUESTS(429),
        METHOD_NOT_ALLOWED(405),
        UNSUPPORTED_MEDIA_TYPE(415)
    }

}