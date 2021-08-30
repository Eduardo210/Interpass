package mx.com.nut.basecleanarquitecture.presentation.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tutorial.*
import mx.com.nut.basecleanarquitecture.R

class TutorialFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_tutorial,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDescription.text = arguments?.getString("descripcion")
        imageView.setImageResource(arguments?.getInt("image")!!)
        tvTitle.text = arguments?.getString("titulo")
    }

    companion object {
        fun newInstance(image: Int, titulo: String, descripcion: String): TutorialFragment {
            val tutorialFragment =
                TutorialFragment()
            val bundle = Bundle()
            bundle.putInt("image",image)
            bundle.putString("titulo",titulo)
            bundle.putString("descripcion",descripcion)
            tutorialFragment.arguments = bundle
            return  tutorialFragment
        }
    }

}