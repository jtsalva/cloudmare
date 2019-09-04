package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.DateString

data class DNSRecord(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "type") var type: String,
    @field:Json(name = "name") var name: String,
    @field:Json(name = "content") var content: String,
    @field:Json(name = "proxiable") var proxiable: Boolean,
    @field:Json(name = "proxied") var proxied: Boolean,
    @field:Json(name = "ttl") var ttl: Int,
    @field:Json(name = "locked") var locked: Boolean,
    @field:Json(name = "zoneId") var zoneId: String,
    @field:Json(name = "zone_name") var zoneName: String,
    @field:Json(name = "priority") var priority: Int? = null,
    @field:Json(name = "created_on") var createdOn: DateString? = null,
    @field:Json(name = "modified_on") var modifiedOn: DateString? = null
) {
    companion object {
        const val A = "A"
        const val AAAA = "AAAA"
        const val CNAME = "CNAME"
        const val MX = "MX"
        const val LOC = "LOC"
        const val SRV = "SRV"
        const val SPF = "SPF"
        const val TXT = "TXT"
        const val NS = "NS"
        const val CAA = "CAA"
        const val PTR = "PTR"
        const val CERT = "CERT"
        const val DNSKEY = "DNSKEY"
        const val DS = "DS"
        const val NAPTR = "NAPTR"
        const val SMIMEA = "SMIMEA"
        const val SSHFP = "SSHFP"
        const val TLSA = "TLSA"
        const val URI = "URI"

        val default: DNSRecord get() =
            DNSRecord(
                id = "",
                type = A,
                name = "",
                content = "",
                proxiable = true,
                proxied = false,
                ttl = Ttl.AUTOMATIC.toInt(),
                locked = false,
                zoneId = "",
                zoneName = ""
            )
    }

    enum class Ttl(
        // 1 for automatic otherwise in seconds
        val value: Int
    ) {
        AUTOMATIC(1),

        TWO_MINUTES(120),
        FIVE_MINUTES(300),
        TEN_MINUTES(600),
        FIFTEEN_MINUTES(900),
        THIRTY_MINUTES(1800),

        ONE_HOURS(3600),
        TWO_HOURS(7200),
        FIVE_HOURS(18000),
        TWELVE_HOURS(43200),

        ONE_DAYS(86400);

        companion object {
            fun fromValue(value: Int): Ttl {
                for (ttl in values()) {
                    if (ttl.value == value) return ttl
                }

                return AUTOMATIC
            }
        }

        fun toInt(): Int = value

        fun toString(context: Context): String = context.getString(
            when (this) {
                AUTOMATIC -> R.string.ttl_automatic

                TWO_MINUTES -> R.string.ttl_two_minutes
                FIVE_MINUTES -> R.string.ttl_five_minutes
                TEN_MINUTES -> R.string.ttl_ten_minutes
                FIFTEEN_MINUTES -> R.string.ttl_fifteen_minutes
                THIRTY_MINUTES -> R.string.ttl_thirty_minutes

                ONE_HOURS -> R.string.ttl_one_hours
                TWO_HOURS -> R.string.ttl_twelve_hours
                FIVE_HOURS -> R.string.ttl_five_hours
                TWELVE_HOURS -> R.string.ttl_twelve_hours

                ONE_DAYS -> R.string.ttl_one_days
            }
        )
    }
}