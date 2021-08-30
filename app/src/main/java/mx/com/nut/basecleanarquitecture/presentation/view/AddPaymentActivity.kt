package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_add_payment.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.afterTextChanged
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import java.text.SimpleDateFormat
import java.util.*
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import kotlin.collections.ArrayList


class AddPaymentActivity : BaseActivity(), CustomDialog.OnClickSetListener {
    lateinit var payment: PaymentsModel
    var userID = ""
    var title: String? = ""
    var type: String = ""
    var checkBoxString: String = "no"
    var isEditable = false

    override fun getLayoutId(): Int {
        return R.layout.activity_add_payment
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
            payment = intent.extras.getParcelable("payment")
            txtvTitle.text = title
            btnAddCard.text = getString(R.string.saveTag)
            type = payment.type!!
            if (type == "1") {
                btnDebit.setBackgroundResource(R.drawable.button_card_enable_left)
                btnDebit.setTextColor(ContextCompat.getColor(this, R.color.white))
                btnDebit.isEnabled = false
                btnCredit.isEnabled = false
            } else if (type == "2") {
                btnCredit.setBackgroundResource(R.drawable.button_card_enable_right)
                btnCredit.setTextColor(ContextCompat.getColor(this, R.color.white))
                btnDebit.isEnabled = false
                btnCredit.isEnabled = false
            }
            if (payment.default == "true") {
                checkbox.isChecked = true
                checkBoxString = "si"
            }
            txtCardNumberHide.visibility = View.VISIBLE
            txtCardNumberHide.setText("**** **** **** ".plus(payment.number!!.substring(12, 16)))
            txtCardNumber.visibility = View.INVISIBLE
            txtCardNumber.setText("1234")
            txtCardNumberHide.isFocusable = false
            txtCardNumberHide.isClickable = false
            txtCardCode.setText(payment.cvv)
            txtCardDate.setText(payment.month.plus("/".plus(payment.year)))
            btnAddCard.isEnabled = true
            txtNickName.setText(payment.nickName)
            isEditable = true
        }
    }

    private fun addListeners() {
        txtNickName.afterTextChanged {
            setColorBlue(txtvNickName)
            validData()
        }

        txtCardCode.afterTextChanged {
            setColorBlue(txtvCardCode)
            validData()
        }

        txtCardNumber.addTextChangedListener(object : TextWatcher {
            var current = ""
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                setColorBlue(txtvCardNumber)
                validData()
                if (s!!.isNotEmpty() && s!![0].toString( ) == "4") {
                    val img = getDrawable(R.drawable.visa)
                    txtCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                } else if (s!!.isNotEmpty() && s!![0].toString( ) == "5") {
                    val img = getDrawable(R.drawable.mastercard)
                    txtCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                } else {
                    txtCardNumber.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                }

                if (s.toString() != current) {
                    val userInput = s.toString().replace("[^\\d]".toRegex(), "")
                    if (userInput.length <= 16) {
                        val sb = StringBuilder()
                        for (i in 0 until userInput.length) {
                            if (i % 4 == 0 && i > 0) {
                                sb.append(" ")
                            }
                            sb.append(userInput[i])
                        }
                        current = sb.toString()

                        s!!.filters = arrayOfNulls<InputFilter>(0)
                    }
                    s!!.replace(0, s.length, current, 0, current.length)
                }
            }
        })

        txtCardDate.addTextChangedListener(object : TextWatcher {
            var previousLength = 0

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                previousLength = txtCardDate.getText().toString().length
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                val length = txtCardDate.getText().toString().trim().length

                if (previousLength <= length && length < 3) {
                    val month = Integer.parseInt(txtCardDate.text.toString())
                    if (length == 1 && month >= 2) {
                        val autoFixStr = "0$month/"
                        txtCardDate.setText(autoFixStr)
                        txtCardDate.setSelection(3)
                    } else if (length == 2 && month <= 12) {
                        val autoFixStr = txtCardDate.text.toString() + "/"
                        txtCardDate.setText(autoFixStr)
                        txtCardDate.setSelection(3)
                    } else if (length == 2 && month > 12) {
                        txtCardDate.setText("1")
                        txtCardDate.setSelection(1)
                    }
                } else if (length == 5) {

                }
            }

            override fun afterTextChanged(editable: Editable?) {
                setColorBlue(txtvCardDate)
                validData()
                if (previousLength == 3 && editable!!.length == 2) {
                    txtCardDate.setText("".plus(editable!![0]))
                    txtCardDate.setSelection(1)
                }
            }
        })

        btnDebit.setOnClickListener {
            btnDebit.setBackgroundResource(R.drawable.button_card_enable_left)
            btnDebit.setTextColor(ContextCompat.getColor(this, R.color.white))

            btnCredit.setBackgroundResource(R.drawable.button_card_disable_right)
            btnCredit.setTextColor(ContextCompat.getColor(this, R.color.blue))

            type = "1"
        }

        btnCredit.setOnClickListener {
            btnCredit.setBackgroundResource(R.drawable.button_card_enable_right)
            btnCredit.setTextColor(ContextCompat.getColor(this, R.color.white))

            btnDebit.setBackgroundResource(R.drawable.button_card_disable_left)
            btnDebit.setTextColor(ContextCompat.getColor(this, R.color.blue))

            type = "2"
        }

        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        btnAddCard.setOnClickListener {
            if (validateData()) {
                if (isEditable) {
                    editPayment()
                } else {
                    addPayment()
                }
            }
        }

        checkbox.setOnClickListener {
            if (checkbox.isChecked) {
                checkBoxString = "si"
            } else {
                checkBoxString =  "no"
            }
        }
    }

    private fun validateData(): Boolean {
        val cardNumber = txtCardNumber.text.toString().replace(" ", "")
        if (cardNumber.length > 0 && cardNumber.length < 16 && !isEditable) {
            runOnUiThread {
                txtvCardNumberError.text = "Número de tarjeta inválido."
                setErrorView(txtvCardNumber, txtvCardNumberError, txtCardNumber)
            }
            return false
        }
        if (type == "") {
            val dialog =
                CustomDialog(this, this, getString(R.string.titleError), "Selecciona el tipo de tarjeta", success = false, action = false)
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
            return false
        }

        val input = txtCardDate.text.toString() // for example
        val simpleDateFormat = SimpleDateFormat("MM/yy")
        simpleDateFormat.isLenient = false
        val expiry = simpleDateFormat.parse(input)
        val expired = expiry.before(Date())
        if (expired) {
            runOnUiThread {
                txtvCardDateError.text = "Su tarjeta ha expirado"
                setErrorView(txtvCardDate, txtvCardDateError, txtCardDate)
            }
            return false
        }

        val code = txtCardCode.text.toString().trim()
        if (code.length > 0 && code.length < 3) {
            runOnUiThread {
                txtvCardCodeError.text = "Longitud inválida."
                setErrorView(txtvCardCode, txtvCardCodeError, txtCardCode)
            }
            return false
        }
        return true
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

    private fun editPayment() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        val month = txtCardDate.text.toString().substringBefore("/")
        val year = txtCardDate.text.toString().substringAfter("/")
        val idCard = payment.idCard.toString()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "2",
            "NumTarjeta" to payment.number!!, "mesVencimiento" to month, "anioVencimiento" to year,
            "nombreTC" to txtNickName.text.toString(), "cvv" to txtCardCode.text.toString(), "esPredeterminado" to checkBoxString, "Tc_id" to idCard, "tipo" to type)
        RestService.baseRequest(this, RequestEnum.GETPAYMENTS, Constans.METHOD_GETPAYMENTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleUpdatePaymentMessage), getString(R.string.updatePaymentMessage), success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                Session.updatePayments = true
                Session.paymentsModel = ArrayList()
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

    private fun addPayment() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        val month = txtCardDate.text.toString().substringBefore("/")
        val year = txtCardDate.text.toString().substringAfter("/")
        val cardNumber = txtCardNumber.text.toString().replace(" ", "")
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "3",
            "NumTarjeta" to cardNumber, "mesVencimiento" to month, "anioVencimiento" to year,
            "nombreTC" to txtNickName.text.toString(), "cvv" to txtCardCode.text.toString(), "esPredeterminado" to checkBoxString, "tipo" to type)

        RestService.baseRequest(this, RequestEnum.GETPAYMENTS, Constans.METHOD_GETPAYMENTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleAddPaymentMessage), getString(R.string.messageAddPayment), success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                Session.updatePayments = true
                Session.paymentsModel = ArrayList()
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
        val text1 = txtNickName.text.toString().trim()
        val text2 = txtCardNumber.text.toString().trim()
        val text3 = txtCardDate.text.toString().trim()
        val text4 = txtCardCode.text.toString().trim()
        when {
            !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty() -> {
                btnAddCard.isEnabled = true
            } else -> {
                btnAddCard.isEnabled = false
            }
        }
    }

    private fun setColorBlue(textView: TextView) {
        txtvNickName.setTextColor(resources.getColor(R.color.grayText))
        txtvCardNumber.setTextColor(resources.getColor(R.color.grayText))
        txtvCardDate.setTextColor(resources.getColor(R.color.grayText))
        txtvCardCode.setTextColor(resources.getColor(R.color.grayText))

        textView.setTextColor(resources.getColor(R.color.blue))
    }

    override fun onClick(code: String, success: Boolean) {
        if (success) {
            onBackPressed()
        }
    }
}