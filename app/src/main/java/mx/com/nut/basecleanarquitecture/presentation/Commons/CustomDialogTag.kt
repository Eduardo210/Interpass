package mx.com.nut.basecleanarquitecture.presentation.Commons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.custom_dialog_tag.*
import mx.com.nut.basecleanarquitecture.R

class CustomDialogTag(context: Context): Dialog(context){
    var clickSetListener: OnClickSetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_tag)


        btnDialogNext.setOnClickListener {
            dismiss()
        }


    }

    interface OnClickSetListener {
    }
}