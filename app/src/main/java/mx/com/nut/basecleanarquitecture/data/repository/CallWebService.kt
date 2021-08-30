package mx.com.nut.basecleanarquitecture.data.repository

import android.util.Log
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Response.ResponseLogin
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

class CallWebService {

    fun callApi(soapObject: SoapObject, method: String): SoapObject? {
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.implicitTypes = true
        envelope.isAddAdornments = false
        envelope.setOutputSoapObject(soapObject)
        val httpTransportSE = HttpTransportSE(Utils.SOAP_URL)
        httpTransportSE.debug = true

        return try {
            val soapMethod = Utils.SOAP_ACTION.plus(method)
            httpTransportSE.call(soapMethod, envelope)
            val soapResponse = envelope.response as SoapObject
            Log.d("request",httpTransportSE.requestDump)
            Log.d("response",httpTransportSE.responseDump)
            soapResponse
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun callApi2(soapObject: SoapObject, method: String): SoapPrimitive? {
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.dotNet = true
        envelope.implicitTypes = true
        envelope.isAddAdornments = false

        envelope.setOutputSoapObject(soapObject)
        val httpTransportSE = HttpTransportSE(Utils.SOAP_URL)
        httpTransportSE.debug = true

        return try {
            val soapMethod = Utils.SOAP_ACTION.plus(method)
            httpTransportSE.call(soapMethod, envelope)
            val soapResponse = envelope.response as SoapPrimitive
            Log.d("request",httpTransportSE.requestDump)
            Log.d("response",httpTransportSE.responseDump)
            soapResponse
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}