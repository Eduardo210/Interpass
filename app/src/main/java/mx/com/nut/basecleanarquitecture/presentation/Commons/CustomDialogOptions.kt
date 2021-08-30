package mx.com.nut.basecleanarquitecture.presentation.Commons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.custom_dialog_options.*
import mx.com.nut.basecleanarquitecture.R

class CustomDialogOptions(context: Context, var clickOptionListener: OnClickOptionListener, var title: String, var message: String): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_options)
        txtvDialogTitle.text = title
        txtvDialogDescription.text = message


        btnDialogCancel.setOnClickListener {
            dismiss()
        }

        btnDialogConfirmD.setOnClickListener {
            clickOptionListener.onConfirmClick()
            dismiss()
        }


    }

    interface OnClickOptionListener {
        fun onConfirmClick()

    }
}