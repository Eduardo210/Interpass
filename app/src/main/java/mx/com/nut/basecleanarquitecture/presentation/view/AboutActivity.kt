package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*


class AboutActivity: BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

    }

}