package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_billing.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.commons.afterTextChanged
import mx.com.nut.basecleanarquitecture.data.entity.BillingModel
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.util.*

class AddBillingActivity: BaseActivity(), CustomDialog.OnClickSetListener {
    var userID = ""
    var title: String? = ""
    var typeEdit = false
    lateinit var billing: BillingModel

    override fun getLayoutId(): Int {
        return R.layout.activity_add_billing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        addListeners()
        setData()
    }

    private fun setData() {
        title = intent.extras.getString("title")
        if (title != "") {
            typeEdit = true
            billing = intent.extras.getParcelable("billing")
            txtvTitle.text = title
            btnAddBilling.text = getString(R.string.saveTag)
            txtRFC.setText(billing.rfc)
            txtRFC.isFocusable = false
            txtRFC.isClickable = false
            txtEmail.setText(billing.email)
            txtName.setText(billing.name)

            if(billing.isPrede == "True"){
                checkbox1.isChecked = true
                checkbox1.isEnabled = false
            }
        }
    }

    private fun addListeners() {
        txtRFC.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(13))
        txtName.afterTextChanged {
            setColorBlue(txtvName)
            validData()
        }

        txtRFC.afterTextChanged {
            setColorBlue(txtvRFC)
            validData()
        }

        txtEmail.afterTextChanged {
            setColorBlue(txtvEmail)
            validData()
        }
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnAddBilling.setOnClickListener {
            btnAddBilling.isEnabled = false
            if (validateData()) {
                addBilling()
            } else {
                btnAddBilling.isEnabled = false
            }
        }
    }

    fun validateData(): Boolean {
        if (isEmailValid(txtEmail.text.toString())) {
            runOnUiThread {
                txtvEmailError.text = "Correo electrónico inválido"
                setErrorView(txtvEmail, txtvEmailError, txtEmail)
            }
            return false
        }
        val rfc = txtRFC.text.toString().trim()
        val regex = "^([A-ZÑ\\x26]{3,4}([0-9]{2})(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[0-1]))((-)?([A-Z\\d]{3}))?\$".toRegex()
        if (!rfc.matches(regex)) {
            runOnUiThread {
                txtvRFCError.text = "RFC inválido."
                setErrorView(txtvRFC, txtvRFCError, txtRFC)
            }
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setErrorView(textView: TextView, errorTextView: TextView, editText: EditText) {
        editText.isEnabled = false
        errorTextView.visibility = View.VISIBLE
        textView.setTextColor(resources.getColor(R.color.errorRed))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    editText.isEnabled = true
                    errorTextView.visibility = View.GONE
                    if (editText.isFocused) {
                        textView.setTextColor(resources.getColor(R.color.blue))
                    } else {
                        textView.setTextColor(resources.getColor(mx.com.nut.basecleanarquitecture.R.color.grayText))
                    }

                }
            }
        }, 1000)
    }

    private fun addBilling() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        val rfc = txtRFC.text.toString().trim()
        val name = txtName.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        var isPredeterminado = ""

        if(checkbox1.isChecked){
            isPredeterminado = "true"
        } else {
            isPredeterminado = "false"
        }

        var opType = "3"
        if (typeEdit) {
            opType = "2"
        }
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to opType, "RFC" to rfc, "Nombre_o_RazonSocial" to name, "email" to email , "esPredeterminado" to isPredeterminado)

        RestService.baseRequest(this, RequestEnum.GETBILLINGDATA, Constans.METHOD_BILLING_DATA, params, { response ->
            stopLoader()
            runOnUiThread {
                Session.updateBillingData = true
                if (typeEdit) {
                    val dialog =
                        CustomDialog(this, this, "Datos actualizados", "¡Listo!, tus datos han sido actualizados correctamente.", success = true, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                } else {
                    val dialog =
                        CustomDialog(this, this, "Datos agregados", "¡Listo!, tus datos han sido agregados correctamente.", success = true, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }

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

    private fun validData() {
        val text1 = txtName.text.toString().trim()
        val text2 = txtRFC.text.toString().trim()
        val text3 = txtEmail.text.toString().trim()
        when {
            !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() -> {
                btnAddBilling.isEnabled = true
            } else -> {
            btnAddBilling.isEnabled = false
        }
        }
    }

    private fun setColorBlue(textView: TextView) {
        txtvName.setTextColor(resources.getColor(R.color.grayText))
        txtvRFC.setTextColor(resources.getColor(R.color.grayText))
        txtvEmail.setTextColor(resources.getColor(R.color.grayText))
        textView.setTextColor(resources.getColor(R.color.blue))
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            if (!Utils.isConnected(this)) {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), "No hay conexión a internet", success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }
        if (success) {
            onBackPressed()
        }
    }

}