package dev.jtsalva.cloudmare.api.zone

import com.squareup.moshi.Json

data class ZoneMeta(
    @field:Json(name = "page_rule_quota") val pageRuleQuota: Int,
    @field:Json(name = "wildcard_proxiable") val wildcardProxiable: Boolean,
    @field:Json(name = "phishing_detected") val phishingDetected: Boolean
)