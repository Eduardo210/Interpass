package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_moves_detail.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.data.entity.MovesModel
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity

class MovesDetailActivity: BaseActivity() {

    lateinit var move: MovesModel

    override fun getLayoutId(): Int {
        return R.layout.activity_moves_detail
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        move = intent.extras.getParcelable("move")
        txtvDate.text = move.date.plus(" ".plus(move.hour))

        val moveID: String? = move.tipoMovimientoID
        when (moveID) {
            "1", "2", "3" -> {
                txtvMoveTitle.text = "ABONO"
                txtvVehicle.text = "Número de tag"
                txtvVehicleType.text = move.tagNumber
                linear2.visibility = View.GONE
                linear3.visibility = View.GONE
                imgMove.setImageResource(R.drawable.icon_recarga)
                txtvAmount.text = "$".plus(move.amount)
            }
            "101", "102", "103", "104" -> {
                txtvMoveTitle.text = "Cruce -".plus(move.squareName)
                txtvVehicleType.text = move.vehicle
                txtvDescriptionType.text = move.squareName
                txtvCarrilType.text = move.carril
                imgMove.setImageResource(R.drawable.icono_cruce)
                txtvAmount.text = "$".plus(move.amount)
            }
            "105" -> {
                txtvMoveTitle.text = "ESTACIONAMIENTO"
                txtvVehicle.text = "Número de tag"
                txtvVehicleType.text = move.tagNumber
                linear2.visibility = View.GONE
                linear3.visibility = View.GONE
                imgMove.setImageResource(R.drawable.icono_estacionamiento)
                txtvAmount.text = "$".plus(move.amount)
            }
        }

/*        if (move.type == "ABONO") {
            txtvMoveTitle.text = "ABONO"
            txtvVehicle.text = "Número de tag"
            txtvVehicleType.text = move.tagNumber
            linear2.visibility = View.GONE
            linear3.visibility = View.GONE
            imgMove.setImageResource(R.drawable.icon_recarga)
            txtvAmount.text = "$".plus(move.amount)
        } else {
            txtvMoveTitle.text = "Cruce-".plus(move.description)
            txtvVehicleType.text = move.vehicle
            txtvDescriptionType.text = move.squareName
            txtvCarrilType.text = move.carril
            imgMove.setImageResource(R.drawable.icon_road)
            txtvAmount.text = "$".plus(move.amount)
        }*/
    }

}