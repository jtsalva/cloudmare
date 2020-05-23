package dev.jtsalva.cloudmare.api.dns

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.IdTranslator
import dev.jtsalva.cloudmare.api.readStringOrBlank

@Suppress("UNUSED")
data class DNSRecord(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "type") var type: String,
    @field:Json(name = "name") var name: String,
    @field:Json(name = "content") var content: String,
    @field:Json(name = "proxiable") var proxiable: Boolean,
    @field:Json(name = "proxied") var proxied: Boolean,
    @field:Json(name = "ttl") var ttl: Int,
    @field:Json(name = "locked") var locked: Boolean,
    @field:Json(name = "zone_id") var zoneId: String,
    @field:Json(name = "zone_name") var zoneName: String,
    @field:Json(name = "priority") var priority: Int? = null,
    @field:Json(name = "created_on") var createdOn: String? = null,
    @field:Json(name = "modified_on") var modifiedOn: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

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

        const val TTL_RESPECT_EXISTING_HEADERS = 0
        const val TTL_AUTOMATIC = 1
        const val TTL_TWO_MINUTES = 120
        const val TTL_FIVE_MINUTES = 300
        const val TTL_TEN_MINUTES = 600
        const val TTL_FIFTEEN_MINUTES = 900
        const val TTL_THIRTY_MINUTES = 1800
        const val TTL_ONE_HOURS = 3600
        const val TTL_TWO_HOURS = 7200
        const val TTL_THREE_HOURS = 10800
        const val TTL_FOUR_HOURS = 14400
        const val TTL_FIVE_HOURS = 18000
        const val TTL_EIGHT_HOURS = 28800
        const val TTL_TWELVE_HOURS = 43200
        const val TTL_SIXTEEN_HOURS = 57600
        const val TTL_TWENTY_HOURS = 72000
        const val TTL_ONE_DAYS = 86400
        const val TTL_TWO_DAYS = 172800
        const val TTL_THREE_DAYS = 259200
        const val TTL_FOUR_DAYS = 345600
        const val TTL_FIVE_DAYS = 432000
        const val TTL_EIGHT_DAYS = 691200
        const val TTL_SIXTEEN_DAYS = 1382400
        const val TTL_TWENTY_FOUR_DAYS = 2073600
        const val TTL_ONE_MONTHS = 2678400
        const val TTL_TWO_MONTHS = 5356800
        const val TTL_SIX_MONTHS = 16070400
        const val TTL_ONE_YEARS = 31536000

        const val SORT_BY_TYPE = "type"
        const val SORT_BY_NAME = "name"
        const val SORT_BY_CONTENT = "content"
        const val SORT_BY_TTL = "ttl"
        const val SORT_BY_PROXIED = "proxied"

        val default: DNSRecord get() =
            DNSRecord(
                id = "",
                type = A,
                name = "",
                content = "",
                proxiable = true,
                proxied = false,
                ttl = TTL_AUTOMATIC,
                locked = false,
                zoneId = "",
                zoneName = ""
            )

        @JvmField val CREATOR = object : Parcelable.Creator<DNSRecord> {
            override fun createFromParcel(parcel: Parcel) = DNSRecord(parcel)

            override fun newArray(size: Int): Array<DNSRecord?> = arrayOfNulls(size)
        }

        fun ttlTranslator(activity: CloudMareActivity) =
            IdTranslator(
                activity.run {
                    mapOf(
                        TTL_RESPECT_EXISTING_HEADERS to getString(R.string.ttl_respect_existing_headers),
                        TTL_AUTOMATIC to getString(R.string.ttl_automatic),

                        TTL_TWO_MINUTES to getString(R.string.ttl_two_minutes),
                        TTL_FIVE_MINUTES to getString(R.string.ttl_five_minutes),
                        TTL_TEN_MINUTES to getString(R.string.ttl_ten_minutes),
                        TTL_FIFTEEN_MINUTES to getString(R.string.ttl_fifteen_minutes),
                        TTL_THIRTY_MINUTES to getString(R.string.ttl_thirty_minutes),

                        TTL_ONE_HOURS to getString(R.string.ttl_one_hours),
                        TTL_TWO_HOURS to getString(R.string.ttl_two_hours),
                        TTL_THREE_HOURS to getString(R.string.ttl_three_hours),
                        TTL_FOUR_HOURS to getString(R.string.ttl_four_hours),
                        TTL_FIVE_HOURS to getString(R.string.ttl_five_hours),
                        TTL_EIGHT_HOURS to getString(R.string.ttl_eight_hours),
                        TTL_TWELVE_HOURS to getString(R.string.ttl_twelve_hours),
                        TTL_SIXTEEN_HOURS to getString(R.string.ttl_sixteen_hours),
                        TTL_TWENTY_HOURS to getString(R.string.ttl_twenty_hours),

                        TTL_ONE_DAYS to getString(R.string.ttl_one_days),
                        TTL_TWO_DAYS to getString(R.string.ttl_two_days),
                        TTL_THREE_DAYS to getString(R.string.ttl_three_days),
                        TTL_FOUR_DAYS to getString(R.string.ttl_four_days),
                        TTL_FIVE_DAYS to getString(R.string.ttl_five_days),
                        TTL_EIGHT_DAYS to getString(R.string.ttl_eight_days),
                        TTL_SIXTEEN_DAYS to getString(R.string.ttl_sixteen_days),
                        TTL_TWENTY_FOUR_DAYS to getString(R.string.ttl_twenty_four_days),

                        TTL_ONE_MONTHS to getString(R.string.ttl_one_months),
                        TTL_TWO_MONTHS to getString(R.string.ttl_two_months),
                        TTL_SIX_MONTHS to getString(R.string.ttl_six_months),
                        TTL_ONE_YEARS to getString(R.string.ttl_one_years)
                    )
                }
            )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(type)
        parcel.writeString(name)
        parcel.writeString(content)
        parcel.writeByte(if (proxiable) 1 else 0)
        parcel.writeByte(if (proxied) 1 else 0)
        parcel.writeInt(ttl)
        parcel.writeByte(if (locked) 1 else 0)
        parcel.writeString(zoneId)
        parcel.writeString(zoneName)
        parcel.writeValue(priority)
        parcel.writeString(createdOn)
        parcel.writeString(modifiedOn)
    }

    override fun describeContents(): Int {
        return 0
    }
}
