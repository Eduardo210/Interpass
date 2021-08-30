package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_change_password.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.commons.sendToView
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.util.*

class ChangePasswordActivity: BaseActivity(), CustomDialog.OnClickSetListener {

    var userID = ""
    var name = ""
    var lastName = ""
    var surName = ""
    var email = ""
    var phone = ""
    var sisUsI = ""
    var sisUsC = ""
    var image = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_change_password
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        setData()
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        btnChange.setOnClickListener {
            btnChange.isEnabled = false
            if (validateData()) {
                changePasswordRequest()
            } else {
                btnChange.isEnabled = true
            }
        }
    }

    private fun setData() {
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        name = intent.extras.getString("name")
        lastName = intent.extras.getString("lastName")
        surName = intent.extras.getString("surName")
        email = intent.extras.getString("email")
        phone =  intent.extras.getString("phone")
        sisUsI = intent.extras.getString("sisUsI")
        sisUsC = intent.extras.getString("sisUsC")
    }


    fun changePasswordRequest() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "0", "nombre" to name,
            "apellidoPaterno" to lastName, "apellidoMaterno" to surName, "calle" to "",
            "colonia" to "", "estado" to "", "municipio" to "", "cp" to "", "telefono1" to phone, "telefono2" to "",
            "fax" to "", "email" to email, "SisUsu_Identificador" to sisUsI, "SisUsu_Contrasenia" to txtNewPassword.text.toString(), "imagen" to image)
        RestService.baseRequest(this,
            RequestEnum.GETPROFILE, Constans.METHOD_GETPROFILE, params, { response ->
                runOnUiThread {
                    stopLoader()
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleChangePassSuccess), getString(R.string.changePassMessage), success = true, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                    Session.updateProfile = true
                    Session.profileData = null
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

    fun validateData(): Boolean {
        if (sisUsC != txtOldPassword.text.toString()) {
            runOnUiThread {
                txtvOldPasswordError.text = "Contraseña incorrecta"
                setErrorView(txtvOldPassword, txtvOldPasswordError, txtOldPassword)
            }
            return false
        } else if (txtNewPassword.text.toString() != txtConfirmPassword.text.toString()) {
            runOnUiThread {
                txtvConfirmPasswordError.text = "Contraseñas no coinciden"
                setErrorView(txtvConfirmPassword, txtvConfirmPasswordError, txtConfirmPassword)
            }
            return false
        }
        return true
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
            Commons.savePreference(this,"firstTime","1")
            sendToView(LoginActivity::class.java,flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            Session.reset()
        }
    }

}