package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.IdTranslator

@Suppress("UNUSED")
@JsonClass(generateAdapter = true)
data class ZoneSetting(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: Any,
    @field:Json(name = "editable") val editable: Boolean? = null,
    @field:Json(name = "modified_on") val modifiedOn: String? = null,
    @field:Json(name = "time_remaining") val timeRemaining: Int? = null
) {

    companion object {
        const val VALUE_OFF = "off"
        const val VALUE_ON = "on"

        const val SSL_MODE_OFF = VALUE_OFF
        const val SSL_MODE_FLEXIBLE = "flexible"
        const val SSL_MODE_FULL = "full"
        const val SSL_MODE_STRICT = "strict"

        const val ID_SSL = "ssl"
        const val ID_ALWAYS_USE_HTTPS = "always_use_https"
        const val ID_OPPORTUNISTIC_ENCRYPTION = "opportunistic_encryption"
        const val ID_OPPORTUNISTIC_ONION = "opportunistic_onion"
        const val ID_AUTOMATIC_HTTPS_REWRITES = "automatic_https_rewrites"
        const val ID_CACHE_LEVEL = "cache_level"
        const val ID_ALWAYS_ONLINE = "always_online"
        const val ID_BROWSER_CACHE_TTL = "browser_cache_ttl"
        const val ID_IPV6 = "ipv6"
        const val ID_WEBSOCKETS = "websockets"
        const val ID_PSEUDO_IPV4 = "pseudo_ipv4"
        const val ID_IP_GELOCATION = "ip_geolocation"

        const val SECURITY_LEVEL_ESSENTIALLY_OFF = "essentially_off"
        const val SECURITY_LEVEL_LOW = "low"
        const val SECURITY_LEVEL_MEDIUM = "medium"
        const val SECURITY_LEVEL_HIGH = "high"
        const val SECURITY_LEVEL_UNDER_ATTACK = "under_attack"

        const val CACHE_LEVEL_BASIC = "basic"
        const val CACHE_LEVEL_SIMPLIFIED = "simplified"
        const val CACHE_LEVEL_AGGRESSIVE = "aggressive"

        const val PSEUDO_IPV4_OFF = VALUE_OFF
        const val PSEUDO_IPV4_ADD_HEADER = "add_header"
        const val PSEUDO_IPV4_OVERWRITE_HEADER = "overwrite_header"

        fun alwaysOnline(value: String) = ZoneSetting(ID_ALWAYS_ONLINE, value)
        fun browserCacheTtl(value: Int) = ZoneSetting(ID_BROWSER_CACHE_TTL, value)
        fun cacheLevel(value: String) = ZoneSetting(ID_CACHE_LEVEL, value)
        fun alwaysUseHttps(value: String) = ZoneSetting(ID_ALWAYS_USE_HTTPS, value)
        fun automaticHttpsRewrites(value: String) = ZoneSetting(ID_AUTOMATIC_HTTPS_REWRITES, value)
        fun opportunisticEncryption(value: String) = ZoneSetting(ID_OPPORTUNISTIC_ENCRYPTION, value)
        fun opportunisticOnion(value: String) = ZoneSetting(ID_OPPORTUNISTIC_ONION, value)
        fun ssl(value: String) = ZoneSetting(ID_SSL, value)
        fun ipv6(value: String) = ZoneSetting(ID_IPV6, value)
        fun webSockets(value: String) = ZoneSetting(ID_WEBSOCKETS, value)
        fun pseudoIpv4(value: String) = ZoneSetting(ID_PSEUDO_IPV4, value)
        fun ipGeolocation(value: String) = ZoneSetting(ID_IP_GELOCATION, value)

        fun cacheLevelTranslator(activity: CloudMareActivity) =
            IdTranslator(
                activity.run {
                    mapOf(
                        CACHE_LEVEL_BASIC to getString(R.string.cache_level_basic),
                        CACHE_LEVEL_SIMPLIFIED to getString(R.string.cache_level_simplified),
                        CACHE_LEVEL_AGGRESSIVE to getString(R.string.cache_level_aggressive)
                    )
                }
            )

        fun sslModeTranslator(activity: CloudMareActivity) =
            IdTranslator(
                activity.run {
                    mapOf(
                        SSL_MODE_OFF to getString(R.string.ssl_mode_off),
                        SSL_MODE_FLEXIBLE to getString(R.string.ssl_mode_flexible),
                        SSL_MODE_FULL to getString(R.string.ssl_mode_full),
                        SSL_MODE_STRICT to getString(R.string.ssl_mode_full_strict)
                    )
                }
            )

        fun pseudoIpv4Translator(activity: CloudMareActivity) =
            IdTranslator(
                activity.run {
                    mapOf(
                        PSEUDO_IPV4_OFF to getString(R.string.network_pseudo_ipv4_off),
                        PSEUDO_IPV4_ADD_HEADER to getString(R.string.network_pseudo_ipv4_add_header),
                        PSEUDO_IPV4_OVERWRITE_HEADER to getString(R.string.network_pseudo_ipv4_overwrite_header)
                    )
                }
            )
    }
}