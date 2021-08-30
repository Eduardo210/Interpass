package mx.com.nut.basecleanarquitecture.presentation.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import kotlinx.android.synthetic.main.activity_notice_detail.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity

class NoticesDetailActivity: BaseActivity(), CustomDialog.OnClickSetListener {
    var title = ""
    var description = ""
    var image = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_notice_detail
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

    override fun onResume() {
        super.onResume()
        title = intent.extras.getString("title")
        description = intent.extras.getString("description")
        image = intent.extras.getString("image")
        txtvNoticeTitle.text = title
        txtvDetail.text = description
        val imageBytes = Base64.decode(image, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        logo.setImageBitmap(decodedImage)
    }


    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            onBackPressed()
        }
    }
}