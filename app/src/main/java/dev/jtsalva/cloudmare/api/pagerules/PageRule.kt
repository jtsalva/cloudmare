package dev.jtsalva.cloudmare.api.pagerules

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.toTtlString
import java.net.URI

data class PageRule(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "targets") val targets: List<Target>,
    @field:Json(name = "actions") val actions: List<Action>,
    @field:Json(name = "priority") var priority: Int,
    @field:Json(name = "status") var status: String,
    @field:Json(name = "modified_on") val modifiedOn: String,
    @field:Json(name = "created_on") val createdOn: String
) {

    companion object {
        const val ACTIVE = "active"
        const val DISABLED = "disabled"

        object ActionId {
            const val ALWAYS_ONLINE = "always_online"
            const val ALWAYS_USE_HTTPS = "always_use_https"
            const val AUTOMATIC_HTTPS_REWRITES = "automatic_https_rewrites"
            const val BROWSER_CACHE_TTL = "browser_cache_ttl"
            const val BROWSER_CHECK = "browser_check"
            const val BYPASS_CACHE_ON_COOKIE = "bypass_cache_on_cookie"
            const val CACHE_BY_DEVICE_TYPE = "cache_by_device_type"
            const val CACHE_DECEPTION_ARMOR = "cache_deception_armor"
            const val CACHE_LEVEL = "cache_level"
            const val CACHE_ON_COOKIE = "cache_on_cookie"
            const val DISABLE_APPS = "disable_apps"
            const val DISABLE_PERFORMANCE = "disable_performance"
            const val DISABLE_RAILGUN = "disable_railgun"
            const val DISABLE_SECURITY = "disable_security"
            const val EDGE_CACHE_TTL = "edge_cache_ttl"
            const val EMAIL_OBFUSCATION = "email_obfuscation"
            const val EXPLICIT_CACHE_CONTROL = "explicit_cache_control"
            const val FORWARDING_URL = "forwarding_url"
            const val HOST_HEADER_OVERRIDE = "host_header_override"
            const val IP_GEOLOCATION = "ip_geolocation"
            const val MINIFY = "minify"
            const val MIRAGE = "mirage"
            const val OPPORTUNISTIC_ENCRYPTION = "opportunistic_encryption"
            const val ORIGIN_ERROR_PAGE_PASS_THRU = "origin_error_page_pass_thru"
            const val POLISH = "polish"
            const val RESOLVE_OVERRIDE = "resolve_override"
            const val RESPECT_STRONG_ETAG = "respect_strong_etag"
            const val RESPONSE_BUFFERING = "response_buffering"
            const val ROCKET_LOADER = "rocket_loader"
            const val SECURITY_LEVEL = "security_level"
            const val SERVER_SIDE_EXCLUDE = "server_side_exclude"
            const val SORT_QUERY_STRING_FOR_CACHE = "sort_query_string_for_cache"
            const val SSL = "ssl"
            const val TRUE_CLIENT_IP_HEADER = "true_client_ip_header"
            const val WAF = "waf"
        }

        val Actions = mapOf(
            ActionId.ALWAYS_ONLINE to "Always Online",
            ActionId.ALWAYS_USE_HTTPS to "Always Use HTTPS",
            ActionId.AUTOMATIC_HTTPS_REWRITES to "Automatic HTTPS Rewrites",
            ActionId.BROWSER_CACHE_TTL to "Browser Cache TTL",
            ActionId.BROWSER_CHECK to "Browser Integrity Check",
            ActionId.BYPASS_CACHE_ON_COOKIE to "Bypass Cache on Cookie",
            ActionId.CACHE_BY_DEVICE_TYPE to "Cache By Device Type",
            ActionId.CACHE_DECEPTION_ARMOR to "Cache Deception Armor",
            ActionId.CACHE_LEVEL to "Cache Level",
            ActionId.CACHE_ON_COOKIE to "Cache On Cookie",
            ActionId.DISABLE_APPS to "Disable Apps",
            ActionId.DISABLE_PERFORMANCE to "Disable Performance",
            ActionId.DISABLE_RAILGUN to "Disable Railgun",
            ActionId.DISABLE_SECURITY to "Disable Security",
            ActionId.EDGE_CACHE_TTL to "Edge Cache TTL",
            ActionId.EMAIL_OBFUSCATION to "Email Obfuscation",
            ActionId.EXPLICIT_CACHE_CONTROL to "Origin Cache Control",
            ActionId.FORWARDING_URL to "Forwarding URL",
            ActionId.HOST_HEADER_OVERRIDE to "Host Header Override",
            ActionId.IP_GEOLOCATION to "IP Geolocation Header",
            ActionId.MINIFY to "Minify",
            ActionId.MIRAGE to "Mirage",
            ActionId.OPPORTUNISTIC_ENCRYPTION to "Opportunistic Encryption",
            ActionId.ORIGIN_ERROR_PAGE_PASS_THRU to "Origin Error Page Pass-thru",
            ActionId.POLISH to "Polish",
            ActionId.RESOLVE_OVERRIDE to "Resolve Override",
            ActionId.RESPECT_STRONG_ETAG to "Respect Strong ETags",
            ActionId.RESPONSE_BUFFERING to "Response Buffering",
            ActionId.ROCKET_LOADER to "Rocker Loader",
            ActionId.SECURITY_LEVEL to "Security Level",
            ActionId.SERVER_SIDE_EXCLUDE to "Server Side Excludes",
            ActionId.SORT_QUERY_STRING_FOR_CACHE to "Query String Sort",
            ActionId.SSL to "SSL",
            ActionId.TRUE_CLIENT_IP_HEADER to "True Client IP Header",
            ActionId.WAF to "Web Application Firewall"
        )
    }

    @JsonClass(generateAdapter = true)
    data class Target(
        @field:Json(name = "target") var target: String,
        @field:Json(name = "constraint") var constraint: Constraint
    ) {

        @JsonClass(generateAdapter = true)
        data class Constraint(
            @field:Json(name = "operator") var operator: String,
            @field:Json(name = "value") var value: String
        )
    }

    @JsonClass(generateAdapter = true)
    data class Action(
        @field:Json(name = "id") val id: String,
        @field:Json(name = "value") var value: Any? = null
    ) {

        override fun toString(): String = when (id) {
            ActionId.FORWARDING_URL ->
                ForwardingUrl.fromMap(value!!).let { forwardingUrl ->
                    val domain = URI(forwardingUrl.url).host
                    
                    "Forward: ${forwardingUrl.statusCode}: $domain"
                }

            ActionId.MINIFY ->
                Minify.fromMap(value!!).let { minify ->
                    "Minify: html: ${minify.html}, css: ${minify.css}, javascript: ${minify.javascript}"
                }

//            ActionId.ALWAYS_USE_HTTPS -> "OBJECT STRUCTURE UNKNOWN" // TODO: find out structure

            ActionId.DISABLE_APPS, ActionId.DISABLE_PERFORMANCE, ActionId.DISABLE_SECURITY ->
                Actions.getValue(id)

            ActionId.BROWSER_CACHE_TTL, ActionId.EDGE_CACHE_TTL ->
            {
                val seconds = value as Double

                "${seconds.toTtlString()} ${Actions.getValue(id)}"
            }

            else -> "${Actions.getValue(id)}: $value"
        }

        @JsonClass(generateAdapter = true)
        data class ForwardingUrl(
            @field:Json(name = "url") var url: String,
            @field:Json(name = "status_code") var statusCode: Int
        ) {
            companion object {
                @Suppress("UNCHECKED_CAST")
                fun fromMap(map: Any) = (map as Map<String, Any>).run {
                    ForwardingUrl(
                        get("url") as String,
                        (get("status_code") as Double).toInt()
                    )
                }
            }
        }

        @JsonClass(generateAdapter = true)
        data class Minify(
            @field:Json(name = "html") var html: String,
            @field:Json(name = "css") var css: String,
            @field:Json(name = "js") var javascript: String
        ) {
            companion object {
                @Suppress("UNCHECKED_CAST")
                fun fromMap(map: Any) = (map as Map<String, String>).run {
                    Minify(
                        get("html")!!,
                        get("css")!!,
                        get("js")!!
                    )
                }
            }
        }
    }
}
