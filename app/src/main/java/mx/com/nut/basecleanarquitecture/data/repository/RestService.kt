package mx.com.nut.basecleanarquitecture.data.repository

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.*
import mx.com.nut.basecleanarquitecture.commons.Utils
import mx.com.nut.basecleanarquitecture.data.entity.Request.StringArraySerializer
import mx.com.nut.basecleanarquitecture.data.entity.Response.*
import mx.com.nut.basecleanarquitecture.domain.model.PrefixCatalog
import mx.com.nut.basecleanarquitecture.domain.model.VehicleCatalog
import mx.com.nut.basecleanarquitecture.presentation.base.Session
import org.ksoap2.serialization.PropertyInfo
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import java.io.IOException
import kotlin.coroutines.CoroutineContext



enum class RequestEnum {
    LOGIN,
    CATALOGV,
    CATALOGP,
    REGISTER,
    ACTIVE_ACCOUNT,
    GETTAG,
    GETMOVES,
    NEWTAG,
    RECOVERPASS,
    DELETE,
    GETPROFILE,
    GETPAYMENTS,
    GETAMOUNTS,
    GENERATECODE,
    VALIDATECODE,
    GETBILLINGDATA,
    SENDBILLINGDATA,
    GETNOTICES
}

class RestService {
    companion object : CoroutineScope {
        var mensajeId: String? = null
        private val contextJob = Job()
        private val dispatcher: CoroutineDispatcher = Dispatchers.IO
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main + contextJob
        private val service = CallWebService()
        private var paramBase: MutableMap<String, String>  = mutableMapOf("usuarioWCF" to "*Acciona*4c","passwordWCF" to "*Acciona*4c${Utils.getDate("ddMMyyyy")}")
        private var paramBaseP: MutableMap<String, String>  = mutableMapOf("usuariosWCF" to "*Acciona*4c","passwordWCF" to "*Acciona*4c${Utils.getDate("ddMMyyyy")}")

        fun baseRequest(context: Context, requestType: RequestEnum, method: String, params: MutableMap<String, String>, successHandler: (Any) -> Unit, failureHandler: (String?) -> Unit, exceptionHandler: (String) -> Unit) {
            if (!isConnected(context)) {
                failureHandler("No hay conexión a internet")
                return
            }
            launch(dispatcher) {
                try {
                    val soapObject = SoapObject(Utils.SOAP_NAMESPACE, method)

                    if (requestType != RequestEnum.CATALOGV && requestType != RequestEnum.CATALOGP) {
                        for ((k, v) in paramBase) {
                            soapObject.addProperty(k,v)
                        }
                    }
                    for ((k, v) in params) {
                        soapObject.addProperty(k,v)
                    }
                    val soapResponse = service.callApi(soapObject, method)
                    var mensajeIdString = "_1_MensajeId"
                    var mensajeString = "_2_Mensaje"
                    when(requestType) {
                        RequestEnum.GETAMOUNTS -> {
                            mensajeIdString = "MensajeId"
                            mensajeString = "Mensaje"
                        }
                        RequestEnum.GENERATECODE -> {
                            mensajeIdString = "_MensajeId"
                            mensajeString = "_Mensaje"
                        }
                        RequestEnum.VALIDATECODE -> {
                            mensajeIdString = "_MensajeId"
                            mensajeString = "_Mensaje"
                        }
                    }

                    mensajeId = soapResponse?.getPrimitivePropertyAsString(mensajeIdString)
                    val mensaje = soapResponse?.getPrimitivePropertyAsString(mensajeString)

                    var response = when(requestType) {
                        RequestEnum.LOGIN -> mapLogin(soapResponse)
                        RequestEnum.CATALOGV -> mapCatalogV(soapResponse)
                        RequestEnum.CATALOGP -> mapCatalogP(soapResponse)
                        RequestEnum.REGISTER -> mapRegister(soapResponse)
                        RequestEnum.ACTIVE_ACCOUNT -> mapRegister(soapResponse)
                        RequestEnum.GETTAG -> mapGetTags(soapResponse)
                        RequestEnum.GETMOVES -> mapGetMoves(soapResponse)
                        RequestEnum.NEWTAG -> mapRegister(soapResponse)
                        RequestEnum.RECOVERPASS -> mapChangePass(soapResponse)
                        RequestEnum.DELETE -> mapGeneric(soapResponse)
                        RequestEnum.GETPROFILE -> mapGetProfile(soapResponse)
                        RequestEnum.GETPAYMENTS -> mapGetPayments(soapResponse)
                        RequestEnum.GETAMOUNTS -> mapGetAmounts(soapResponse)
                        RequestEnum.GENERATECODE -> mapGenerateCode(soapResponse)
                        RequestEnum.VALIDATECODE -> mapValidateCode(soapResponse)
                        RequestEnum.GETBILLINGDATA -> mapGetBillingData(soapResponse)
                        RequestEnum.SENDBILLINGDATA -> mapGeneric(soapResponse)
                        RequestEnum.GETNOTICES -> mapGetNotices(soapResponse)
                    }

                    val mensajeIdInt: Int? = mensajeId?.toInt()
                    mensajeIdInt?.let {
                        when(it) {
                            128, 129 -> {
                                successHandler(response)
                            } 0 -> {
                            failureHandler(mensaje)
                        } else -> {
                            failureHandler(mensaje)
                        }
                        }
                    }
                } catch (exception: IOException) {
                    exceptionHandler(exception.localizedMessage)
                } catch (exception: Throwable) {
                    exceptionHandler(exception.localizedMessage)
                }
            }
        }

        fun baseRequest2(context: Context, method: String, successHandler: (Any) -> Unit, failureHandler: (String?) -> Unit, exceptionHandler: (String) -> Unit) {
            if (!isConnected(context)) {
                failureHandler("No hay conexión a internet")
                return
            }
            launch(dispatcher) {
                try {
                    val soapObject = SoapObject(Utils.SOAP_NAMESPACE, method)

                    for ((k, v) in paramBase) {
                        soapObject.addProperty(k,v)
                    }
                    val soapResponse = service.callApi2(soapObject, method)

                    val response = mapPrivacy(soapResponse)
                    if (response.mensaje != "") {
                        successHandler(response)
                    } else {
                        failureHandler(response.toString())
                    }
                } catch (exception: IOException) {
                    exceptionHandler(exception.localizedMessage)
                } catch (exception: Throwable) {
                    exceptionHandler(exception.localizedMessage)
                }
            }
        }

        fun baseRequest4(context: Context, method: String, params: MutableMap<String, String>, successHandler: (Any) -> Unit, failureHandler: (String?) -> Unit, exceptionHandler: (String) -> Unit) {
            if (!isConnected(context)) {
                failureHandler("No hay conexión a internet")
                return
            }
            launch(dispatcher) {
                try {
                    val soapObject = SoapObject(Utils.SOAP_NAMESPACE, method)

                    for ((k, v) in paramBase) {
                        soapObject.addProperty(k,v)
                    }
                    for ((k, v) in params) {
                        soapObject.addProperty(k,v)
                    }
                    val soapResponse = service.callApi2(soapObject, method)

                    val response = mapSession(soapResponse)
                    if (response.code != "") {
                        successHandler(response)
                    } else {
                        failureHandler(response.toString())
                    }
                } catch (exception: IOException) {
                    exceptionHandler(exception.localizedMessage)
                } catch (exception: Throwable) {
                    exceptionHandler(exception.localizedMessage)
                }
            }
        }

        fun baseRequestP(context: Context, method: String, params: MutableMap<String, String>, successHandler: (Any) -> Unit, failureHandler: (String?) -> Unit, exceptionHandler: (String) -> Unit) {
            if (!isConnected(context)) {
                failureHandler("No hay conexión a internet")
                return
            }
            launch(dispatcher) {
                try {
                    val soapObject = SoapObject(Utils.SOAP_NAMESPACE, method)

                    for ((k, v) in paramBaseP) {
                        soapObject.addProperty(k, v)
                    }
                    for ((k, v) in params) {
                        soapObject.addProperty(k, v)
                    }
                    val soapResponse = service.callApi(soapObject, method)

                    val response = mapPurchase(soapResponse)

                    val mensajeIdInt: Int? = response.mensajeID?.toInt()
                    mensajeIdInt?.let {
                        when(it) {
                            128, 129 -> {
                                successHandler(response)
                            } 0 -> {
                                failureHandler(response.mensaje)
                            } else -> {
                                failureHandler(response.mensaje)
                            }
                        }
                    }
                } catch (exception: IOException) {
                    exceptionHandler(exception.localizedMessage)
                } catch (exception: Throwable) {
                    exceptionHandler(exception.localizedMessage)
                }
            }
        }

        fun baseRequestList(context: Context, requestType: RequestEnum, method: String, params: MutableMap<String, String>, paramsList: ArrayList<String>, successHandler: (String?) -> Unit, failureHandler: (String?) -> Unit, exceptionHandler: (String) -> Unit) {
            if (!isConnected(context)) {
                failureHandler("No hay conexión a internet")
                return
            }
            launch(dispatcher) {
                try {
                    val soapObject = SoapObject(Utils.SOAP_NAMESPACE, method)
                    if (requestType != RequestEnum.CATALOGV && requestType != RequestEnum.CATALOGP) {
                        for ((k, v) in paramBase) {
                            soapObject.addProperty(k,v)
                        }
                    }
                    for ((k, v) in params) {
                        soapObject.addProperty(k,v)
                    }
                    val propertyInfo = PropertyInfo()
                    propertyInfo.setName("ListaTransacciones")
                    val list = StringArraySerializer()
                    for (param in paramsList) {
                        list.add(param)
                    }
                    propertyInfo.value = list
                    propertyInfo.type = StringArraySerializer::class.java
                    soapObject.addProperty(propertyInfo)


                    val soapResponse = service.callApi(soapObject, method)


                    var mensajeIdString = "MensajeId"
                    var mensajeString = "Mensaje"

                    mensajeId = soapResponse?.getPrimitivePropertyAsString(mensajeIdString)
                    val mensaje = soapResponse?.getPrimitivePropertyAsString(mensajeString)

                    var response = mapBilling(soapResponse)

                    val mensajeIdInt: Int? = mensajeId?.toInt()
                    mensajeIdInt?.let {
                        when(it) {
                            128, 129 -> {
                                successHandler(mensaje)
                            } 0 -> {
                                failureHandler(mensaje)
                            } else -> {
                                failureHandler(mensaje)
                            }
                        }
                    }
                } catch (exception: IOException) {
                    exceptionHandler(exception.localizedMessage)
                } catch (exception: Throwable) {
                    exceptionHandler(exception.localizedMessage)
                }
            }
        }


        private fun isConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun mapPrivacy(soapResponse: SoapPrimitive?): ResponsePrivacy {
            val mensaje = soapResponse?.value.toString()
            return ResponsePrivacy(mensaje)
        }

        fun mapSession(soapResponse: SoapPrimitive?): ResponseSession {
            val code = soapResponse?.value.toString()
            return ResponseSession(code)
        }

        fun mapGeneric(soapResponse: SoapObject?): ResponseGeneric {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val usuarioId = soapResponse?.getPrimitivePropertyAsString("_3_UsuarioId")
            return ResponseGeneric(mensajeId,mensaje,usuarioId)
        }

        fun mapBilling(soapResponse: SoapObject?): ResponseGeneric {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("Mensaje")
            return ResponseGeneric(mensajeId, mensaje, "")
        }

        fun mapLogin(soapResponse: SoapObject?): ResponseLogin {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val usuarioId = soapResponse?.getPrimitivePropertyAsString("_3_UsuarioId")
            val codigo = soapResponse?.getPrimitivePropertyAsString("_4_Codigo")
            return ResponseLogin(mensajeId,mensaje,usuarioId,codigo)
        }

        fun mapGetProfile(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val userList = list?.getProperty("UsuarioDatos")
            return ResponseList(mensajeId, mensaje, userList)
        }

        fun mapGetNotices(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val userList = list?.getProperty("ListaNoticias")
            return ResponseList(mensajeId, mensaje, userList)
        }

        fun mapPurchase(soapResponse: SoapObject?): ResponsePurchase {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("Folio")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("Mensaje")
            val mensajeID = soapResponse?.getPrimitivePropertyAsString("MensajeId")
            return ResponsePurchase(mensajeId, mensaje, mensajeID)
        }

        fun mapGetAmounts(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("Mensaje")
            val list = soapResponse?.getProperty("Importes")
            return ResponseList(mensajeId, mensaje, list)
        }

        fun mapGetBillingData(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista")
            if (mensajeId == "0") {
                this.mensajeId = "129"
            }
            return ResponseList(mensajeId, mensaje, list)
        }

        fun mapGetPayments(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val userList = list?.getProperty("litaTarjetaCredito")
            return ResponseList(mensajeId, mensaje, userList)
        }

        fun mapGenerateCode(soapResponse: SoapObject?): ResponseList {
            var mensajeId = soapResponse?.getPrimitivePropertyAsString("_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_Mensaje")
            val codesList = soapResponse?.getProperty("CodigoQR") as SoapObject?
            if (codesList != null) {
                val codes = codesList?.getPropertyAsString(codesList.propertyCount - 1)
                return ResponseList(mensajeId, mensaje, codes)
            }
            if (mensajeId == "0") {
                this.mensajeId = "129"
            }
            return ResponseList(mensajeId, mensaje, "")

        }

        fun mapValidateCode(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_Mensaje")
            val codesList = soapResponse?.getProperty("CodigoQR") as SoapObject?
            return ResponseList(mensajeId, mensaje, codesList)
        }

        fun mapChangePass(soapResponse: SoapObject?): ResponseLogin {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val usuarioId = soapResponse?.getPrimitivePropertyAsString("_3_UsuarioId")
            val codigo = soapResponse?.getPrimitivePropertyAsString("_4_TipoUsuario")
            return ResponseLogin(mensajeId,mensaje,usuarioId,codigo)
        }

        fun mapCatalogV(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val vehicleList = list?.getProperty("ListaVehiculos")
            val responseCatalog = ResponseList(mensajeId, mensaje, vehicleList)
            val vehicles = responseCatalog.list as SoapObject
            var count = 0
            var arrayVehicleCatalog: ArrayList<VehicleCatalog> = ArrayList()
            while (count < vehicles.propertyCount) {
                val vehicle = vehicles.getProperty(count) as SoapObject
                arrayVehicleCatalog.add(
                    VehicleCatalog(
                        vehicle.getPrimitivePropertyAsString("Clase_Descripcion"),
                        vehicle.getPrimitivePropertyAsString("Clase_Id")
                    )
                )
                count += 1
            }
            Session.setValue(arrayVehicleCatalog)
            return ResponseList(mensajeId, mensaje, vehicleList)
        }

        fun mapCatalogP(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val prefixList = list?.getProperty("ListaPrefijo")

            val responseCatalog = ResponseList(mensajeId, mensaje, prefixList)
            val prefix = responseCatalog.list as SoapObject
            var count = 0
            var arrayPrefixCatalog: ArrayList<PrefixCatalog> = ArrayList()
            while (count < prefix.propertyCount) {
                val prefix = prefix.getProperty(count) as SoapObject
                arrayPrefixCatalog.add(
                    PrefixCatalog(
                        prefix.getPrimitivePropertyAsString("TP_Id"),
                        prefix.getPrimitivePropertyAsString("TP_Longitud"),
                        prefix.getPrimitivePropertyAsString("TP_Prefijo_Nombre"),
                        prefix.getPrimitivePropertyAsString("TP_Prefijo_Texto")
                    )
                )
                count += 1
            }
            Session.setValuePrefix(arrayPrefixCatalog)
            return ResponseList(mensajeId, mensaje, prefixList)
        }

        fun mapRegister(soapResponse: SoapObject?): ResponseRegister {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val usuarioId = soapResponse?.getPrimitivePropertyAsString("_3_UsuarioId")
            val TipoUsuario = soapResponse?.getPrimitivePropertyAsString("_4_TipoUsuario")
            return ResponseRegister(mensajeId, mensaje, usuarioId, TipoUsuario)
        }

        fun mapGetTags(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            return ResponseList(mensajeId, mensaje, list)
        }

        fun mapGetMoves(soapResponse: SoapObject?): ResponseList {
            val mensajeId = soapResponse?.getPrimitivePropertyAsString("_1_MensajeId")
            val mensaje = soapResponse?.getPrimitivePropertyAsString("_2_Mensaje")
            val list = soapResponse?.getProperty("_3_Lista") as SoapObject?
            val movesList = list?.getProperty("ListaTransacciones")
            return ResponseList(mensajeId, mensaje, movesList)
        }



    }
}
