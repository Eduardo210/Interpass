package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.view.View
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_qr.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialogDelete
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity

class QRActivity: BaseActivity(), CustomDialogDelete.OnClickDeleteListener, CustomDialog.OnClickSetListener {
    var tag = ""
    var code = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_qr
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        setToolBarHeight(navbar)
        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }
        btnNewCode.setOnClickListener {
            btnNewCode.isEnabled = false
            generateCode()
        }
        btnDeleteCode.setOnClickListener {
            btnDeleteCode.isEnabled = false
            showDeleteAlert()
        }
        setupView()

    }

    override fun onResume() {
        super.onResume()
        getCode()
    }

    private fun setupView() {
        tag = intent.extras.getString("tagnumber")
        txtTagNumber.text = tag

    }

    private fun setupQRImage(qrString: String) {
        val barCodeEncoder = BarcodeEncoder()
        val bitmap= barCodeEncoder.encodeBitmap(qrString, BarcodeFormat.QR_CODE, resources.getDimensionPixelSize(R.dimen.dp210), resources.getDimensionPixelSize(R.dimen.dp210))
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun setupViewNoCode() {
        imageView.visibility = View.INVISIBLE
        txtStatus.visibility = View.INVISIBLE
        btnDeleteCode.visibility = View.GONE
        btnNewCode.visibility = View.VISIBLE
        txtNoQr.visibility = View.VISIBLE
        noQRImageView.visibility = View.VISIBLE
    }

    private fun setupViewCode() {
        imageView.visibility = View.VISIBLE
        txtStatus.visibility = View.VISIBLE
        btnDeleteCode.visibility = View.VISIBLE
        btnNewCode.visibility = View.GONE
        txtNoQr.visibility = View.INVISIBLE
        noQRImageView.visibility = View.INVISIBLE
    }

    private fun getCode() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("tag" to tag, "tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.GENERATECODE, Constans.GENERATE_CODE, params, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponseList
                if (response.mensajeId == "128") {
                    val responseCode = response
                    val code = responseCode.list as String
                    this.code = code
                    setupQRImage(code)
                    setupViewCode()
                    validateCode()
                } else if (response.mensajeId == "0") {
                    setupViewNoCode()
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

    private fun generateCode() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("tag" to tag, "tipoOperacion" to "2")
        RestService.baseRequest(this, RequestEnum.GENERATECODE, Constans.GENERATE_CODE, params, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponseList
                if (response.mensajeId == "128") {
                    val responseCode = response
                    val code = responseCode.list as String
                    this.code = code
                    setupQRImage(code)
                    validCode()
                    setupViewCode()
                } else {
                    setupViewNoCode()
                }
                btnNewCode.isEnabled = true
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        })
    }

    private fun validateCode() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("QR" to code, "tipoOperacion" to "1")
        RestService.baseRequest(this, RequestEnum.VALIDATECODE, Constans.VALIDATE_CODE, params, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponseList
                if (response.mensajeId == "128") {
                    setupQRImage(code)
                    validCode()
                } else {
                    invalidCode()
                }
                btnNewCode.isEnabled = true
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        })
    }

    private fun deleteCode() {
        startLoader()
        var params: MutableMap<String, String> = mutableMapOf("QR" to code, "tipoOperacion" to "2")
        RestService.baseRequest(this, RequestEnum.VALIDATECODE, Constans.VALIDATE_CODE, params, { response ->
            stopLoader()
            runOnUiThread {
                val response = response as ResponseList
                if (response.mensajeId == "128") {
                    val dialog =
                        CustomDialog(this, this, "Código eliminado ", "El código ha sido eliminado correctamente. ", success = true, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                    btnNewCode.isEnabled = true
                }
                btnNewCode.isEnabled = true
            }
        }, { error ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), error.toString(), success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        }, { exception ->
            stopLoader()
            runOnUiThread {
                val dialog =
                    CustomDialog(this, this, getString(R.string.titleError), exception, success = false, action = false)
                dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(false)
                dialog.show()
                btnNewCode.isEnabled = true
            }
        })
    }

    private fun showDeleteAlert() {
        val dialog =
            CustomDialogDelete(
                this,
                this,
                "¿Está seguro de eliminar el código?",
                "Esta opción no puede deshacerse.",
                true
            )
        dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun validCode() {
        txtStatus.text = "VIGENTE"
        txtStatus.setBackgroundResource(R.drawable.label_green)
    }

    private fun invalidCode() {
        txtStatus.text = "VENCIDO"
        txtStatus.setBackgroundResource(R.drawable.label_red)
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet) || success) {
            onBackPressed()
        }
    }

    override fun onDeleteClick() {
        btnDeleteCode.isEnabled = true
        deleteCode()
        getCode()
    }

    override fun onCancelClick() {
        btnDeleteCode.isEnabled = true
    }


}