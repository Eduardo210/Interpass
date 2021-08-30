package mx.com.nut.basecleanarquitecture.presentation.base


import mx.com.nut.basecleanarquitecture.data.entity.BillingModel
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.domain.model.PrefixCatalog
import mx.com.nut.basecleanarquitecture.domain.model.VehicleCatalog
import org.ksoap2.serialization.SoapObject

class Session {

    companion object SharedInstance {

        var arrayVehicleCatalog: ArrayList<VehicleCatalog> = ArrayList()
        var updateTagMoves = false
        var updateProfile = true
        var updatePayments = true
        var launchHandler = false
        var updateBillingData = true
        var paymentsModel: ArrayList<PaymentsModel> = ArrayList()
        var billingModel: ArrayList<BillingModel> = ArrayList()
        var profileData: SoapObject? = null

        fun getValue(): List<VehicleCatalog> {
            return arrayVehicleCatalog
        }

        fun setValue(list: ArrayList<VehicleCatalog>) {
            arrayVehicleCatalog = list
        }

        var arrayCatalogPrefix: ArrayList<PrefixCatalog> = ArrayList()

        fun  getValuePrefix(): List<PrefixCatalog> {
            return arrayCatalogPrefix
        }

        fun setValuePrefix(list: ArrayList<PrefixCatalog>) {
            arrayCatalogPrefix = list
        }

        fun reset() {
            updateTagMoves = false
            updateProfile = true
            updatePayments = true
            updateBillingData = true
            paymentsModel = ArrayList()
            billingModel = ArrayList()
            profileData = null
        }
    }
}