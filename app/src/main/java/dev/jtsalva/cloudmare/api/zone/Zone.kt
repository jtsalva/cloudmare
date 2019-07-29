package dev.jtsalva.cloudmare.api.zone

import dev.jtsalva.cloudmare.api.DateString

data class Zone(
    val id: String,
    val name: String,
    val developmentMode: Int,
    val originalNameServers: List<String>,
    val originalRegistrar: String,
    val originalDnshost: String,
    val createdOn: DateString,
    val modiiedOn: DateString,
    val nameServers: List<String>,
    val owner: Owner,
    val permissions: List<String>,
    val plan: ZonePlan,
    val planPending: ZonePlan,
    val status: String,
    val paused: Boolean,
    val type: String,
    val host: Host,
    val vanityNameServers: List<String>,
    val betas: List<String>,
    val deactivationReason: String,
    val meta: ZoneMeta
)