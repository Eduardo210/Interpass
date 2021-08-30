package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.amount_list_item.view.*
import kotlinx.android.synthetic.main.billing_list_item.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.BillingModel


class BillinRecyclerAdaptar(val context: Context, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<BillinRecyclerAdaptar.BillinRecyclerViewHolder>()  {
    var select = 0
    var items = ArrayList<BillingModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BillinRecyclerViewHolder {
        return BillinRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.billing_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: BillinRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        view.txtvRFC.text = item.rfc
        view.txtvName.text = item.name

        if(item.isPrede == "True"){
            view.cardPrede.visibility = View.VISIBLE
        }
        else{
            view.cardPrede.visibility = View.GONE
        }
    }


    fun setElement(items: List<BillingModel>?){
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class BillinRecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtvRFC = view.txtvRFC
        val txtvName = view.txtvName
        val cardPrede = view.cardPrede
        init {
            view.setOnClickListener {
                onItemClickListener.selectBilling(items[adapterPosition])
            }
        }
    }

    interface OnItemClickListener {
        fun selectBilling(billing: BillingModel)
    }

}

