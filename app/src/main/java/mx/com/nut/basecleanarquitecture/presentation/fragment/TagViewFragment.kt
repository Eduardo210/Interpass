package mx.com.nut.basecleanarquitecture.presentation.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tag_view.*
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import mx.com.nut.basecleanarquitecture.R


class TagViewFragment: Fragment() {
    var listener: ClickEdit? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mx.com.nut.basecleanarquitecture.R.layout.fragment_tag_view,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtvNickName.text = arguments?.getString("nickName")
        val byteArray = arguments?.getByteArray("tagImage")
        if (byteArray != null) {
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            tagImage.setImageBitmap(bmp)
        }
        txtvBalance.text = "$".plus(arguments?.getString("balance"))
        val statusString = arguments?.getString("status")
        txtvStatus.text = statusString
        if (statusString == "Activo") {
            txtvStatus.setTextColor(resources.getColor(R.color.green))
        }
        var tagString = arguments?.getString("tagNumber")
        var tagFormatString = tagString!!.substring(0, tagString!!.length - 1).plus(" ").plus(tagString!!.substring(tagString!!.length - 1, tagString!!.length))
        txtvTagNumber.text = tagString
        var tagTypeString = arguments?.getString("tagType")
        if (tagTypeString == "POS-PAGO") {
            txtvBalance.visibility = View.GONE
            guideline.setGuidelinePercent(0.33F)
            txtvStatus.textSize = 16F
            txtvTagType.textSize = 16F
            val params: ConstraintLayout.LayoutParams = tagImage.layoutParams as ConstraintLayout.LayoutParams
            params.setMargins(resources.getDimensionPixelSize(R.dimen.dp15), resources.getDimensionPixelSize(R.dimen.dp7),
                resources.getDimensionPixelSize(R.dimen.dp15), resources.getDimensionPixelSize(R.dimen.dp0))
            val paramstxt: ConstraintLayout.LayoutParams = txtvTagNumber.layoutParams as ConstraintLayout.LayoutParams
            paramstxt.setMargins(resources.getDimensionPixelSize(R.dimen.dp0), resources.getDimensionPixelSize(R.dimen.dp5),
                resources.getDimensionPixelSize(R.dimen.dp0), resources.getDimensionPixelSize(R.dimen.dp0))

        }
        txtvTagType.text = arguments?.getString("tagType")

        var esPredeterminado = arguments?.getString("esPredeterminado")
        if (esPredeterminado == "true") {
            cardPrede.visibility = View.VISIBLE
        }
        btnEditTag.setOnClickListener {
            btnEditTag.isEnabled = false
            listener?.onClickEditPressed(arguments?.getString("tagNumber")?: "")
        }
    }


    override fun onResume() {
        super.onResume()
        btnEditTag.isEnabled = true
    }

    companion object {
        fun newInstance(statusTag: String, tagImage: Bitmap, nickName: String, balance: String, tagNumber: String, tagType: String, esPredeterminado : String,  context: ClickEdit): TagViewFragment {
            val tagViewFragment = TagViewFragment()
            val bStream = ByteArrayOutputStream()
            tagImage.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            val byteArray = bStream.toByteArray()
            val bundle = Bundle()
            bundle.putString("status", statusTag)
            bundle.putByteArray("tagImage", byteArray)
            bundle.putString("nickName", nickName)
            bundle.putString("balance", balance)
            bundle.putString("tagNumber", tagNumber)
            bundle.putString("tagType", tagType)
            bundle.putString("esPredeterminado", esPredeterminado)
            tagViewFragment.arguments = bundle
            tagViewFragment.listener = context
            return tagViewFragment
        }
    }

    interface ClickEdit {
        fun onClickEditPressed(tagID: String)
    }

}