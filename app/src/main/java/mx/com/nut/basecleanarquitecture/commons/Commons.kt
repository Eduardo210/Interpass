package mx.com.nut.basecleanarquitecture.commons

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.view.WindowManager
import android.R.id.edit
import android.text.method.TextKeyListener.clear
import android.content.SharedPreferences



class Commons {


    companion object {

        fun savePreference(activity: Activity, key: String, value: Any) {
            val sharedPref = activity.getSharedPreferences("ControlTag",Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                when (value) {
                    is Int -> putInt(key, value)
                    is String -> putString(key, value)

                }
                commit()
            }
        }

        fun saveIntPreference(activity: Activity?, key: String, value: Int) {
            if (activity == null) {
                return
            }
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putInt(key, value)
                commit()
            }
        }


        fun saveStringPreference(activity: Activity?, key: String, value: String?) {
            if (activity == null) {
                return
            }
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return
            with(sharedPref.edit()) {
                putString(key, value)
                commit()
            }
        }


        fun getStringPreference(activity: Activity, key: String): String? {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return null
            val value = sharedPref.getString(key, "")
            return  value
        }

        fun getIntPreference(activity: Activity, key: String): Int? {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return null
            val value = sharedPref.getInt(key, 0)
            return  value
        }


        fun getStringPreferenceFragment(activity: FragmentActivity, key: String): String? {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return null
            val value = sharedPref.getString(key, "")

            return  value
        }

        fun getPreference(activity: Activity, key: String): Any? {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return null
            var value: Any? = sharedPref.getString(key, "")
            if(value == null){
                value = sharedPref.getInt(key, 0)
            }
            return value
        }

        fun getPreferenceFragment(activity: FragmentActivity, key: String):Any? {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE) ?: return null
            var value: Any? = sharedPref.getString(key, "")
            if(value == null){
                value = sharedPref.getInt(key, 0)
            }
            return value
        }

        fun deletePreference(activity: FragmentActivity, key: String) {
            val sharedPref = activity.getSharedPreferences("ControlTag", Context.MODE_PRIVATE)
            sharedPref.edit().remove(key).commit()
        }

        fun addZeroToLeft(number: Int): String{
            return if(number < 10){
                "0" + number.toString()
            }else{
                number.toString()
            }
        }

        fun <T> sendToView(context:Context?, cls: Class<T> ){
            if(context == null) {
                return
            }
            val intent = Intent(context, cls)
            startActivity(context,intent,null)
        }

        fun <T> sendToViewFinish(context:Context?, cls: Class<T>) {
            if(context == null) {
                return
            }
            val intent = Intent(context, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent,null)
        }

        fun <T> sendToViewFinishBundle(context:Context?, cls: Class<T>, bundle: Bundle) {
            if(context == null) {
                return
            }
            val intent = Intent(context, cls)
            intent.putExtras(bundle)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(context, intent,null)
        }

        fun <T> sendToViewWithBundle(context:Context?, cls: Class<T>, bundle: Bundle) {
            if(context == null){
                return
            }
            val intent = Intent(context, cls)
            intent.putExtras(bundle)
            startActivity(context, intent, null)
        }

        fun setStatusBarColor(activity: Activity,idColor: Int){
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, idColor)
        }

    }

}