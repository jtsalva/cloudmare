package dev.jtsalva.cloudmare.api.zonesettings

import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.R

data class ZoneSetting(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "value") val value: Any,
    @field:Json(name = "editable") val editable: Boolean? = null,
    @field:Json(name = "modified_on") val modifiedOn: String? = null,
    @field:Json(name = "time_remaining") val timeRemaining: Int? = null
) {

    companion object {
        const val SSL_MODE_OFF = "off"
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

        const val VALUE_OFF = "off"
        const val VALUE_ON = "on"

        const val SECURITY_LEVEL_ESSENTIALLY_OFF = "essentially_off"
        const val SECURITY_LEVEL_LOW = "low"
        const val SECURITY_LEVEL_MEDIUM = "medium"
        const val SECURITY_LEVEL_HIGH = "high"
        const val SECURITY_LEVEL_UNDER_ATTACK = "under_attack"

        const val CACHE_LEVEL_BASIC = "basic"
        const val CACHE_LEVEL_SIMPLIFIED = "simplified"
        const val CACHE_LEVEL_AGGRESSIVE = "aggressive"

        fun alwaysOnline(value: String) = ZoneSetting(ID_ALWAYS_ONLINE, value)
        fun browserCacheTtl(value: Int) = ZoneSetting(ID_BROWSER_CACHE_TTL, value)
        fun cacheLevel(value: String) = ZoneSetting(ID_CACHE_LEVEL, value)
        fun alwaysUseHttps(value: String) = ZoneSetting(ID_ALWAYS_USE_HTTPS, value)
        fun automaticHttpsRewrites(value: String) = ZoneSetting(ID_AUTOMATIC_HTTPS_REWRITES, value)
        fun opportunisticEncryption(value: String) = ZoneSetting(ID_OPPORTUNISTIC_ENCRYPTION, value)
        fun opportunisticOnion(value: String) = ZoneSetting(ID_OPPORTUNISTIC_ONION, value)
        fun ssl(value: String) = ZoneSetting(ID_SSL, value)
    }

    class SSLModeTranslator(activity: CloudMareActivity) {

        val idToReadable = activity.run {
            mapOf(
                SSL_MODE_OFF to getString(R.string.ssl_mode_off),
                SSL_MODE_FLEXIBLE to getString(R.string.ssl_mode_flexible),
                SSL_MODE_FULL to getString(R.string.ssl_mode_full),
                SSL_MODE_STRICT to getString(R.string.ssl_mode_full_strict)
            )
        }

        inline fun getReadable(id: String): String =
            idToReadable.getValue(id)

        inline fun getId(readable: String): String =
            idToReadable.filterValues { it == readable }.keys.first()

    }

    class CacheLevelTranslator(activity: CloudMareActivity) {

        val idToReadable = activity.run {
            mapOf(
                CACHE_LEVEL_BASIC to getString(R.string.cache_level_basic),
                CACHE_LEVEL_SIMPLIFIED to getString(R.string.cache_level_simplified),
                CACHE_LEVEL_AGGRESSIVE to getString(R.string.cache_level_aggressive)
            )
        }

        inline fun getReadable(id: String): String =
            idToReadable.getValue(id)

        inline fun getId(readable: String): String =
            idToReadable.filterValues { it == readable }.keys.first()

    }

}