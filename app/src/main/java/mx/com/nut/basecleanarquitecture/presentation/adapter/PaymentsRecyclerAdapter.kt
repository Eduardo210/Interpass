package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.card_list_item.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel


class PaymentsRecyclerAdapter(val context: Context, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<PaymentsRecyclerAdapter.PaymentsRecyclerViewHolder>()  {
    var select = 0
    var items = ArrayList<PaymentsModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PaymentsRecyclerViewHolder {
        return PaymentsRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.card_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: PaymentsRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        if (item.default == "true") {
            view.contentView.setBackgroundResource(R.drawable.content_tag_default)
            view.txtv6.visibility = View.VISIBLE
        }
        view.txtvName.text = item.nickName
        val cardNumber = "**** **** **** ".plus(item.number!!.substring(12, 16))
        if (item.number!!.startsWith("4")) {
            view.imageViewType.setImageResource(R.drawable.visa)
        } else if (item.number!!.startsWith("5")) {
            view.imageViewType.setImageResource(R.drawable.mastercard)
        }
        view.txtvDescription.text = cardNumber
    }


    fun setElement(items: List<PaymentsModel>?){
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class PaymentsRecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewType = view.imageViewType
        val txtvName = view.txtvName
        val txtvDescription = view.txtvDescription
        val contentView = view.contentView
        val txtv6 = view.txtv6
        init {
            view.setOnClickListener {
                onItemClickListener.showPaymentDetail(items[adapterPosition])
            }
        }
    }

    interface OnItemClickListener {
        fun showPaymentDetail(payment: PaymentsModel)
    }

}

