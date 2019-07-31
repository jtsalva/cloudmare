package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.Json

data class Owner(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "email") val email: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "type") val type: String
)