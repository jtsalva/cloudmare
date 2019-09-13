package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.Json

data class Zone(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "development_mode") val developmentMode: Int,
    @field:Json(name = "original_name_servers") val originalNameServers: List<String>,
    @field:Json(name = "original_registrar") val originalRegistrar: String,
    @field:Json(name = "original_dns_host") val originalDnshost: String,
    @field:Json(name = "created_on") val createdOn: String,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "name_servers") val nameServers: List<String>,
    @field:Json(name = "owner") val owner: Owner,
    @field:Json(name = "permissions") val permissions: List<String>,
    @field:Json(name = "plan") val plan: ZonePlan,
    @field:Json(name = "plan_pending") val planPending: ZonePlan,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "paused") val paused: Boolean,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "host") val host: Host,
    @field:Json(name = "vanity_name_servers") val vanityNameServers: List<String>,
    @field:Json(name = "betas") val betas: List<String>,
    @field:Json(name = "deactivation_reason") val deactivationReason: String,
    @field:Json(name = "meta") val meta: ZoneMeta
)