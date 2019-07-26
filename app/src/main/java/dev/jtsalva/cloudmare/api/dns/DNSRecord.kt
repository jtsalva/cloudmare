package dev.jtsalva.cloudmare.api.dns

import android.content.Context
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.DateString
import java.security.InvalidParameterException

data class DNSRecord(
    val id: String,
    var type: String,
    var name: String,
    var content: String,
    var proxiable: Boolean,
    var proxied: Boolean,
    var ttl: Int,
    var locked: Boolean,
    var zoneId: String,
    var zoneName: String,
    var priority: Int? = null,
    @Json(name = "created_on") var createdOn: DateString? = null,
    @Json(name = "modified_on") var modifiedOn: DateString? = null
) {
    enum class Type {
        A, AAAA, CNAME, MX,
        LOC, SRV, SPF, TXT, NS,
        CAA, PTR, CERT, DNSKEY,
        DS, NAPTR, SMIMEA,
        SSHFP, TLSA, URI;

        companion object {
            fun fromString(string: String): Type {
                for (type in values()) {
                    if (type.toString() == string) return type
                }

                throw InvalidParameterException("$string isn't a valid TTL")
            }
        }
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