package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_register_tag.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.*
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.domain.model.PrefixCatalog
import mx.com.nut.basecleanarquitecture.domain.model.VehicleCatalog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogTag
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import com.google.zxing.integration.android.IntentIntegrator
import android.widget.*
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.util.*


class RegisterTagActivity: BaseActivity(), CustomDialogTag.OnClickSetListener, CustomDialog.OnClickSetListener {
    lateinit var name: String
    lateinit var lastName: String
    lateinit var lastName2: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var userID: String

    var tagString = ""
    var dig = ""
    var plate = ""
    var nickName = ""
    var vehiclePosition: Int? = 0
    var prefixPosition: Int? = 0
    var addDataSave = false
    var register = true
    var mensajeLogin = ""
    private var arrayVehicleCatalog: ArrayList<VehicleCatalog> = ArrayList()
    private var arrayPrefixCatalog: ArrayList<PrefixCatalog> = ArrayList()

    lateinit var intentIntegrator: IntentIntegrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        if(intent.extras.getString("mensaje")!=null){
            mensajeLogin = intent.extras.getString("mensaje")
        }

        setToolBarHeight(navbar)
        getVehicle()
        getPrefix()
        setListeners()
        setData()
        if (arrayVehicleCatalog.size == 0 || arrayPrefixCatalog.size == 0) {
            runOnUiThread {
                var dialog = CustomDialog(this, this, getString(R.string.titleError), getString(R.string.catalogError), false, false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_register_tag
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                //Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                var number = result.contents
                number = number.trim()
                if (number.contains("INTP")) {
                    addDataSave = true
                    txtTagNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    val numberT = number.substringAfter("INTP")
                    val tagNumber = number.subSequence(4, 12)
                    val dig = number.substring(number.lastIndex)
                    txtTagNumber.setText(tagNumber)
                    txtDig.setText(dig)
                    spinnerPrefix.setSelection(2)
                } else if (number.contains("021")) {
                    addDataSave = true
                    txtTagNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                    val numberT = number.substringAfter("021")
                    val tagNumber = numberT.subSequence(0, 11)
                    val dig = number.substring(number.lastIndex)
                    txtTagNumber.setText(tagNumber)
                    txtDig.setText(dig)
                    txtTagNumber.setText(numberT)
                    spinnerPrefix.setSelection(1)
                }
                Commons.saveStringPreference(this, "tagNumber", txtTagNumber.text.toString().trim())
                Commons.saveStringPreference(this, "digit", txtDig.text.toString().trim())
                Commons.saveIntPreference(this, "prefixPosition", spinnerPrefix.selectedItemPosition)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
                            getString(R.string.newTagTittle),
                            getString(R.string.newTagDescription),
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

    fun registerUser(params: MutableMap<String, String>) {
        if (!register) {
            return
        }
        startLoader()
        RestService.baseRequest(
            this,
            RequestEnum.REGISTER, Constans.METHOD_REGISTER, params, { response ->
                stopLoader()
                runOnUiThread {
                    val dialog =
                        CustomDialog(
                            this,
                            this,
                            getString(R.string.titleRegisterDialog),
                            getString(R.string.dialogSuccess),
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

    fun getVehicle() {
        arrayVehicleCatalog = Session.getValue() as ArrayList<VehicleCatalog>
        val descriptionArray = ArrayList<String>()

        descriptionArray.add("Seleccione una opción")
        arrayVehicleCatalog.forEach { vehicle ->
            descriptionArray.add(vehicle.description)
        }

        var spinnerArrayAdapter = ArrayAdapter<String>(
            this, R.layout.spinner_item, descriptionArray
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerVehicle.adapter = spinnerArrayAdapter

    }

    fun getPrefix() {
        arrayPrefixCatalog = Session.getValuePrefix() as ArrayList<PrefixCatalog>
        val descriptionArray = ArrayList<String>()
        descriptionArray.add("Seleccione una opción")
        arrayPrefixCatalog.forEach { prefix ->
            descriptionArray.add(prefix.tpPrefijo_Nombre)
        }

        var spinnerArrayAdapter = ArrayAdapter<String>(
            this, R.layout.spinner_item, descriptionArray
        )
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinnerPrefix.adapter = spinnerArrayAdapter
    }

    fun setRegisterData(): MutableMap<String, String> {
        name = intent.extras.getString("name")
        lastName = intent.extras.getString("lastName")
        lastName2 = intent.extras.getString("lastName2")
        email = intent.extras.getString("email")
        phone = intent.extras.getString("phone")
        tagString = txtTagNumber.text.toString().trim()
        dig = txtDig.text.toString().trim()
        plate = txtPlate.text.toString().trim()
        nickName = txtNickName.text.toString().trim()
        val vehicle = arrayVehicleCatalog.get(spinnerVehicle.selectedItemPosition - 1).classID
        val prefixObj = arrayPrefixCatalog.get(spinnerPrefix.selectedItemPosition - 1)
        var prefix = ""
        if (prefixObj.tpPrefijo_Nombre != "021") {
            prefix = prefixObj.tpPrefijo_Nombre
            if (tagString.length != 8) {
                txtvTagNumberError.text = "8 caracteres"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else {
                register = true
            }
        } else {
            if (tagString.substring(0, 3) != "021") {
                txtvTagNumberError.text = "Formato incorrecto"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else if (tagString.length != 11)  {
                txtvTagNumberError.text = "11 caracteres"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else {
                register = true
            }

        }

        var params: MutableMap<String, String> = mutableMapOf(
            "nombre" to name,
            "apellidoPaterno" to lastName,
            "apellidoMaterno" to lastName2,
            "email" to email,
            "telefono" to phone,
            "tag" to prefix.plus(tagString),
            "clase" to vehicle,
            "placa" to plate,
            "noEconomico" to nickName,
            "Dig_Verificador" to dig
        )

        return params
    }

    fun setNewData(): MutableMap<String, String> {
        userID = intent.extras.getString("sisUsu_Id")
        tagString = txtTagNumber.text.toString().trim()
        dig = txtDig.text.toString().trim()
        plate = txtPlate.text.toString().trim()
        nickName = txtNickName.text.toString().trim()
        val vehicle = arrayVehicleCatalog.get(spinnerVehicle.selectedItemPosition - 1).classID
        val prefixObj = arrayPrefixCatalog.get(spinnerPrefix.selectedItemPosition - 1)
        var prefix = ""
        if (prefixObj.tpPrefijo_Nombre != "021") {
            prefix = prefixObj.tpPrefijo_Nombre
            if (tagString.length != 8) {
                txtvTagNumberError.text = "8 caracteres"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else {
                register = true
            }
        } else {
            if (tagString.substring(0, 3) != "021") {
                txtvTagNumberError.text = "Formato inválido"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else if (tagString.length != 11) {
                txtvTagNumberError.text = "11 caracteres"
                setErrorView(txtvTagNumber, txtvTagNumberError, txtTagNumber)
                register = false
            } else {
                register = true
            }

        }

        if(checkbox1.isChecked){
            var params: MutableMap<String, String> = mutableMapOf(
                "sisUsu_Id" to userID,
                "tag" to prefix.plus(tagString),
                "clase" to vehicle,
                "placa" to plate,
                "noEconomico" to nickName,
                "Dig_Verificador" to dig,
                "EsPredeterminado" to "true"
            )
            return params
        } else{
            var params: MutableMap<String, String> = mutableMapOf(
                "sisUsu_Id" to userID,
                "tag" to prefix.plus(tagString),
                "clase" to vehicle,
                "placa" to plate,
                "noEconomico" to nickName,
                "Dig_Verificador" to dig
            )
            return params
        }


    }

    fun setListeners() {
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        intentIntegrator.setPrompt(getString(R.string.tagScannerMessage))
        intentIntegrator.setCameraId(0)
        intentIntegrator.setBeepEnabled(false)
        intentIntegrator.setBarcodeImageEnabled(false)
        intentIntegrator.captureActivity = ScannerActivity::class.java
        btnTagScan.setOnClickListener {
            intentIntegrator.initiateScan()
        }

        btnQuestion.setOnClickListener {
            val dialog = CustomDialogTag(
                this@RegisterTagActivity
            )
            dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCancelable(false)
            dialog.show()
        }

        btnNoticePrivace.setOnClickListener {
            Commons.sendToView(this, PrivacyActivity::class.java)
        }

        btnRegister.setOnClickListener {
            if (intent.extras.getBoolean("newTag")) {
                registerNewTag(setNewData())
            } else {
                registerUser(setRegisterData())
            }

        }

        val context = this
        spinnerPrefix.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Commons.saveIntPreference(context, "prefixPosition", position)
                if (addDataSave) {
                    addDataSave = false
                } else {
                    txtTagNumber.setText("")
                }

                if (position == 0) {
                    txtTagNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(0))
                } else {
                    val prefix = arrayPrefixCatalog[position - 1]
                    if (prefix.tpPrefijo_Nombre == "021") {
                        txtTagNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(11))
                        if (txtTagNumber.length() != 11) {
                            txtTagNumber.setText("021")
                        }
                    } else {
                        txtTagNumber.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                    }
                }

            }

        }

        spinnerVehicle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val text1 = txtPlate.text.toString().trim()
                val text3 = txtDig.text.toString().trim()
                val text4 = txtTagNumber.text.toString().trim()
                val text6 = txtNickName.text.toString().trim()
                if (position == 0) {
                    btnRegister.isEnabled = false
                } else if (!text1.isEmpty() && !text3.isEmpty() && !text4.isEmpty() && !text6.isEmpty() && checkbox.isChecked && spinnerVehicle.selectedItemPosition != 0) {
                    btnRegister.isEnabled = true
                }

                Commons.saveIntPreference(context, "vehiclePosition", position)
            }

        }

        spinnerPrefix.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setBlueColor(txtvPrefix)
                        txtTagNumber.clearFocus()
                    }

                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        spinnerVehicle.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        setBlueColor(txtvVehicleType)
                    }

                }
                return v?.onTouchEvent(event) ?: true
            }
        })

        txtTagNumber.afterTextChanged {
            setBlueColor(txtvTagNumber)
            validData()
            Commons.saveStringPreference(this, "tagNumber", txtTagNumber.text.toString().trim())
        }

        txtDig.afterTextChanged {
            setBlueColor(txtvDig)
            validData()
            Commons.saveStringPreference(this, "digit", txtDig.text.toString().trim())

        }

        txtPlate.afterTextChanged {
            setBlueColor(txtvPlate)
            validData()
            Commons.saveStringPreference(this, "plate", txtPlate.text.toString().trim())
        }

        txtNickName.afterTextChanged {
            setBlueColor(txtvNickName)
            validData()
            Commons.saveStringPreference(this, "nickName", txtNickName.text.toString().trim())
        }

        checkbox.setOnClickListener {
            validData()
            when {
                checkbox.isChecked -> {
                    Commons.saveStringPreference(this, "checked", "1")
                }
                else -> {
                    Commons.saveStringPreference(this, "checked", "0")
                }
            }

        }

    }

    fun setErrorView(textView: TextView, errorTextView: TextView, editText: EditText) {
        scrollView.scrollTo(0, editText.top - 30)
        editText.isEnabled = false
        errorTextView.visibility = View.VISIBLE
        textView.setTextColor(resources.getColor(R.color.errorRed))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    editText.isEnabled = true
                    editText.requestFocus()
                    errorTextView.visibility = View.GONE
                    if (editText.isFocused) {
                        textView.setTextColor(resources.getColor(R.color.blue))
                    } else {
                        textView.setTextColor(resources.getColor(R.color.grayText))
                    }

                }
            }
        }, 1000)
    }

    fun validData() {
        val text1 = txtPlate.text.toString().trim()
        val text2 = txtDig.text.toString().trim()
        val text3 = txtTagNumber.text.toString().trim()
        val text4 = txtNickName.text.toString().trim()
        when {
            !text1.isEmpty() && !text2.isEmpty() && !text3.isEmpty() && !text4.isEmpty() && checkbox.isChecked && spinnerVehicle.selectedItemPosition != 0 -> {
                btnRegister.isEnabled = true
            }
            else -> {
                btnRegister.isEnabled = false
            }
        }
    }

    fun setBlueColor(textView: TextView) {
        txtvPrefix.setTextColor(resources.getColor(R.color.grayText))
        txtvTagNumber.setTextColor(resources.getColor(R.color.grayText))
        txtvDig.setTextColor(resources.getColor(R.color.grayText))
        txtvVehicleType.setTextColor(resources.getColor(R.color.grayText))
        txtvPlate.setTextColor(resources.getColor(R.color.grayText))
        txtvNickName.setTextColor(resources.getColor(R.color.grayText))

        textView.setTextColor(resources.getColor(R.color.blue))
    }

    fun setData() {
        tagString = Commons.getStringPreference(this, "tagNumber").toString()
        dig = Commons.getStringPreference(this, "digit").toString()
        plate = Commons.getStringPreference(this, "plate").toString()
        nickName = Commons.getStringPreference(this, "nickName").toString()
        vehiclePosition = Commons.getIntPreference(this, "vehiclePosition")
        prefixPosition = Commons.getIntPreference(this, "prefixPosition")
        //checkbox.isChecked = Commons.getStringPreference(this, "checked").toString() == "1"

        if (intent.extras.getBoolean("newTag")) {
            checkbox.visibility = View.GONE
            btnNoticePrivace.visibility = View.GONE
            txtvTitle.text = getString(R.string.titleNewTag)
            checkbox.isChecked = true
        } else {
            checkbox.visibility = View.VISIBLE
            btnNoticePrivace.visibility = View.VISIBLE
            txtvTitle.text = getString(R.string.titleTag)
        }

        if (tagString != "") {
            addDataSave = true
        }

        txtTagNumber.setText(tagString)
        txtDig.setText(dig)
        txtPlate.setText(plate)
        txtNickName.setText(nickName)


        vehiclePosition?.let { spinnerVehicle.setSelection(it) }
        prefixPosition?.let { spinnerPrefix.setSelection(it) }

    }

    override fun onClick(code: String, success: Boolean) {
        if (success) {
            if (intent.extras.getBoolean("newTag")) {
                val bundle = Bundle()
                bundle.putBoolean("canScroll", false)
                bundle.putBoolean("validateAccount", false)
                bundle.putString("mensaje", mensajeLogin)
                Commons.sendToViewFinishBundle(this, TagsActivity::class.java, bundle)

            } else {
                Commons.sendToViewFinish(this, LoginActivity::class.java)
            }
            finish()
        }
        if (code == getString(R.string.code)) {
            onBackPressed()
        }
    }

}