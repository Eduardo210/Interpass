package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_password.*
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseLogin
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import java.util.*

class RecoverPasswordActivity: BaseActivity(), CustomDialog.OnClickSetListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        btnChange.setOnClickListener {
            btnChange.isEnabled = false
            if (validateData()) {
                recoverPasswordRequest()
            } else {
                btnChange.isEnabled = true
            }
        }

    }

    fun recoverPasswordRequest() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("email" to txtEmail.text.toString())
        RestService.baseRequest(this, RequestEnum.RECOVERPASS, Constans.METHOD_RECOVER_PASSWORD, params, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponseLogin
                val dialog = CustomDialog(this, this, getString(R.string.titleRegisterDialog), response.mensaje.toString(), success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnChange.isEnabled = true
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnChange.isEnabled = true
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnChange.isEnabled = true
            }
        })
    }

    fun validateData(): Boolean {
        if (isEmailValid(txtEmail.text.toString())) {
            runOnUiThread {
                txtvEmailError.text = "Correo electrónico inválido"
                setErrorView(txtvEmail, txtvEmailError, txtEmail)
            }
            return false
        }
        return true
    }

    fun isEmailValid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun setErrorView(textView: TextView, errorTextView: TextView, editText: EditText) {
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

    override fun onClick(code: String, success: Boolean) {
        if (success) {
            onBackPressed()
        }
    }

}