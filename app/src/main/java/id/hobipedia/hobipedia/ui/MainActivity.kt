package id.hobipedia.hobipedia.ui
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import id.hobipedia.hobipedia.R
import id.hobipedia.hobipedia.ui.home.HomeFragment
import id.hobipedia.hobipedia.ui.my_events.MyEventsFragment
import id.hobipedia.hobipedia.ui.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateFragment(HomeFragment())
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun updateFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, fragment)
                .commit()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportActionBar?.title = "HobiPedia"
                updateFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_my_events -> {
                supportActionBar?.title = "My Events"
                updateFragment(MyEventsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                supportActionBar?.title = "Profile"
                updateFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
}
