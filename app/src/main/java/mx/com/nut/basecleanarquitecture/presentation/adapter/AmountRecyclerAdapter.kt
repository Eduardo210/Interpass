package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.amount_list_item.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.AmountModel


class AmountRecyclerAdapter(val context: Context, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<AmountRecyclerAdapter.AmountRecyclerViewHolder>()  {
    var select = 0
    var items = ArrayList<AmountModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AmountRecyclerViewHolder {
        return AmountRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.amount_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: AmountRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        if (item.selected!!) {
            view.imageViewType.setImageResource(R.drawable.card_vector)
            view.txtvAmount.setTextColor(ContextCompat.getColor(context, R.color.white))
            view.txtv1.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            view.imageViewType.setImageResource(R.drawable.card_vector_default)
            view.txtvAmount.setTextColor(ContextCompat.getColor(context, R.color.blue))
            view.txtv1.setTextColor(ContextCompat.getColor(context, R.color.blue))
        }
        view.txtvAmount.text = "$".plus(item.amount)
    }


    fun setElement(items: List<AmountModel>?){
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class AmountRecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageViewType = view.imageViewType
        val txtvAmount = view.txtvAmount
        val txtv1 = view.txtv1
        init {
            view.setOnClickListener {
                for (item in items) {
                    item.selected = false
                }
                items[adapterPosition].selected = true
                onItemClickListener.selectAmount(items[adapterPosition].amount)
            }
        }


    }

    interface OnItemClickListener {
        fun selectAmount(amount: String?)
    }

}

