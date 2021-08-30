package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_detail_tag.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.domain.model.VehicleCatalog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogDelete
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.util.*

class DetailTagActivity: BaseActivity(), CustomDialogDelete.OnClickDeleteListener, CustomDialog.OnClickSetListener {

    var userID = ""
    var tagID = ""
    var nickName = ""
    var plate = ""
    var type = ""
    var register = true
    var status = ""
    var positionScroll = 0
    var esPredeterminado = ""
    var mensajeLogin = ""
    private var arrayVehicleCatalog: ArrayList<VehicleCatalog> = ArrayList()

    override fun getLayoutId(): Int {
        return R.layout.activity_detail_tag
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        tagID = intent.extras.getString("tagID")
        nickName = intent.extras.getString("nickName")
        plate = intent.extras.getString("plate")
        type = intent.extras.getString("vehicleType")
        status =  intent.extras.getString("status")
        positionScroll = intent.extras.getInt("positionScroll")
        esPredeterminado = intent.extras.getString("esPredeterminado")
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        mensajeLogin = intent.extras.getString("mensaje")
        txtvTagNumber.text = tagID
        txtPlate.setText(plate)
        txtNickName.setText(nickName)
        getVehicle()
        if (arrayVehicleCatalog.size == 0) {
            runOnUiThread {
                var dialog = CustomDialog(this, this, getString(R.string.titleError), getString(R.string.catalogError), false, false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }

        if (status == "Inactivo") {
            btnSave.isEnabled = false
        }
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        if(esPredeterminado == "true"){
            checkbox1.isEnabled = false
            checkbox1.isChecked = true
        }
        btnDelete.setOnClickListener {
            val dialog =
                CustomDialogDelete(this, this, getString(R.string.titleDeleteTag), getString(R.string.messageDeleteTag), false)
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
        }

        btnSave.setOnClickListener {
            registerNewTag(setNewData())
        }

    }

    fun getVehicle() {
        arrayVehicleCatalog = Session.getValue() as ArrayList<VehicleCatalog>
        val descriptionArray = ArrayList<String>()
        var position = 0
        descriptionArray.add("Seleccione una opción")
        arrayVehicleCatalog.forEach { vehicle ->
            descriptionArray.add(vehicle.description)
            if (vehicle.classID == type) {
                position = descriptionArray.size - 1
            }
        }

        var spinnerArrayAdapter = ArrayAdapter<String>(
            this, R.layout.spinner_item, descriptionArray
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerVehicle.adapter = spinnerArrayAdapter

        spinnerVehicle.setSelection(position)
    }

    fun setNewData(): MutableMap<String, String> {
        register = true
        plate = txtPlate.text.toString().trim()
        nickName = txtNickName.text.toString().trim()
        val position = spinnerVehicle.selectedItemPosition
        var vehicle = ""
        if (position >= 1) {
            vehicle = arrayVehicleCatalog.get(spinnerVehicle.selectedItemPosition - 1).classID
        } else {
            register = false
        }
        if (plate.isEmpty()) {
            register = false
            txtvPlateError.text = "Campo obligatorio"
            setErrorView(txtvPlate, txtvPlateError, txtPlate)
        }
        if (nickName.isEmpty()) {
            register = false
            txtvNickNameError.text = "Campo obligatorio"
            setErrorView(txtvNickName, txtvNickNameError, txtNickName)
        }


        if(checkbox1.isChecked){
            var params: MutableMap<String, String> = mutableMapOf(
                "sisUsu_Id" to userID,
                "tag" to tagID,
                "clase" to vehicle,
                "placa" to plate,
                "noEconomico" to nickName,
                "Dig_Verificador" to "0",
                "EsPredeterminado" to "true"
            )
            return params
        } else{
            var params: MutableMap<String, String> = mutableMapOf(
                "sisUsu_Id" to userID,
                "tag" to tagID,
                "clase" to vehicle,
                "placa" to plate,
                "noEconomico" to nickName,
                "Dig_Verificador" to "0"
            )
            return params
        }
    }

    fun registerNewTag(params: MutableMap<String, String>) {
        if (!register) {
            return
        }
        startLoader()
        RestService.baseRequest(
            this,
            RequestEnum.NEWTAG, Constans.METHOD_NEW_TAG, params, { response ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(
                            this,
                            this,
                            getString(R.string.updateTagTittle),
                            getString(R.string.updateTagDescription),
                            true,
                            false
                        )
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), error.toString(), false, false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }, { exception ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleError), exception, false, false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                }
            })
    }

    override fun onDeleteClick() {
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tag" to tagID)
        RestService.baseRequest(this, RequestEnum.DELETE, Constans.METHOD_DELETE, params, { response ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleDeleteTagSuccess), getString(R.string.messageDeleteTagSuccess), success = true, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
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

    fun setErrorView(textView: TextView, errorTextView: TextView, editText: EditText) {
        editText.isEnabled = false
        errorTextView.visibility = View.VISIBLE
        textView.setTextColor(resources.getColor(R.color.errorRed))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    editText.isEnabled = true
                    errorTextView.visibility = View.GONE
                    if (editText.isFocused) {
                        textView.setTextColor(resources.getColor(R.color.blue))
                    } else {
                        textView.setTextColor(resources.getColor(mx.com.nut.basecleanarquitecture.R.color.grayText))
                    }

                }
            }
        }, 1000)
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.code)) {
            onBackPressed()
        }
        if (success) {
            val bundle = Bundle()
            bundle.putBoolean("canScroll", true)
            bundle.putBoolean("validateAccount", false)
            bundle.putString("mensaje", mensajeLogin)
            if(checkbox1.isChecked){
                positionScroll = 0
            }
            bundle.putInt("positionScroll", positionScroll)
            Commons.sendToViewFinishBundle(this, TagsActivity::class.java, bundle)
            finish()
        }
    }

    override fun onCancelClick() {

    }


}