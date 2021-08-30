package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_my_account.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseList
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.Commons.CustomDialog
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import org.ksoap2.serialization.SoapObject

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyAccountActivity : BaseActivity(), CustomDialog.OnClickSetListener {

    var userID = ""
    lateinit var profileData: SoapObject
    override fun getLayoutId() = R.layout.activity_my_account

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolBarHeight(navbar)
        setToolbar()
        userID = Commons.getStringPreference(this,"sisUsu_Id").toString()
        btnEditAccount.setOnClickListener {
            btnEditAccount.isEnabled = false
            val bundle = Bundle()
            bundle.putString("name", profileData.getPrimitivePropertyAsString("Nombre"))
            bundle.putString("lastName", profileData.getPrimitivePropertyAsString("APaterno"))
            bundle.putString("surName", profileData.getPrimitivePropertyAsString("AMaterno"))
            bundle.putString("email", profileData.getPrimitivePropertyAsString("Correo"))
            bundle.putString("phone", profileData.getPrimitivePropertyAsString("Telefono3"))
            bundle.putString("sisUsI", profileData.getPrimitivePropertyAsString("SisUsu_Identificador"))
            bundle.putString("sisUsC", profileData.getPrimitivePropertyAsString("SisUsu_Contrasenia"))
            Commons.sendToViewWithBundle(this, EditProfileActivity::class.java, bundle)
        }

        btnChangePassword.setOnClickListener {
            btnChangePassword.isEnabled = false
            val bundle = Bundle()
            bundle.putString("name", profileData.getPrimitivePropertyAsString("Nombre"))
            bundle.putString("lastName", profileData.getPrimitivePropertyAsString("APaterno"))
            bundle.putString("surName", profileData.getPrimitivePropertyAsString("AMaterno"))
            bundle.putString("email", profileData.getPrimitivePropertyAsString("Correo"))
            bundle.putString("phone", profileData.getPrimitivePropertyAsString("Telefono3"))
            bundle.putString("sisUsI", profileData.getPrimitivePropertyAsString("SisUsu_Identificador"))
            bundle.putString("sisUsC", profileData.getPrimitivePropertyAsString("SisUsu_Contrasenia"))
            Commons.sendToViewWithBundle(this, ChangePasswordActivity::class.java, bundle)
        }
        btnForgotPassword.setOnClickListener {
            btnForgotPassword.isEnabled = false
            Commons.sendToView(this, RecoverPasswordActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        btnForgotPassword.isEnabled = true
        btnChangePassword.isEnabled = true
        btnEditAccount.isEnabled = true
        if (Session.updateProfile) {
            if (Session.profileData == null) {
                getProfile()
                Session.updateProfile = false
            } else {
                profileData = Session.profileData!!
                setUserData(profileData)
            }
        }
    }

    private fun getProfile() {
        startLoader()
        val params: MutableMap<String, String> = mutableMapOf("sisUsu_Id" to userID, "tipoOperacion" to "1")
        RestService.baseRequest(this,
            RequestEnum.GETPROFILE, Constans.METHOD_GETPROFILE, params, { response ->
                runOnUiThread {
                    stopLoader()
                    val responseGetProfile = response as ResponseList
                    val profile = responseGetProfile.list as SoapObject
                    profileData = profile
                    Session.profileData = profileData
                    setUserData(profileData)
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

    private fun setUserData(profile: SoapObject) {
        txtvName.text = profile.getPrimitivePropertyAsString("Nombre")
        txtvLastName.text = profile.getPrimitivePropertyAsString("APaterno")
        txtvSurName.text = profile.getPrimitivePropertyAsString("AMaterno")
        txtvEmail.text = profile.getPrimitivePropertyAsString("Correo")
        txtvPhone.text = profile.getPrimitivePropertyAsString("Telefono3")
    }

    private fun setToolbar() {
        setSupportActionBar(navbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_btn)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(code: String, success: Boolean) {
        if (code == getString(R.string.internet)) {
            onBackPressed()
        }
    }


}