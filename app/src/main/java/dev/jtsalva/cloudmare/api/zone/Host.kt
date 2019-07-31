package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.Json

data class Host(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "website") val website: String
)