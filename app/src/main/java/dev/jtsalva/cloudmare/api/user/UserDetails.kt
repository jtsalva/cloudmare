package dev.jtsalva.cloudmare.api.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDetails(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "first_name") val firstName: String?,
    @field:Json(name = "last_name") val lastName: String?,
    @field:Json(name = "username") val username: String,
    @field:Json(name = "telephone") val telephone: String?,
    @field:Json(name = "country") val country: String?,
    @field:Json(name = "zipcode") val zipcode: String?,
    @field:Json(name = "created_on") val createdOn: String,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "two_factor_authentication_enabled") val twoFactorAuthenticationEnabled: Boolean,
    @field:Json(name = "suspended") val suspended: Boolean
)