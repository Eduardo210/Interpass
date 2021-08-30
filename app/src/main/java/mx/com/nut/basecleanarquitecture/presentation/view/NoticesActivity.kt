package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_notice.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.BillingModel
import mx.com.nut.basecleanarquitecture.data.entity.NoticesModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.adapter.NoticesRecyclerAdaptar
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import org.ksoap2.serialization.SoapObject

class NoticesActivity: BaseActivity(), CustomDialog.OnClickSetListener, NoticesRecyclerAdaptar.OnItemClickListener {
    var userID = ""
    var noticesArray: ArrayList<NoticesModel> = ArrayList()
    lateinit var rvadapter: NoticesRecyclerAdaptar

    override fun getLayoutId(): Int {
        return R.layout.activity_notice
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
        noticesArray = ArrayList()
        getNotices()
    }

    private fun setDataAdapter() {
        rvNotices.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvadapter = NoticesRecyclerAdaptar( this, this)
        rvNotices.adapter = rvadapter
        rvadapter.setElement(noticesArray)
    }

    private fun getNotices() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.GETNOTICES, Constans.NOTICES, params, { response ->
            stopLoader()
            runOnUiThread {
                val responsePayments = response as ResponseList
                val notices = responsePayments.list as SoapObject?
                var count = 0
                if (notices?.propertyCount == null) {
                    return@runOnUiThread
                }
                while (count < notices.propertyCount!!) {
                    val notice = notices?.getProperty(count) as SoapObject
                    noticesArray.add(
                        NoticesModel(
                            notice.getPrimitivePropertyAsString("Titulo"),
                            notice.getPrimitivePropertyAsString("Descripcion"),
                            notice.getPrimitivePropertyAsString("Imagen")
                        )
                    )
                    count += 1
                }
                setDataAdapter()
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        })
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            onBackPressed()
        }
        if (success) {
            noticesArray = ArrayList()
            getNotices()
        }
    }

    override fun selectBilling(billing: NoticesModel) {
        val bundle = Bundle()
        bundle.putString("title", billing.title)
        bundle.putString("image", billing.image)
        bundle.putString("description", billing.description)
        Commons.sendToViewWithBundle(this, NoticesDetailActivity::class.java, bundle)

    }

}