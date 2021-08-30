package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import mx.com.nut.basecleanarquitecture.data.entity.PaymentsModel
import mx.com.nut.basecleanarquitecture.presentation.fragment.PaymentFragment

class PaymentPagerAdapter(fragmentManager: FragmentManager, val items: ArrayList<PaymentsModel>): FragmentStatePagerAdapter(fragmentManager) {
    // 2
    override fun getItem(position: Int): Fragment {
        return PaymentFragment.newInstance(items[position])
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    // 3
    override fun getCount(): Int {
        return items.size
    }
}