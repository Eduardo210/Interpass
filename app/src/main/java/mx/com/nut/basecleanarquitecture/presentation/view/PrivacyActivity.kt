package mx.com.nut.basecleanarquitecture.presentation.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import kotlinx.android.synthetic.main.activity_privacy.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponsePrivacy
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity

class PrivacyActivity: BaseActivity(), CustomDialog.OnClickSetListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_privacy
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        getPrivacy()
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
    }

    fun getPrivacy() {
        startLoader()
        RestService.baseRequest2(this, Constans.METHOD_PRIVACY, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponsePrivacy
                val text = response.mensaje
                val spannable = SpannableStringBuilder(response.mensaje)
                spannable.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                text!!.indexOf("AVISO DE PRIVACIDAD."), // start
                    text!!.indexOf("AVISO DE PRIVACIDAD.") + "AVISO DE PRIVACIDAD.".length, // end
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("ACCIONA INGENIERÍA Y DESARROLLO EMPRESARIAL, S.A. DE C.V."), // start
                    text!!.indexOf("ACCIONA INGENIERÍA Y DESARROLLO EMPRESARIAL, S.A. DE C.V.") + "ACCIONA INGENIERÍA Y DESARROLLO EMPRESARIAL, S.A. DE C.V.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("Cómo contactarnos."), // start
                    text!!.indexOf("Cómo contactarnos.") + "Cómo contactarnos.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("Datos personales que se recaban."), // start
                    text!!.indexOf("Datos personales que se recaban.") + "Datos personales que se recaban.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("Alcance del presente “Aviso de Privacidad”."), // start
                    text!!.indexOf("Alcance del presente “Aviso de Privacidad”.") + "Alcance del presente “Aviso de Privacidad”.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("Finalidades del tratamiento de datos personales."), // start
                    text!!.indexOf("Finalidades del tratamiento de datos personales.") + "Finalidades del tratamiento de datos personales.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("A.\tFinalidades Principales."), // start
                    text!!.indexOf("A.\tFinalidades Principales.") + "A.\tFinalidades Principales.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("B.\tFinalidades Secundarias."), // start
                    text!!.indexOf("B.\tFinalidades Secundarias.") + "B.\tFinalidades Secundarias.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.titleBlack)),
                    text!!.indexOf("Clasificación de los datos personales que se obtienen."), // start
                    text!!.indexOf("Clasificación de los datos personales que se obtienen.") + "Clasificación de los datos personales que se obtienen.".length, // end
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                txtPrivacy.text = spannable

            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        })
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            onBackPressed()
        }
    }
}