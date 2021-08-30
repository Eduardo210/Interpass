package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_profile.*
import mx.com.nut.basecleanarquitecture.GlideApp
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.commons.afterTextChanged
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class EditProfileActivity : BaseActivity(), CustomDialog.OnClickSetListener {

    private var bitmap: Bitmap? = null
    private val GALLERY = 1
    private val CAMERA = 2
    var userID = ""
    var name = ""
    var lastName = ""
    var surName = ""
    var email = ""
    var phone = ""
    var sisUsI = ""
    var sisUsC = ""

    override fun getLayoutId() = R.layout.activity_edit_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarHeight(navbar)
        setListeners()
        setData()

    }

    private fun setData() {
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        name = intent.extras.getString("name")
        lastName = intent.extras.getString("lastName")
        surName = intent.extras.getString("surName")
        email = intent.extras.getString("email")
        phone =  intent.extras.getString("phone")
        sisUsI = intent.extras.getString("sisUsI")
        sisUsC = intent.extras.getString("sisUsC")

        txtName.setText(name)
        txtLastName.setText(lastName)
        txtSurName.setText(surName)
        txtEmail.setText(email)
        txtPhone.setText(phone)
    }

    private fun updateProfile() {
        startLoader()
        var byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        var byteArray = byteArrayOutputStream.toByteArray()
        var image = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        var params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "0", "nombre" to txtName.text.toString(),
            "apellidoPaterno" to txtLastName.text.toString(), "apellidoMaterno" to txtSurName.text.toString(), "calle" to "",
            "colonia" to "", "estado" to "", "municipio" to "", "cp" to "", "telefono1" to txtPhone.text.toString(), "telefono2" to "",
            "fax" to "", "email" to txtEmail.text.toString(), "SisUsu_Identificador" to sisUsI, "SisUsu_Contrasenia" to sisUsC, "imagen" to image)
        RestService.baseRequest(this,
            RequestEnum.GETPROFILE, Constans.METHOD_GETPROFILE, params, { response ->
                runOnUiThread {
                    stopLoader()
                    val dialog =
                        CustomDialog(this, this, getString(R.string.titleEditProfileSuccess), getString(R.string.profileEditMessage), success = true, action = false)
                    dialog.window.setBackgroundDrawableResource(android.R.color.transparent)
                    dialog.setCancelable(false)
                    dialog.show()
                    Session.updateProfile = true
                    Session.profileData = null
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

    private fun validateData(): Boolean {
        if (isEmailValid(txtEmail.text.toString())) {
            runOnUiThread {
                txtvEmailError.text = "Correo electrónico inválido"
                setErrorView(txtvLabelEmail, txtvEmailError, txtEmail)
            }
            return false
        }
        val phone = txtPhone.text.toString().trim()
        if (phone.length in 1..9 || phone.substring(0, 1) == "0") {
            runOnUiThread {
                txtvPhoneError.text = "Número de teléfono inválido."
                setErrorView(txtvLabelPhone, txtvPhoneError, txtPhone)
            }
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setErrorView(textView: TextView, errorTextView: TextView, editText: EditText) {
        editText.isEnabled = false
        errorTextView.visibility = View.VISIBLE
        textView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.errorRed))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    editText.isEnabled = true
                    errorTextView.visibility = View.GONE
                    if (editText.isFocused) {
                        textView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.blue))
                    } else {
                        textView.setTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.grayText))
                    }

                }
            }
        }, 2000)
    }


    private fun setListeners() {
        btnSave.setOnClickListener {
            if (validateData()) {
                updateProfile()
            }
        }

        btnBack.setOnClickListener {
            btnBack.isEnabled = false
            onBackPressed()
        }

        imgAccount.setOnClickListener { showPictureDialog() }

        txtName.afterTextChanged {
            setColorBlue(txtvLabelName)
            validData()
        }

        txtLastName.afterTextChanged {
            setColorBlue(txtvLabelLastName)
            validData()
        }

        txtSurName.afterTextChanged {
            setColorBlue(txtvLabelSurName)
            validData()
        }

        txtEmail.afterTextChanged {
            setColorBlue(txtvLabelEmail)
            validData()
        }

        txtPhone.afterTextChanged {
            setColorBlue(txtvLabelPhone)
            validData()
        }
    }

    private fun validData() {
        val text1 = txtName.text.toString().trim()
        val text2 = txtLastName.text.toString().trim()
        val text3 = txtSurName.text.toString().trim()
        val text4 = txtEmail.text.toString().trim()
        val text5 = txtPhone.text.toString().trim()
        when {
            text1.isNotEmpty() && text2.isNotEmpty() && text3.isNotEmpty() && text4.isNotEmpty() && text5.isNotEmpty() -> {
                btnSave.isEnabled = true
            } else -> {
            btnSave.isEnabled = false
        }
        }
    }

    private fun setColorBlue(textView: TextView) {
        txtvLabelName.setTextColor(ContextCompat.getColor(this, R.color.grayText))
        txtvLabelLastName.setTextColor(ContextCompat.getColor(this, R.color.grayText))
        txtvLabelSurName.setTextColor(ContextCompat.getColor(this, R.color.grayText))
        txtvLabelEmail.setTextColor(ContextCompat.getColor(this, R.color.grayText))
        txtvLabelPhone.setTextColor(ContextCompat.getColor(this, R.color.grayText))

        textView.setTextColor(ContextCompat.getColor(this, R.color.blue))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPictureDialog() {
        var pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setPositiveButton(R.string.option_camera) { dialog, _ ->
            takePhotoFromCamera()
            dialog.dismiss()
        }
        pictureDialog.setNegativeButton(R.string.option_gallery) { dialog, _ ->
            choosePhotoFromGallery()
            dialog.dismiss()
        }
        pictureDialog.setTitle(R.string.change_photo_dialog_title)
        pictureDialog.setMessage(R.string.change_photo_dialog_message)

        pictureDialog.show()

    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            data?.let {
                val contentURI = it.data
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
                    //imgAccount.setImageBitmap(bitmap)

                    GlideApp.with(this)
                        .load(bitmap)
                        .circleCrop()
                        .into(imgAccount)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CAMERA) {
            data?.let {
                val thumbnail = it.extras!!.get("data") as Bitmap
                bitmap = thumbnail
                //imgAccount.setImageDrawable(BitmapDrawable(resources, bitmap))

                GlideApp.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(imgAccount)
            }
        }
    }

    private fun choosePhotoFromGallery() {

        when {
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 30 )
            else -> {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY)
            }
        }

    }

    private fun takePhotoFromCamera() {

        when{
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=  PackageManager.PERMISSION_GRANTED -> ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 10 )
            else -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            10 -> {
                if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) !=  PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 20 )
                }
            }
            20-> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
            30-> {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, GALLERY)
            }
            else -> Toast.makeText(this,"los permisos son necesarios", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(code: String, success: Boolean) {
        if (success) {
            onBackPressed()
        }
    }

}