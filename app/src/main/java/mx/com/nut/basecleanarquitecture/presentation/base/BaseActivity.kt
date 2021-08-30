package mx.com.nut.basecleanarquitecture.presentation.base

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.ProgressDialogCustom

abstract class BaseActivity : AppCompatActivity() {
    private  var loader: ProgressDialogCustom?  = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        loader = ProgressDialogCustom(this, R.style.LoaderDialog)
    }

    abstract fun getLayoutId(): Int


    fun startLoader() {
        loader?.show()
    }

    fun stopLoader() {
        loader?.dismiss()
    }

    fun setToolBarHeight(toolbar: android.support.v7.widget.Toolbar) {
        val statusBar = getStatusBarHeight()
        val params = toolbar.layoutParams
        params.height = statusBar + resources.getDimensionPixelSize(R.dimen.dp56)
        toolbar.layoutParams = params
    }

    open fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    fun showAlert(title: String, message: String) {
        val alert = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") {dialog, which -> }
            .create()

        alert.show()
    }

}