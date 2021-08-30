package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_payment_detail.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session

class PaymentDetailActivity: BaseActivity() {

    lateinit var payment: PaymentsModel

    override fun getLayoutId(): Int {
        return R.layout.activity_payment_detail
    }

    override fun onResume() {
        super.onResume()
        btnEdit.isEnabled = true
        if (Session.updatePayments) {
            onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnEdit.setOnClickListener {
            btnEdit.isEnabled = false
            val bundle = Bundle()
            bundle.putParcelable("payment", payment)
            bundle.putString("title", "Editar tarjeta")
            Commons.sendToViewWithBundle(this, AddPaymentActivity::class.java, bundle)
        }
        payment = intent.extras.getParcelable("payment")


        val cardType = payment.type
        var cardTypeString = ""
        if (cardType == "1") {
            cardTypeString = "Débito"
        } else if (cardType == "2") {
            cardTypeString = "Crédito"
        }
        txtvCardType2.text = cardTypeString
        txtvNickName2.text = payment.nickName
        txtvCardNumber2.text = "**** **** **** ".plus(payment.number!!.substring(12, 16))
        txtvDate2.text = payment.month.plus("/".plus(payment.year))
        if (payment.default == "true") {
            checkbox.isChecked = true
        }
        checkbox.isFocusable = false
        checkbox.isClickable = false

    }

}