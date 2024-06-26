package com.euphoiniateam.euphonia.domain.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKUser(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val birthDate: String = "",
    val photo: String = "",
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(birthDate)
        parcel.writeString(photo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKUser> {
        override fun createFromParcel(parcel: Parcel): VKUser {
            return VKUser(parcel)
        }

        override fun newArray(size: Int): Array<VKUser?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) =
            VKUser(
                id = json.optLong("id", 0),
                firstName = json.optString("first_name", ""),
                lastName = json.optString("last_name", ""),
                birthDate = json.optString("bdate", ""),
                photo = json.optString("photo_200", ""),
            )
    }
}
