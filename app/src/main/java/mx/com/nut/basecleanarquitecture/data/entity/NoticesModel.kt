package mx.com.nut.basecleanarquitecture.data.entity

import android.os.Parcel
import android.os.Parcelable

data class NoticesModel(var title: String?, var description: String?, var image: String?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoticesModel> {
        override fun createFromParcel(parcel: Parcel): NoticesModel {
            return NoticesModel(parcel)
        }

        override fun newArray(size: Int): Array<NoticesModel?> {
            return arrayOfNulls(size)
        }
    }
}