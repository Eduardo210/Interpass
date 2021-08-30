package mx.com.nut.basecleanarquitecture.presentation.Commons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.custom_dialog_delete.*
import mx.com.nut.basecleanarquitecture.R

class CustomDialogDelete(context: Context, var clickDeleteListener: OnClickDeleteListener, var title: String, var message: String, var code: Boolean): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_delete)
        txtvDialogTitle.text = title
        txtvDialogDescription.text = message
        if (code) {
            imgDialog.setImageResource(R.drawable.icon_trash)
        }
        btnDialogCancel.setOnClickListener {
            clickDeleteListener.onCancelClick()
            dismiss()
        }

        btnDialogConfirmD.setOnClickListener {
            clickDeleteListener.onDeleteClick()
            dismiss()
        }


    }

    interface OnClickDeleteListener {
        fun onDeleteClick()
        open fun onCancelClick()

    }
}