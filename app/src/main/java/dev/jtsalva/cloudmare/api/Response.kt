package dev.jtsalva.cloudmare.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.adapter.fit

@JsonClass(generateAdapter = true)
open class Response(
    val success: Boolean,
    val errors: List<Error> = emptyList(),
    val messages: List<String> = emptyList()
) {

    companion object {
        fun createWithErrors(vararg errors: Error): String =
            getAdapter(Response::class).toJson(
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
        // TODO: find more messages and make them more understandable
        when (code) {
            6102, 6103 -> "Invalid email or api key format"
            9041 -> "This DNS record cannot be proxied"
            9103 -> "Invalid email or api key"
            9106, 9107 -> "Missing email or api key"
            else -> message.fit(100)
        }
    }

    @JsonClass(generateAdapter = true)
    data class Error(
        @field:Json(name = "code") val code: Int,
        @field:Json(name = "message") val message: String,

        @field:Json(name = "error_chain")
        val errorChain: List<Error>? = null
    ) {

        val mostRelevantError: Error get() = errorChain?.get(0)?.mostRelevantError ?: this

    }

}