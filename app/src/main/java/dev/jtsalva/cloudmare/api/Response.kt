package dev.jtsalva.cloudmare.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

// TODO: handle null response before reaching calling code
@JsonClass(generateAdapter = true)
open class Response(
    val success: Boolean,
    val errors: List<Error> = listOf(),
    val messages: List<String> = listOf()
) {

    open val result: Any? = null

    val failure: Boolean get() = !success

    val firstErrorMessage: String get() = errors[0].mostRelevantMessage

    @JsonClass(generateAdapter = true)
    data class Error(
        val code: Int,
        val message: String,

        @Json(name = "error_chain")
        val errorChain: List<Error>?
    ) {

        val mostRelevantMessage: String get() = errorChain?.get(0)?.mostRelevantMessage ?: message

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