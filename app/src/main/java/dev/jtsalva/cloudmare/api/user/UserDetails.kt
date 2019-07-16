package dev.jtsalva.cloudmare.api.user

import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.DateString

@JsonClass(generateAdapter = true)
data class UserDetails(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val username: String,
    val telephone: String?,
    val country: String?,
    val zipcode: String?,
    val createdOn: DateString,
    val modifiedOn: DateString,
    val twoFactorAuthenticationEnabled: Boolean,
    val suspended: Boolean
)