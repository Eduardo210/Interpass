package mx.com.nut.basecleanarquitecture.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import mx.com.nut.basecleanarquitecture.R

fun LinearLayout.addIndicator(index: Int){
    val image = ImageView(this.context)
    image.tag = index
    image.setImageResource(R.drawable.dot)
    val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    layoutParams.marginEnd = context.resources.getDimensionPixelSize(R.dimen.margin5)
    layoutParams.marginStart = context.resources.getDimensionPixelSize(R.dimen.margin5)
    image.layoutParams = layoutParams
    addView(image)

}

fun LinearLayout.selectIndicator(index: Int, selected: Boolean){
    val image = findViewWithTag<ImageView>(index)
    if(selected){
        image.setImageResource(R.drawable.selected_dot)
    }else{
        image.setImageResource(R.drawable.dot)
    }
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun <T> Activity.sendToView(cls: Class<T>, extras: Bundle? = null , flags : Int? = null) {
    val intent = Intent(this, cls)
    intent.apply {
        extras?.let { it ->
            putExtras(it)
        }
        flags?.let {
            setFlags(it)
        }
    }
    ContextCompat.startActivity(this, intent, null)
}