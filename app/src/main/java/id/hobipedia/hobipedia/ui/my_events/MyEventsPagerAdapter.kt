package id.hobipedia.hobipedia.ui.my_events

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MyEventsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> EventSayaFragment()
            1 -> EventLainFragment()
            else -> EventSayaFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "My Event"
            1 -> "Event"
            else -> null
        }
    }

}