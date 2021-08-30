package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_register.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.afterTextChanged
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import java.util.*



class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        setListeners()
        btnRegister.setOnClickListener {
            if (validateData()) {
                val bundle = Bundle()
                val name = txtName.text.toString().trim()
                val lastName = txtLastName.text.toString().trim()
                val lastName2 = txtLastName2.text.toString().trim()
                val email = txtEmail.text.toString().trim()
                val phone = txtPhone.text.toString().trim()
                bundle.putString("name", name)
                bundle.putString("lastName", lastName)
                bundle.putString("lastName2", lastName2)
                bundle.putString("email", email)
                bundle.putString("phone", phone)
                bundle.putBoolean("newTag", false)
                Commons.sendToViewWithBundle(this, RegisterTagActivity::class.java, bundle)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    fun validateData(): Boolean {
        if (isEmailValid(txtEmail.text.toString())) {
            runOnUiThread {
                txtvEmailError.text = "Correo electrónico inválido"
                setErrorView(txtvEmail, txtvEmailError, txtEmail)
            }
            return false
        }
        val phone = txtPhone.text.toString().trim()
        if (phone.length > 0 && phone.length < 10 || phone.substring(0, 1) == "0") {
            runOnUiThread {
                txtvPhoneError.text = "Número de teléfono inválido."
                setErrorView(txtvPhone, txtvPhoneError, txtPhone)
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

    fun setListeners() {
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        txtName.afterTextChanged {
            setColorBlue(txtvName)
            validData()
        }

        txtLastName.afterTextChanged {
            setColorBlue(txtvLastName)
            validData()
        }

        txtLastName2.afterTextChanged {
            setColorBlue(txtvLastName2)
            validData()
        }

        txtEmail.afterTextChanged {
            setColorBlue(txtvEmail)
            validData()
        }

        txtPhone.afterTextChanged {
            setColorBlue(txtvPhone)
            validData()
        }
    }

    fun validData() {
        val text1 = txtName.text.toString().trim()
        val text2 = txtLastName.text.toString().trim()
        val text3 = txtLastName2.text.toString().trim()
        val text4 = txtEmail.text.toString().trim()
        val text5 = txtPhone.text.toString().trim()
        when {
            !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty() && !text5.isEmpty() -> {
                btnRegister.isEnabled = true
            } else -> {
                btnRegister.isEnabled = false
            }
        }
    }

    fun setColorBlue(textView: TextView) {
        txtvName.setTextColor(resources.getColor(R.color.grayText))
        txtvLastName.setTextColor(resources.getColor(R.color.grayText))
        txtvLastName2.setTextColor(resources.getColor(R.color.grayText))
        txtvEmail.setTextColor(resources.getColor(R.color.grayText))
        txtvPhone.setTextColor(resources.getColor(R.color.grayText))

        textView.setTextColor(resources.getColor(R.color.blue))
    }

}