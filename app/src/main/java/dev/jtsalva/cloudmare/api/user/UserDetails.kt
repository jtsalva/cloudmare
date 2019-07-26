package dev.jtsalva.cloudmare.api.user

import com.squareup.moshi.Json
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
    @Json(name = "created_on") val createdOn: DateString,
    @Json(name = "modified_on") val modifiedOn: DateString,
    @Json(name = "two_factor_authentication_enabled") val twoFactorAuthenticationEnabled: Boolean,
    val suspended: Boolean
)