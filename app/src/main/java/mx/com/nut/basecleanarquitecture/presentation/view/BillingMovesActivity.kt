package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_billing_moves.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.BillingModel
import mx.com.nut.basecleanarquitecture.data.entity.MovesModel
import mx.com.nut.basecleanarquitecture.data.entity.MovesToBillingModel
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.adapter.BillingMovesRecyclerAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import org.ksoap2.serialization.SoapObject

class BillingMovesActivity: BaseActivity(), CustomDialog.OnClickSetListener, BillingMovesRecyclerAdapter.OnItemClickListener {
    var userID = ""
    var tag = ""
    var billingsArray: ArrayList<BillingModel> = ArrayList()
    var movesArray: ArrayList<MovesModel> = ArrayList()
    var movesBillingArray: ArrayList<MovesToBillingModel> = ArrayList()
    var movesToBilling: ArrayList<MovesToBillingModel> = ArrayList()
    lateinit var rvadapter: BillingMovesRecyclerAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_billing_moves
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        setToolBarHeight(navbar)
        tag = intent.extras.getString("tagnumber")
        txtTagNumber.text = tag
        movesArray = intent.extras.getParcelableArrayList("moves")
        setDataAdapter()
        if (movesBillingArray.size > 0) {
            rvMoves.visibility = View.VISIBLE
            txtv3.visibility = View.INVISIBLE
        } else {
            rvMoves.visibility = View.INVISIBLE
            txtv3.visibility = View.VISIBLE
        }
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnBilling.setOnClickListener {
            if (movesToBilling.size > 0 && spinnerRFC.selectedItemPosition > 0)  {
                btnBilling.isEnabled = false
                sendMovesToBilling()
            } else {
                showAlert("Error", "Faltan datos por llenar")
            }

        }
    }

    override fun onResume() {
        super.onResume()
        if (Session.updateBillingData) {
            if (Session.paymentsModel.size == 0) {
                billingsArray = ArrayList()
                getBillings()
                Session.updateBillingData = false
            } else {
                billingsArray = Session.billingModel
                setBillingSpinner()
                Session.updateBillingData = false
            }
        }
    }

    private fun setBillingSpinner() {
        val descriptionArray = ArrayList<String>()

        descriptionArray.add("Seleccione una opción")
        billingsArray.sortWith(compareByDescending { it.isPrede })
        billingsArray.forEach { billing ->
            descriptionArray.add(billing.rfc.toString())
        }

        var spinnerArrayAdapter = ArrayAdapter<String>(
            this, R.layout.spinner_item, descriptionArray
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerRFC.adapter = spinnerArrayAdapter

    }

    private fun sendMovesToBilling() {
        startLoader()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        val rfc = billingsArray.get(spinnerRFC.selectedItemPosition - 1).rfcId.toString()
        var paramsLista: ArrayList<String> = ArrayList()
        movesToBilling.forEach { move ->
            paramsLista.add(move.trnId!!)
        }
        var params: MutableMap<String, String> = mutableMapOf("sisUsuId" to userID, "RFCId" to rfc)
        RestService.baseRequestList(this, RequestEnum.SENDBILLINGDATA, Constans.BILLING_DATA, params, paramsLista, { response ->
            stopLoader()
            runOnUiThread {
                btnBilling.isEnabled = true
                val responsePayments = response
                val dialog =
                    CustomDialog(this, this, "Factura generada", "!Listo! tu facturación ha sido creada con éxito.", success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()

            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                btnBilling.isEnabled = true
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                btnBilling.isEnabled = true
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        })
    }

    private fun setDataAdapter() {
        movesArray.forEach { move ->
            val moveID: String? = move.tipoMovimientoID
            when (moveID) {
                "101", "102", "103", "104" -> {
                    movesBillingArray.add(
                        MovesToBillingModel(
                            move.id,
                            move.date,
                            move.hour,
                            move.description,
                            move.amount,
                            move.type,
                            move.balance,
                            move.carril,
                            move.vehicle,
                            move.tagNumber,
                            move.squareName,
                            move.status,
                            move.tipoMovimientoID,
                            move.trnId,
                            false
                        )
                    )
                }
            }
        }
        rvMoves.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvadapter = BillingMovesRecyclerAdapter(this,this)
        rvMoves.adapter = rvadapter
        rvadapter.setElement(movesBillingArray)
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

                } else {
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
                    Session.billingModel = billingsArray
                    setBillingSpinner()
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
            onBackPressed()
        }
    }

    override fun selectBilling(billing: MovesToBillingModel, selected: Boolean) {
        if (selected) {
            movesToBilling.remove(billing)
        } else {
            movesToBilling.add(billing)
        }
        billing.selected = !selected
        rvadapter.notifyDataSetChanged()
    }


}