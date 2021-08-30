package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_payments.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.adapter.PaymentsRecyclerAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import org.ksoap2.serialization.SoapObject
import android.support.v7.widget.helper.ItemTouchHelper
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeController
import mx.com.nut.basecleanarquitecture.presentation.base.SwipeControllerActions
import android.support.v7.widget.RecyclerView
import android.graphics.Canvas
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogDelete
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogLottie
import mx.com.nut.basecleanarquitecture.presentation.base.Session


class PaymentsActivity : BaseActivity(), CustomDialogDelete.OnClickDeleteListener, CustomDialog.OnClickSetListener,
    PaymentsRecyclerAdapter.OnItemClickListener {

    var positionCard = 0
    var userID = ""
    var paymentsModel: ArrayList<PaymentsModel> = ArrayList()
    lateinit var rvadapter: PaymentsRecyclerAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_payments
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)

        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnAddCard.setOnClickListener {
            btnAddCard.isEnabled = false
            val bundle = Bundle()
            bundle.putString("title", "")
            Commons.sendToViewWithBundle(this, AddPaymentActivity::class.java, bundle)
        }
        btnAddCards.setOnClickListener {
            btnAddCard.isEnabled = false
            val bundle = Bundle()
            bundle.putString("title", "")
            Commons.sendToViewWithBundle(this, AddPaymentActivity::class.java, bundle)
        }
        btnQuestion.setOnClickListener {
            val dialog =
                CustomDialogLottie(this, getString(R.string.titlePaymentHelp), "1")
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        btnAddCard.isEnabled = true
        btnAddCards.isEnabled = true
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

    private fun getPayments() {
        startLoader()
        userID = Commons.getStringPreference(this, "sisUsu_Id").toString()
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.GETPAYMENTS, Constans.METHOD_GETPAYMENTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val responsePayments = response as ResponseList
                val payments = responsePayments.list as SoapObject?

                if (responsePayments.mensajeId == "129") {
                    noCardView.visibility = View.VISIBLE
                    cardsView.visibility = View.GONE
                } else {
                    noCardView.visibility = View.GONE
                    cardsView.visibility = View.VISIBLE
                    var count = 0
                    if (payments?.propertyCount == null) {
                        return@runOnUiThread
                    }
                    while (count < payments.propertyCount!!) {
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
                    CustomDialog(
                        this,
                        this,
                        getString(R.string.titleError),
                        error.toString(),
                        success = false,
                        action = false
                    )
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
        rvCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvadapter = PaymentsRecyclerAdapter(this, this)
        rvCards.adapter = rvadapter
        rvadapter.setElement(paymentsModel)

        val swipeController = SwipeController(object : SwipeControllerActions() {
            override fun onDeleteClicked(position: Int) {
                showDeleteAlert(position)
            }

            override fun onEditClicked(position: Int) {
                showEditCard(position)
            }
        })
        val itemTouchhelper = ItemTouchHelper(swipeController)
        itemTouchhelper.attachToRecyclerView(rvCards)
        rvCards.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                swipeController.onDraw(c)
            }
        })
    }

    private fun showDeleteAlert(position: Int) {
        positionCard = position
        val dialog =
            CustomDialogDelete(
                this,
                this,
                getString(R.string.titleDeletePaymentMessage),
                getString(R.string.messageDeleteCard),
                false
            )
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun deleteCard(position: Int) {
        startLoader()
        userID = Commons.getStringPreference(this, "sisUsu_Id").toString()
        val idCard = paymentsModel[position].idCard.toString()
        var params: MutableMap<String, String> =
            mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "4", "Tc_id" to idCard)
        RestService.baseRequest(this, RequestEnum.GETPAYMENTS, Constans.METHOD_GETPAYMENTS, params, { response ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(
                        this,
                        this,
                        getString(R.string.titleDeletePaymentSuccess),
                        getString(R.string.messageDeleteCardSuccess),
                        success = true,
                        action = false
                    )
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(
                        this,
                        this,
                        getString(R.string.titleError),
                        error.toString(),
                        success = false,
                        action = false
                    )
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

    private fun showEditCard(position: Int) {
        val bundle = Bundle()
        bundle.putParcelable("payment", paymentsModel[position])
        bundle.putString("title", "Editar tarjeta")
        Commons.sendToViewWithBundle(this, AddPaymentActivity::class.java, bundle)
    }

    override fun showPaymentDetail(payment: PaymentsModel) {
        val bundle = Bundle()
        bundle.putParcelable("payment", payment)
        Commons.sendToViewWithBundle(this, PaymentDetailActivity::class.java, bundle)
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            onBackPressed()
        }
        if (success) {
            paymentsModel = ArrayList()
            getPayments()
        }
    }

    override fun onDeleteClick() {
        deleteCard(positionCard)
    }


    override fun onCancelClick() {

    }
}
