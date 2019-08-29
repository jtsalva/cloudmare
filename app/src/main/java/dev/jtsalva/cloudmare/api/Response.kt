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
        object Status {
            const val OK = 200
            const val NOT_MODIFIED = 304
            const val BAD_REQUEST = 400
            const val UNAUTHORIZED = 401
            const val FORBIDDEN = 403
            const val TOO_MANY_REQUESTS = 429
            const val METHOD_NOT_ALLOWED = 405
            const val UNSUPPORTED_MEDIA_TYPE = 415
        }

        fun createWithErrors(vararg errors: Error): String =
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

}