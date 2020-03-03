package dev.jtsalva.cloudmare.api.zone

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import dev.jtsalva.cloudmare.api.createStringArrayListOrBlank
import dev.jtsalva.cloudmare.api.readStringOrBlank

@JsonClass(generateAdapter = true)
data class Zone(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "name") val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readStringOrBlank(),
        parcel.readStringOrBlank()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Zone> {
        override fun createFromParcel(parcel: Parcel) = Zone(parcel)

        override fun newArray(size: Int) = arrayOfNulls<Zone>(size)
    }
}