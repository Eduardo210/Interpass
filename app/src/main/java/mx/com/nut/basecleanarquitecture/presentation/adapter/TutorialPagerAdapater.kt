package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import mx.com.nut.basecleanarquitecture.domain.model.Tutorial
import mx.com.nut.basecleanarquitecture.presentation.fragment.TutorialFragment

class TutorialPagerAdapter(fragmentManager: FragmentManager, val arrayTutorial: ArrayList<Tutorial>): FragmentPagerAdapter(fragmentManager) {
    // 2
    override fun getItem(position: Int): Fragment {
        return TutorialFragment.newInstance(
            arrayTutorial[position].image!!,
            arrayTutorial[position].title,
            arrayTutorial[position].descripcion
        )
    }

    // 3
    override fun getCount(): Int {
        return arrayTutorial.size
    }
}