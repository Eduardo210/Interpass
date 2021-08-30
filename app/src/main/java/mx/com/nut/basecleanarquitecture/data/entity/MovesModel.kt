package mx.com.nut.basecleanarquitecture.data.entity

import android.os.Parcel
import android.os.Parcelable

data class MovesModel(var id: Int?, var date: String?, var hour: String?, var description: String?, var amount: String?,
                      var type: String?, var balance: String?, var carril: String?, var vehicle: String?,
                      var tagNumber: String?, var squareName: String?, var status: String?, var tipoMovimientoID: String?, var trnId: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
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
        parcel.writeValue(id)
        parcel.writeString(date)
        parcel.writeString(hour)
        parcel.writeString(description)
        parcel.writeString(amount)
        parcel.writeString(type)
        parcel.writeString(balance)
        parcel.writeString(carril)
        parcel.writeString(vehicle)
        parcel.writeString(tagNumber)
        parcel.writeString(squareName)
        parcel.writeString(status)
        parcel.writeString(tipoMovimientoID)
        parcel.writeString(trnId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MovesModel> {
        override fun createFromParcel(parcel: Parcel): MovesModel {
            return MovesModel(parcel)
        }

        override fun newArray(size: Int): Array<MovesModel?> {
            return arrayOfNulls(size)
        }
    }
}
