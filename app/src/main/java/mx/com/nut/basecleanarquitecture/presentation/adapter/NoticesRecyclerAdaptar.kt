package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.notice_list_item.view.*
import mx.com.nut.basecleanarquitecture.data.entity.NoticesModel
import android.graphics.BitmapFactory
import android.util.Base64


class NoticesRecyclerAdaptar(val context: Context, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<NoticesRecyclerAdaptar.NoticesRecyclerViewHolder>()  {
    var select = 0
    var items = ArrayList<NoticesModel>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NoticesRecyclerViewHolder {
        return NoticesRecyclerViewHolder(LayoutInflater.from(context).inflate(mx.com.nut.basecleanarquitecture.R.layout.notice_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(view: NoticesRecyclerViewHolder, pos: Int) {
        val item = items[pos]
        view.txtvTitleNotice.text = item.title
        view.txtvDescription.text = item.description
        val imageBytes = Base64.decode(item.image, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.imgNotice.setImageBitmap(decodedImage)

    }


    fun setElement(items: List<NoticesModel>?){
        this.select = 0
        this.items.clear()
        this.items.addAll(items!!)
        notifyDataSetChanged()
    }

    inner class NoticesRecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtvTitleNotice = view.txtvTitleNotice
        val txtvDescription = view.txtvDescription
        val imgNotice = view.imgNotice
        init {
            view.setOnClickListener {
                onItemClickListener.selectBilling(items[adapterPosition])
            }
        }
    }

    interface OnItemClickListener {
        fun selectBilling(billing: NoticesModel)
    }

}

