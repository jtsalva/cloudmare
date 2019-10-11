package dev.jtsalva.cloudmare.api.dns

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.CloudMareActivity
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.api.readStringOrBlank

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

        const val TTL_AUTOMATIC = 1
        const val TTL_TWO_MINUTES = 120
        const val TTL_FIVE_MINUTES = 300
        const val TTL_TEN_MINUTES = 600
        const val TTL_FIFTEEN_MINUTES = 900
        const val TTL_THIRTY_MINUTES = 1800
        const val TTL_ONE_HOURS = 3600
        const val TTL_TWO_HOURS = 7200
        const val TTL_FIVE_HOURS = 18000
        const val TTL_TWELVE_HOURS = 43200
        const val TTL_ONE_DAYS = 86400

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
    }

    class TtlTranslator(activity: CloudMareActivity) {

        val valueToReadable = activity.run {
            mapOf(
                TTL_AUTOMATIC to getString(R.string.ttl_automatic),

                TTL_TWO_MINUTES to getString(R.string.ttl_two_minutes),
                TTL_FIVE_MINUTES to getString(R.string.ttl_five_minutes),
                TTL_TEN_MINUTES to getString(R.string.ttl_ten_minutes),
                TTL_FIFTEEN_MINUTES to getString(R.string.ttl_fifteen_minutes),
                TTL_THIRTY_MINUTES to getString(R.string.ttl_thirty_minutes),

                TTL_ONE_HOURS to getString(R.string.ttl_one_hours),
                TTL_TWO_HOURS to getString(R.string.ttl_two_hours),
                TTL_FIVE_HOURS to getString(R.string.ttl_five_hours),
                TTL_TWELVE_HOURS to getString(R.string.ttl_twelve_hours),

                TTL_ONE_DAYS to getString(R.string.ttl_one_days)
            )
        }

        inline fun getReadable(value: Int): String =
            valueToReadable.getValue(value)

        inline fun getValue(readable: String): Int =
            valueToReadable.filterValues { it == readable }.keys.first()

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