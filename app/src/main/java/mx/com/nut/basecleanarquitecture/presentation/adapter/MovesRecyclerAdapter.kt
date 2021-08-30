package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.moves_list_item.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.data.entity.MovesModel

class MovesRecyclerAdapter(val context: Context, val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MovesRecyclerAdapter.MovesRecyclerViewHolder>() {
    var select = 0
    var items = ArrayList<MovesModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovesRecyclerViewHolder {
        return MovesRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.moves_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: MovesRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        view.txtvDate.text = item.date
        view.txtvHour.text = item.hour
        view.txtvDescription.text = item.type

        val moveID: String? = item.tipoMovimientoID
        when (moveID) {
            "1", "2", "3" -> {
                view.imgMove.setImageResource(R.drawable.icon_recarga)
                if (item.amount == "0.00") {
                    view.txtvAmount.text = "$".plus(item.amount)
                } else {
                    view.txtvAmount.text = "+$".plus(item.amount)
                }
            }
            "101", "102", "103", "104" -> {
                view.imgMove.setImageResource(R.drawable.icono_cruce)
                if (item.amount == "0.00") {
                    view.txtvAmount.text = "$".plus(item.amount)
                } else {
                    view.txtvAmount.text = "-$".plus(item.amount)
                }
            }
            "105" -> {
                view.imgMove.setImageResource(R.drawable.icono_estacionamiento)
                if (item.amount == "0.00") {
                    view.txtvAmount.text = "$".plus(item.amount)
                } else {
                    view.txtvAmount.text = "-$".plus(item.amount)
                }
            }
        }
    }


    fun setElement(items: List<MovesModel>?) {
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class MovesRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutImg = view.layoutImg
        val imgMove = view.imgMove
        val txtvDate = view.txtvDate
        val txtvHour = view.txtvHour
        val txtvDescription = view.txtvDescription
        val txtvAmount = view.txtvAmount

        init {
            view.setOnClickListener {
                onItemClickListener.showMovesDetail(items[adapterPosition])
            }
        }


    }

    interface OnItemClickListener {
        fun showMovesDetail(move: MovesModel)
    }

}

