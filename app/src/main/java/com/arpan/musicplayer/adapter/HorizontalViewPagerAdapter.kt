package com.arpan.musicplayer.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class HorizontalViewPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<Fragment>): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return if (fragments[position].arguments!!.getString("FRAGMENT_TITLE") != null) {
            fragments[position].arguments!!.getString("FRAGMENT_TITLE")
        } else {
            null
        }
    }
}