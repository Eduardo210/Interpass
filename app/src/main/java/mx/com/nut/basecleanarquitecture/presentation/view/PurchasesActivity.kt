package mx.com.nut.basecleanarquitecture.presentation.view

import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import kotlinx.android.synthetic.main.activity_purchases.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.AmountModel
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogDelete
import mx.com.nut.basecleanarquitecture.presentation.adapter.AmountRecyclerAdapter
import mx.com.nut.basecleanarquitecture.presentation.adapter.PaymentPagerAdapter
import mx.com.nut.basecleanarquitecture.presentation.adapter.PaymentsRecyclerAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeController
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeControllerActions
import org.ksoap2.serialization.SoapObject

class PurchasesActivity: BaseActivity(), CustomDialogDelete.OnClickDeleteListener, CustomDialog.OnClickSetListener, AmountRecyclerAdapter.OnItemClickListener {

    var positionCard = ""
    var userID = ""
    var tag = ""
    var amountString = ""
    var mensajeLogin =""
    var paymentsModel: ArrayList<PaymentsModel> = ArrayList()
    var amountModel: ArrayList<AmountModel> = ArrayList()
    lateinit var adapter: PaymentPagerAdapter
    lateinit var rvadapter: AmountRecyclerAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_purchases
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        tag = intent.extras.getString("tagnumber")
        txtTagNumber.text = tag
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        getAmounts()
        btnRegister.setOnClickListener {
            purchaseAmount()
        }

    }

    override fun onResume() {
        super.onResume()
        //getPayments()
        if (Session.updatePayments) {
            if (Session.paymentsModel.size == 0) {
                paymentsModel = ArrayList()
                getPayments()
                Session.updatePayments = false
            } else {
                paymentsModel = Session.paymentsModel
                setDataAdapter()
                Session.updatePayments = false
            }
        }
    }

    private fun purchaseAmount() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("tag" to tag, "monto" to amountString, "idTarjeta" to positionCard, "idTransact" to "")
        RestService.baseRequestP(this, Constans.METHOD_PURCHASE, params, { response ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titlePurchaseSuccess), getString(R.string.messagePurchaseSuccess), success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                Session.updateTagMoves = true
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

    private fun getPayments() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.GETPAYMENTS, Constans.METHOD_GETPAYMENTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val responsePayments = response as ResponseList
                val payments = responsePayments.list as SoapObject?

                if (responsePayments.mensajeId == "129") {
                    cardsView.visibility = View.INVISIBLE
                    val dialog =
                        CustomDialogDelete(this, this, "Upss","No tienes métodos de pago vinculados a esta cuenta, necesitas al menos 1 método para poder recargar saldo.", false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                } else {
                    cardsView.visibility = View.VISIBLE
                    var count = 0
                    if (payments?.propertyCount == null) {
                        return@runOnUiThread
                    }
                    while (count < payments?.propertyCount!!) {
                        val payment = payments?.getProperty(count) as SoapObject
                        if (payment.getPrimitivePropertyAsString("EsPredeterminada") == "true") {
                            paymentsModel.add(
                                0, PaymentsModel(
                                    payment.getPrimitivePropertyAsString("AnioVencimiento"),
                                    payment.getPrimitivePropertyAsString("Cvv"),
                                    payment.getPrimitivePropertyAsString("EsPredeterminada"),
                                    payment.getPrimitivePropertyAsString("Id"),
                                    payment.getPrimitivePropertyAsString("MesVencimiento"),
                                    payment.getPrimitivePropertyAsString("NombreTC"),
                                    payment.getPrimitivePropertyAsString("NumeroTC"),
                                    payment.getPrimitivePropertyAsString("Tipo")
                                )
                            )
                        } else {
                            paymentsModel.add(
                                PaymentsModel(
                                    payment.getPrimitivePropertyAsString("AnioVencimiento"),
                                    payment.getPrimitivePropertyAsString("Cvv"),
                                    payment.getPrimitivePropertyAsString("EsPredeterminada"),
                                    payment.getPrimitivePropertyAsString("Id"),
                                    payment.getPrimitivePropertyAsString("MesVencimiento"),
                                    payment.getPrimitivePropertyAsString("NombreTC"),
                                    payment.getPrimitivePropertyAsString("NumeroTC"),
                                    payment.getPrimitivePropertyAsString("Tipo")
                                )
                            )

                        }
                        count += 1
                    }
                    Session.paymentsModel = paymentsModel
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

    private fun getAmounts() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf()
        RestService.baseRequest(this, RequestEnum.GETAMOUNTS, Constans.METHOD_GETAMOUNTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val responseAmount = response as ResponseList
                val amount = responseAmount.list as SoapObject?

                var count = 0
                if (amount?.propertyCount == null) {
                    return@runOnUiThread
                }
                while (count < amount?.propertyCount!!) {
                    val payment = amount?.getProperty(count) as SoapObject
                    amountModel.add(
                        AmountModel(
                            payment.getPrimitivePropertyAsString("Id"),
                            payment.getPrimitivePropertyAsString("Monto"),
                            false
                        )
                    )
                    count += 1
                }
                rvAmount.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rvadapter = AmountRecyclerAdapter( this, this)
                rvAmount.adapter = rvadapter
                rvadapter.setElement(amountModel)

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

    private fun setDataAdapter() {
        adapter = PaymentPagerAdapter(supportFragmentManager, paymentsModel)
        pagerPayments.adapter = adapter
        pagerPayments.currentItem = 0
        pagerPayments.offscreenPageLimit = 1
        positionCard = paymentsModel[0].idCard!!
        addCarousel()
    }

    private fun addCarousel() {
        pagerPayments.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                positionCard = paymentsModel[position].idCard!!
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet) || success) {
            onBackPressed()
        }
    }

    override fun selectAmount(amount: String?) {
        txtvAmount.text = "$".plus(amount.plus(".00"))
        amountString = amount!!
        btnRegister.isEnabled = true
        rvadapter.notifyDataSetChanged()
    }

    override fun onDeleteClick() {
        val bundle = Bundle()
        bundle.putString("title", "")
        Commons.sendToViewWithBundle(this, AddPaymentActivity::class.java, bundle)
    }


    override fun onCancelClick() {
        onBackPressed()
    }


}