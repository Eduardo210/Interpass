package mx.com.nut.basecleanarquitecture.presentation.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_new_tag.*
import java.io.ByteArrayOutputStream

class NewTagFragment: Fragment() {
    var listener: ClickNew? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(mx.com.nut.basecleanarquitecture.R.layout.fragment_new_tag,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNewTag.setOnClickListener {
            btnNewTag.isEnabled = false
            listener?.onClickNewPressed(arguments?.getString("tagNumber")?: "")
        }
    }


    override fun onResume() {
        super.onResume()
        btnNewTag.isEnabled = true
    }

    companion object {
        fun newInstance(statusTag: String, tagImage: Bitmap, nickName: String, balance: String, tagNumber: String, context: ClickNew): NewTagFragment {
            val newTagFragment = NewTagFragment()
            val bStream = ByteArrayOutputStream()
            tagImage.compress(Bitmap.CompressFormat.PNG, 100, bStream)
            val byteArray = bStream.toByteArray()
            val bundle = Bundle()
            bundle.putByteArray("tagImage", byteArray)
            bundle.putString("nickName", nickName)
            bundle.putString("balance", balance)
            bundle.putString("tagNumber", tagNumber)
            newTagFragment.arguments = bundle
            newTagFragment.listener = context
            return  newTagFragment
        }
    }

    interface ClickNew {
        fun onClickNewPressed(tagID: String)
    }

}