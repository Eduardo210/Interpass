package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.moves_list_item.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.MovesToBillingModel

class BillingMovesRecyclerAdapter(val context: Context, val onItemClickListener: OnItemClickListener):
    RecyclerView.Adapter<BillingMovesRecyclerAdapter.BillinMovesRecyclerViewHolder>()  {
    var select = 0
    var items = ArrayList<MovesToBillingModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BillinMovesRecyclerViewHolder {
        return BillinMovesRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.moves_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: BillinMovesRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        view.txtvDate.text = item.date
        view.txtvHour.text = item.hour
        view.txtvDescription.text = item.type
        view.txtvAmount.text = item.amount
        if (item.selected!!) {
            view.imgMove.setImageResource(R.drawable.icono_check)
        } else {
            view.imgMove.setImageResource(R.drawable.icono_no_check)
        }
    }


    fun setElement(items: List<MovesToBillingModel>?){
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class BillinMovesRecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val layoutImg = view.layoutImg
        val imgMove = view.imgMove
        val txtvDate = view.txtvDate
        val txtvHour = view.txtvHour
        val txtvDescription = view.txtvDescription
        val txtvAmount = view.txtvAmount

        init {
            view.setOnClickListener {
                onItemClickListener.selectBilling(items[adapterPosition], items[adapterPosition].selected!!)
            }
        }
    }

    interface OnItemClickListener {
        fun selectBilling(billing: MovesToBillingModel, selected: Boolean)
    }

}

