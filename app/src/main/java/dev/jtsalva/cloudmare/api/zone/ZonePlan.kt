package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.Json

data class ZonePlan(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "price") val price: Int,
    @field:Json(name = "currency") val currency: String,
    @field:Json(name = "frequency") val frequency: String
)