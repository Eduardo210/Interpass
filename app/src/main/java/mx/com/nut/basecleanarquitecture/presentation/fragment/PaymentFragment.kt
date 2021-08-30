package mx.com.nut.basecleanarquitecture.presentation.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_pay_list_item.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import java.io.ByteArrayOutputStream

class PaymentFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mx.com.nut.basecleanarquitecture.R.layout.card_pay_list_item,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = arguments?.getParcelable<PaymentsModel>("card")
        if (item!!.default == "true") {
            contentView.setBackgroundResource(R.drawable.content_card_default)
            txtvDescription.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            txtvNumber.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            txtvName.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            txtvDate.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            txtvDefault.visibility = View.VISIBLE
        }
        txtvName.text = item!!.nickName
        val cardNumber = "**** **** **** ".plus(item.number!!.substring(12, 16))
        if (item.number!!.startsWith("4")) {
            imageViewType.setImageResource(R.drawable.visa)
        } else if (item.number!!.startsWith("5")) {
            imageViewType.setImageResource(R.drawable.mastercard)
        }
        txtvNumber.text = cardNumber
        val cardType = item.type
        var cardTypeString = ""
        if (cardType == "1") {
            cardTypeString = "Débito"
        } else if (cardType == "2") {
            cardTypeString = "Crédito"
        }
        txtvDescription.text = cardTypeString
        txtvDate.text = item.month.plus("/".plus(item.year))
    }

    companion object {
        fun newInstance(card: PaymentsModel): PaymentFragment {
            val paymentFragment = PaymentFragment()
            val bundle = Bundle()
            bundle.putParcelable("card", card)
            paymentFragment.arguments = bundle
            return  paymentFragment
        }
    }

}