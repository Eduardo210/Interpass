package mx.com.nut.basecleanarquitecture.domain.model

import android.graphics.Bitmap
import mx.com.nut.basecleanarquitecture.data.entity.MovesModel

data class Tag(
    var statusTag: String,
    val date: String,
    val economicNum: String,
    var balance: String,
    val tagNumber: String,
    val tagType: String,
    val vehicleClass: String,
    val vehiclePlate: String,
    val idType: String,
    val tagImage: Bitmap,
    var getCurrentMoves: Boolean,
    var moves: ArrayList<MovesModel>,
    val esPrederterminado: String
)