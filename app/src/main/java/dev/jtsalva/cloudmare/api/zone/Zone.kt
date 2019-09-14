package dev.jtsalva.cloudmare.api.zone

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import dev.jtsalva.cloudmare.api.createStringArrayListOrBlank
import dev.jtsalva.cloudmare.api.readStringOrBlank

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
    @field:Json(name = "permissions") val permissions: List<String>,
    @field:Json(name = "status") val status: String,
    @field:Json(name = "paused") val paused: Boolean,
    @field:Json(name = "type") val type: String,
    @field:Json(name = "vanity_name_servers") val vanityNameServers: List<String>,
    @field:Json(name = "betas") val betas: List<String>,
    @field:Json(name = "deactivation_reason") val deactivationReason: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readInt(),
        parcel.createStringArrayListOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank(),
        parcel.createStringArrayListOrBlank(),
        parcel.createStringArrayListOrBlank(),
        parcel.readStringOrBlank(),
        parcel.readByte() != 0.toByte(),
        parcel.readStringOrBlank(),
        parcel.createStringArrayListOrBlank(),
        parcel.createStringArrayListOrBlank(),
        parcel.readStringOrBlank()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(developmentMode)
        parcel.writeStringList(originalNameServers)
        parcel.writeString(originalRegistrar)
        parcel.writeString(originalDnshost)
        parcel.writeString(createdOn)
        parcel.writeString(modifiedOn)
        parcel.writeStringList(nameServers)
        parcel.writeStringList(permissions)
        parcel.writeString(status)
        parcel.writeByte(if (paused) 1 else 0)
        parcel.writeString(type)
        parcel.writeStringList(vanityNameServers)
        parcel.writeStringList(betas)
        parcel.writeString(deactivationReason)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Zone> {
        override fun createFromParcel(parcel: Parcel): Zone {
            return Zone(parcel)
        }

        override fun newArray(size: Int): Array<Zone?> {
            return arrayOfNulls(size)
        }
    }
}