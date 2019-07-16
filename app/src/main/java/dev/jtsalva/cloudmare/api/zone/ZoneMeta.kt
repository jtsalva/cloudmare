package dev.jtsalva.cloudmare.api.zone

data class ZoneMeta(
    val pageRuleQuota: Int,
    val wildcardProxiable: Boolean,
    val phishingDetected: Boolean
)