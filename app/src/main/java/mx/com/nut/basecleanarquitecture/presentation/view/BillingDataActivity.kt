package mx.com.nut.basecleanarquitecture.presentation.view

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import kotlinx.android.synthetic.main.activity_billing_data.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.BillingModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.adapter.BillinRecyclerAdaptar
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeController
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeControllerActions
import org.ksoap2.serialization.SoapObject

class BillingDataActivity: BaseActivity(), CustomDialog.OnClickSetListener, BillinRecyclerAdaptar.OnItemClickListener {
    var userID = ""
    var billingsArray: ArrayList<BillingModel> = ArrayList()
    var reloadView = true
    lateinit var rvadapter: BillinRecyclerAdaptar

    override fun getLayoutId(): Int {
        return R.layout.activity_billing_data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)

        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnAddBilling.setOnClickListener {
            btnAddBilling.isEnabled = false
            val bundle = Bundle()
            bundle.putString("title", "")
            Commons.sendToViewWithBundle(this, AddBillingActivity::class.java, bundle)
        }
        btnAddBillings.setOnClickListener {
            btnAddBillings.isEnabled = false
            val bundle = Bundle()
            bundle.putString("title", "")
            Commons.sendToViewWithBundle(this, AddBillingActivity::class.java, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        btnAddBillings.isEnabled = true
        btnAddBilling.isEnabled = true
        if (Session.updateBillingData) {
            if (Session.paymentsModel.size == 0) {
                billingsArray = ArrayList()
                getBillings()
                Session.updateBillingData = false
            } else {
                billingsArray = Session.billingModel
                setDataAdapter()
                Session.updateBillingData = false
            }
        }
    }

    private fun setDataAdapter() {
        rvBillings.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvadapter = BillinRecyclerAdaptar( this, this)
        rvBillings.adapter = rvadapter
        rvadapter.setElement(billingsArray)
    }

    private fun getBillings() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.GETBILLINGDATA, Constans.METHOD_BILLING_DATA, params, { response ->
            stopLoader()
            runOnUiThread {
                val responsePayments = response as ResponseList
                val billings = responsePayments.list as SoapObject?

                if (responsePayments.mensajeId == "0") {
                    noCardView.visibility = View.VISIBLE
                    cardsView.visibility = View.GONE
                } else {
                    noCardView.visibility = View.GONE
                    cardsView.visibility = View.VISIBLE
                    var count = 0
                    if (billings?.propertyCount == null) {
                        return@runOnUiThread
                    }
                    while (count < billings.propertyCount!!) {
                        val billing = billings?.getProperty(count) as SoapObject
                        billingsArray.add(
                            BillingModel(
                                billing.getPrimitivePropertyAsString("RFC"),
                                billing.getPrimitivePropertyAsString("Correo"),
                                billing.getPrimitivePropertyAsString("Nombre_o_RazonSocial"),
                                billing.getPrimitivePropertyAsString("UsuDatosId"),
                                billing.getPrimitivePropertyAsString("Predeterminado")
                            )
                        )
                        count += 1
                    }
                    billingsArray.sortWith(compareByDescending { it.isPrede })
                    Session.billingModel = billingsArray
                    setDataAdapter()
                }
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
            billingsArray = ArrayList()
            getBillings()
        }
    }

    override fun selectBilling(billing: BillingModel) {
        btnAddBilling.isEnabled = false
        val bundle = Bundle()
        bundle.putString("title", "Editar datos")
        bundle.putParcelable("billing", billing)
        Commons.sendToViewWithBundle(this, AddBillingActivity::class.java, bundle)
    }

}