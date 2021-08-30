package mx.com.nut.basecleanarquitecture.commons

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {

        //val SOAP_URL = "http://www.mep.com.mx/AIDE_WCFMovil/Service.svc"
        val SOAP_URL = "http://telepeajeinterpass.mx/WCFMovil/Service.svc"
        val SOAP_NAMESPACE = "http://tempuri.org/"
        val SOAP_ACTION = "http://tempuri.org/IServicio/"

        fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }


        fun getDate(format: String): String{
            val date = Calendar.getInstance()
            val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
            return dateFormatter.format(date.time)
        }


    }
}