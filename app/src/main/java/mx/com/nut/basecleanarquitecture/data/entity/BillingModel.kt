package mx.com.nut.basecleanarquitecture.data.entity

import android.os.Parcel
import android.os.Parcelable

data class BillingModel(var rfc: String?, var email: String?, var name: String?, var rfcId: String?, var isPrede : String?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rfc)
        parcel.writeString(email)
        parcel.writeString(name)
        parcel.writeString(rfcId)
        parcel.writeString(isPrede)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillingModel> {
        override fun createFromParcel(parcel: Parcel): BillingModel {
            return BillingModel(parcel)
        }

        override fun newArray(size: Int): Array<BillingModel?> {
            return arrayOfNulls(size)
        }
    }
}