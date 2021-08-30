package mx.com.nut.basecleanarquitecture.presentation.Commons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.custom_dialog_register.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.afterTextChanged

class CustomDialog(context: Context, var clickSetListener: OnClickSetListener, var title: String, var message: String, var success: Boolean, var action: Boolean): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_register)
        txtvDialogTitle.text = title
        txtvDialogDescription.text = message
        if (action) {
            imgDialog.setImageResource(R.drawable.cell_phone)
            layoutImg.setBackgroundResource(R.drawable.circule_green)
            btnDialogNext.isEnabled =  false
        } else if (success) {
            if (title == "Actualización") {
                imgDialog.setImageResource(R.drawable.gesture)
            } else {
                imgDialog.setImageResource(R.drawable.success)
            }
            layoutImg.setBackgroundResource(R.drawable.circule_green)
        } else {
            imgDialog.setImageResource(R.drawable.icon_x)
            layoutImg.setBackgroundResource(R.drawable.circule_red)
        }
        if (action) {
            linearInput.visibility = View.VISIBLE
        } else {
            linearInput.visibility = View.GONE
        }
        chart1.afterTextChanged {
            chart2.requestFocus()
            validData()
        }
        chart2.afterTextChanged {
            chart3.requestFocus()
            validData()
        }
        chart3.afterTextChanged {
            chart4.requestFocus()
            validData()
        }
        chart4.afterTextChanged {
            validData()
        }

        btnDialogNext.setOnClickListener {
            var code = ""
            if (message == "Tu contraseña ha cambiado, debe ingresar nuevamente con su nueva contraseña") {
                code = "logOut"
            } else if (message == context.getString(R.string.catalogError)) {
                code = context.getString(R.string.code)
            } else if (message == "No hay conexión a internet") {
                code = context.getString(R.string.internet)
            } else {
                code = chart1.text.toString().plus(chart2.text.toString().plus(chart3.text.toString().plus(chart4.text.toString())))
            }

            clickSetListener?.onClick(code, success)
            dismiss()
        }


    }

    fun validData() {
        val text1 = chart1.text.toString().trim()
        val text2 = chart2.text.toString().trim()
        val text3 = chart3.text.toString().trim()
        val text4 = chart4.text.toString().trim()
        when {
            !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty() -> {
                btnDialogNext.isEnabled = true
            } else -> {
                btnDialogNext.isEnabled = false
            }
        }
    }

    interface OnClickSetListener {
        fun onClick(code: String, success: Boolean)
    }
}