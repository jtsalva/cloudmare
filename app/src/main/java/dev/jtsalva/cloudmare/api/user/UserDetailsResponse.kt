package dev.jtsalva.cloudmare.api.user

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.Response

@JsonClass(generateAdapter = true)
class UserDetailsResponse(
    success: Boolean,
    errors: List<Error> = emptyList(),
    messages: List<Message> = emptyList(),
    override val result: UserDetails? = null
) : Response(success, errors, messages)
