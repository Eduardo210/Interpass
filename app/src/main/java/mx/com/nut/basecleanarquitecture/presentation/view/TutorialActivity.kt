package mx.com.nut.basecleanarquitecture.presentation.view

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_tutorial.*
import mx.com.nut.basecleanarquitecture.R
import mx.com.nut.basecleanarquitecture.commons.Commons
import mx.com.nut.basecleanarquitecture.commons.addIndicator
import mx.com.nut.basecleanarquitecture.commons.selectIndicator
import mx.com.nut.basecleanarquitecture.domain.model.Tutorial
import mx.com.nut.basecleanarquitecture.presentation.adapter.TutorialPagerAdapter
import mx.com.nut.basecleanarquitecture.presentation.base.BaseActivity

class TutorialActivity: BaseActivity(), ViewPager.OnPageChangeListener {
    private var select = 0
    private var arrayTutorial: ArrayList<Tutorial> = ArrayList()

    override fun getLayoutId(): Int {
       return R.layout.activity_tutorial
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        addTutorials()
        addPointsToPager()
        pager.adapter =
            TutorialPagerAdapter(
                supportFragmentManager,
                arrayTutorial
            )
        pager.addOnPageChangeListener(this)
        pager.offscreenPageLimit = 2

        btnNext.setOnClickListener {
            if (arrayTutorial.size >= pager.currentItem + 1) {
                pager.currentItem = pager.currentItem + 1
            }
        }
        btnJump.setOnClickListener {
            btnJump.isEnabled = false
            Commons.saveStringPreference(this, "firstTime", "1")
            Commons.sendToView(this,LoginActivity::class.java)
        }
        btnContinue.setOnClickListener {
            btnJump.isEnabled = false
            Commons.saveStringPreference(this, "firstTime", "1")
            Commons.sendToView(this,LoginActivity::class.java)
        }
    }



    private fun addPointsToPager(){
        val tam = this.arrayTutorial.size-1
        for (i in 0..tam) {
            linearIndicator.addIndicator(i)
        }
        linearIndicator.selectIndicator(0,true)
    }

    private fun addTutorials(){
        arrayTutorial.add(
            Tutorial(
                R.drawable.img_01,
                getString(R.string.title1),
                getString(R.string.description1)
            )
        )
        arrayTutorial.add(
            Tutorial(
                R.drawable.img_02,
                getString(R.string.title2),
                getString(R.string.description2)
            )
        )
        arrayTutorial.add(
            Tutorial(
                R.drawable.img_03,
                getString(R.string.title3),
                getString(R.string.description3)
            )
        )
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
        linearIndicator.selectIndicator(select,false)
        linearIndicator.selectIndicator(p0,true)
        select = p0
        if (select == 2) {
            btnContinue.visibility = View.VISIBLE
            btnJump.visibility = View.GONE
            btnNext.visibility = View.GONE
        } else {
            btnContinue.visibility = View.GONE
            btnJump.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE
        }
    }
}
