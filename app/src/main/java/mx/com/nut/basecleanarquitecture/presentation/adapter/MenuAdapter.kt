package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_side_menu.view.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.domain.model.ItemMenu

class MenuAdapter(val items : List<ItemMenu>, val clickAction : (pos: Int) -> Unit) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_side_menu,parent,false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(items[pos])
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        fun bind(item : ItemMenu) {
            itemView.txtItemName.setText(item.title)
            itemView.imgIcon.setImageResource(item.icon)
            itemView.setOnClickListener { clickAction(adapterPosition) }
        }

    }

}