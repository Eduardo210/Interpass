package mx.com.nut.basecleanarquitecture.presentation.Commons

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.custom_dialog_lottie.*
import mx.com.nut.basecleanarquitecture.R

class CustomDialogLottie(context: Context, var title: String, var message: String): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_lottie)
        txtv1.text = title
        when (message) {
            "0" -> lottieAnimation.setAnimation("tutorial.json")
            "1" -> lottieAnimation.setAnimation("payment.json")
        }
        lottieAnimation.playAnimation()
        lottieAnimation.loop(true)

        btnDialogNext.setOnClickListener {
            dismiss()
        }
    }

}