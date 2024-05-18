package com.euphoiniateam.euphonia.ui
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class MidiFile(
    val uri: String? = ""
) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MidiFile> {
        override fun createFromParcel(parcel: Parcel): MidiFile {
            return MidiFile(parcel)
        }

        override fun newArray(size: Int): Array<MidiFile?> {
            return arrayOfNulls(size)
        }
    }
}
