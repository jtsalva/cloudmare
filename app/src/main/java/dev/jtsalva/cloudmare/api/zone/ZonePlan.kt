package dev.jtsalva.cloudmare.api.zone

data class ZonePlan(
    val id: String,
    val name: String,
    val price: Int,
    val currency: String,
    val frequency: String
)