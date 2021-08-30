package mx.com.nut.basecleanarquitecture.presentation.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import mx.com.nut.basecleanarquitecture.domain.model.Tag
import mx.com.nut.basecleanarquitecture.presentation.fragment.NewTagFragment
import mx.com.nut.basecleanarquitecture.presentation.fragment.TagViewFragment

class TagPagerAdapter(fragmentManager: FragmentManager, val arrayTags: ArrayList<Tag>, val context: TagViewFragment.ClickEdit, val newTagContext: NewTagFragment.ClickNew): FragmentStatePagerAdapter(fragmentManager) {
    // 2
    override fun getItem(position: Int): Fragment {
        if (position + 1 == arrayTags.size) {
            return NewTagFragment.newInstance(
                arrayTags[position].statusTag,
                arrayTags[position].tagImage,
                arrayTags[position].economicNum,
                arrayTags[position].balance,
                arrayTags[position].tagNumber,
                newTagContext
            )
        } else {
            return TagViewFragment.newInstance(
                arrayTags[position].statusTag,
                arrayTags[position].tagImage,
                arrayTags[position].economicNum,
                arrayTags[position].balance,
                arrayTags[position].tagNumber,
                arrayTags[position].tagType,
                arrayTags[position].esPrederterminado,
                context
            )
        }

    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    // 3
    override fun getCount(): Int {
        return arrayTags.size
    }
}