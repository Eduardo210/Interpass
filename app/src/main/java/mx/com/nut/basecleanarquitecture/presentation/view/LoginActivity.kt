package mx.com.nut.basecleanarquitecture.presentation.view


import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseLogin
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogOptions
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import java.util.*


class LoginActivity : BaseActivity(), CustomDialog.OnClickSetListener, CustomDialogOptions.OnClickOptionListener {

    var userID = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    fun addCredentials() {
        var user = ""
        Commons.getStringPreference(this, "userLogin")?.let { user = (it) }
        if (user != "") {
            txtUserID.setText(user, TextView.BufferType.EDITABLE)
            switchC.isChecked = true
        }
//        var pass = ""
//        Commons.getStringPreference(this, "pass")?.let { pass = (it) }
//        if (pass != "") {
//            txtPassword.setText(pass, TextView.BufferType.EDITABLE)
//            switchC.isChecked = true
//        }
    }

    override fun onResume() {
        super.onResume()
        super.onRestart()
        Commons.deletePreference(this, "tagNumber")
        Commons.deletePreference(this, "digit")
        Commons.deletePreference(this, "plate")
        Commons.deletePreference(this, "nickName")
        Commons.deletePreference(this, "checked")
        Commons.deletePreference(this, "vehiclePosition")
        Commons.deletePreference(this, "prefixPosition")
        btnNoticePrivace.isEnabled = true
        btnRegister.isEnabled = true
        btnForgotPassword.isEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        addCredentials()
        if (!isConnected(this)) {
            val dialog =
                CustomDialog(this, this, getString(R.string.titleError), "No hay conexión a internet", success = false, action = false)
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
            btnLogIn.isEnabled = true
        }
        switchC.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!isChecked) {
                Commons.deletePreference(this, "userLogin")
            }
        }

        btnForgotPassword.setOnClickListener {
            btnForgotPassword.isEnabled = false
            Commons.sendToView(this, RecoverPasswordActivity::class.java)
        }

        btnRegister.setOnClickListener {
            btnRegister.isEnabled = false
            Commons.sendToView(this, RegisterActivity::class.java)
        }

        btnNoticePrivace.setOnClickListener {
            btnNoticePrivace.isEnabled = false
            Commons.sendToView(this, PrivacyActivity::class.java)
        }

        btnLogIn.setOnClickListener {
            val input1 = txtUserID.text.toString().trim()
            val input2 = txtPassword.text.toString().trim()
            when {
                input1.isEmpty() -> {
                    txtvUserIDError.text = "Campo obligatorio"
                    setErrorView(txtvUserID, txtvUserIDError, txtUserID)
                }
                input2.isEmpty() -> {
                    txtvPasswordError.text = "Campo obligatorio"
                    setErrorView(txtvPassword, txtvPasswordError, txtPassword)
                } else -> {
                    btnLogIn.isEnabled = false
                    login(input1, input2)
                }
            }
        }

        txtUserID.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        txtvUserID.setTextColor(resources.getColor(R.color.blue))
                        txtvPassword.setTextColor(resources.getColor(R.color.grayText))
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        txtPassword.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        txtvPassword.setTextColor(resources.getColor(R.color.blue))
                        txtvUserID.setTextColor(resources.getColor(R.color.grayText))
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })


    }

    private fun isConnected(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun login(user: String, pass: String) {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("usuario" to user, "password" to pass)
        RestService.baseRequest(this, RequestEnum.LOGIN, Constans.METHOD_LOGIN, params, { response ->
            stopLoader()
            runOnUiThread {
                val responseLogin = response as ResponseLogin
                userID = responseLogin.usuarioId.toString()
                Commons.saveStringPreference(this, "sisUsu_Id", userID)
                var mensaje = responseLogin.mensaje
                if (responseLogin.mensajeId == "129") {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleCode), getString(R.string.codeMessage), success = true, action = true)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                } else {
                    if (switchC.isChecked) {
                        Commons.saveStringPreference(this, "userLogin", user)
                        Commons.saveStringPreference(this, "firstTime", "2")

                    }
                    Commons.saveStringPreference(this, "mensaje", mensaje)
                    val bundle = Bundle()
                    bundle.putBoolean("canScroll", false)
                    bundle.putBoolean("validateAccount", false)
                    bundle.putString("mensaje", mensaje)
                    Commons.sendToViewFinishBundle(this, TagsActivity::class.java, bundle)
                    finish()
                }
                btnLogIn.isEnabled = true
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnLogIn.isEnabled = true
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnLogIn.isEnabled = true
            }
        })
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
                        textView.setTextColor(resources.getColor(R.color.grayText))
                    }

                }
            }
        }, 1000)
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            return
        }
        if (code.isNotEmpty()) {
            startLoader()
            var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "CodConfirmacion" to code)
            RestService.baseRequest(this, RequestEnum.ACTIVE_ACCOUNT, Constans.METHOD_ACTIVE_ACCOUNT, params, { response ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleCodeSucces),
                            getString(R.string.codeMessageSuccess), true, false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    if (error == "Número de confirmación no es valido.") {
                        val dialog =
                            CustomDialogOptions(this, this, error.toString(), getString(R.string.messageRecoverCode))
                        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.setCancelable(false)
                        dialog.show()
                    } else {
                        val dialog =
                            CustomDialog(this, this, getString(R.string.titleError), error.toString(), false, false)
                        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                        dialog.setCancelable(false)
                        dialog.show()
                    }

                }
            }, { exception ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), exception, false, false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
        } else if (success) {
            val bundle = Bundle()
            bundle.putBoolean("canScroll", false)
            bundle.putBoolean("validateAccount", false)
            //bundle.putString("mensaje", mensaje)
            Commons.sendToViewFinishBundle(this, TagsActivity::class.java, bundle)
            finish()
        }
    }

    override fun onConfirmClick() {
        Commons.sendToView(this, RecoverPasswordActivity::class.java)
    }
}

