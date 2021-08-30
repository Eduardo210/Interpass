@file:Suppress("DEPRECATION")

package mx.com.nut.basecleanarquitecture.commons

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_dialog_progress.*
import mx.com.nut.basecleanarquitecture.R

class ProgressDialogCustom(context: Context,theme: Int) : ProgressDialog(context,theme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_dialog_progress)
        lottieAnimation.setAnimation("progress.json")
        lottieAnimation.playAnimation()
        //lottieAnimation.loop(true)
    }

}