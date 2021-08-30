package mx.com.nut.basecleanarquitecture.presentation.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.Constans
import mx.com.nut.basecleanarquitecture.data.repository.RequestEnum
import mx.com.nut.basecleanarquitecture.data.repository.RestService
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity:  BaseActivity() {
    var timer: TimerTask? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Session.reset()

        getCatalogVehicle()
        getCatalogPrefix()
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        Glide.with(this)
            .load(R.drawable.fondo_android)
            .into(imageView)


        lottie_Splash.setAnimation("splash.json")
        lottie_Splash.playAnimation()
        lottie_Splash.loop(true)
        val isfirstTime = Commons.getPreference(this,"firstTime")
        val mensaje = Commons.getPreference(this,"mensaje")
        timer = Timer("Splash", false).schedule(5100) {
            if (isfirstTime == "1") {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else if (isfirstTime == "2") {
                val intent = Intent(this@SplashActivity, TagsActivity::class.java)
                val bundle = Bundle()
                bundle.putBoolean("canScroll", false)
                bundle.putBoolean("validateAccount", true)
                bundle.putString("mensaje", mensaje.toString())
                intent.putExtras(bundle)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, TutorialActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            finish()
        }
    }

    fun getCatalogPrefix() {
        var params: MutableMap<String, String> = mutableMapOf(
            "catalogo" to "PREFIJO",
            "valorCatalogo1" to "3",
            "valorCatalogo2" to "",
            "valorCatalogo3" to ""
        )

        RestService.baseRequest(this,
            RequestEnum.CATALOGP, Constans.METHOD_GET_CATALOG, params, { response ->
                stopLoader()
                runOnUiThread {

                }
            }, { error ->
                stopLoader()
                runOnUiThread {
                    print("error")
                }
            }, { exception ->
                stopLoader()
                runOnUiThread {
                    print("error")
                }
            })
    }

    fun getCatalogVehicle() {
        var params: MutableMap<String, String> = mutableMapOf("catalogo" to "VEHICULO", "valorCatalogo1" to "", "valorCatalogo2" to "", "valorCatalogo3" to "")
        RestService.baseRequest(this,
            RequestEnum.CATALOGV, Constans.METHOD_GET_CATALOG, params, { response ->
                runOnUiThread {

                }
            }, { error ->
                runOnUiThread {
                    print("error")
                }
            }, { exception ->
                runOnUiThread {
                    print("error")
                }
            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        timer = null
    }

}