package mx.com.nut.basecleanarquitecture.data.entity

import android.os.Parcel
import android.os.Parcelable

data class PaymentsModel(var year: String?, var cvv: String?, var default: String?, var idCard: String?, var month: String?, var nickName: String?, var number: String?, var type: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(year)
        parcel.writeString(cvv)
        parcel.writeString(default)
        parcel.writeString(idCard)
        parcel.writeString(month)
        parcel.writeString(nickName)
        parcel.writeString(number)
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaymentsModel> {
        override fun createFromParcel(parcel: Parcel): PaymentsModel {
            return PaymentsModel(parcel)
        }

        override fun newArray(size: Int): Array<PaymentsModel?> {
            return arrayOfNulls(size)
        }
    }
}
