package dev.jtsalva.cloudmare.api.user

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
data class UserDetailsResponse(
    override val success: Boolean,
    override val errors: List<Response.Error>,
    override val messages: List<String>,
    override val result: UserDetails?
) : Response()