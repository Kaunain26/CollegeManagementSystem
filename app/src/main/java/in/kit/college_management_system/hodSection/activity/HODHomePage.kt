package `in`.kit.college_management_system.hodSection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivityHodhomePageBinding
import `in`.kit.college_management_system.hodSection.fragments.HODDashBoardFragment
import `in`.kit.college_management_system.hodSection.fragments.HODProfileFragment
import `in`.kit.college_management_system.hodSection.fragments.NotificationsFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class HODHomePage : AppCompatActivity() {

    private lateinit var binding: ActivityHodhomePageBinding
    private var dashBoardFragment = HODDashBoardFragment()
    private var notificationsFragment = NotificationsFragment()
    private var hodProfileFragment = HODProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHodhomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            addFragmentsToFragmentContainer()
        } else {
            // if activity recreates then instead of adding new fragment get the old instances of fragment and assign it to all the fragments
            // this helps to prevent creation of duplicates fragment
            getOldFragInstances()
        }

        binding.hodBottomView.selectedItemId = R.id.page_1
        setUpBottomNavigationView()
    }

    private fun setUpBottomNavigationView() {
        //when app load then show Class fragment and hide all the fragment
        supportFragmentManager.beginTransaction().show(dashBoardFragment).commit()
        supportFragmentManager.beginTransaction().hide(notificationsFragment).commit()
        supportFragmentManager.beginTransaction().hide(hodProfileFragment).commit()

        binding.hodBottomView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.beginTransaction().show(dashBoardFragment).commit()
                    supportFragmentManager.beginTransaction().hide(notificationsFragment).commit()
                    supportFragmentManager.beginTransaction().hide(hodProfileFragment).commit()
                    true
                }
                R.id.page_2 -> {
                    supportFragmentManager.beginTransaction().hide(dashBoardFragment).commit()
                    supportFragmentManager.beginTransaction().show(notificationsFragment).commit()
                    supportFragmentManager.beginTransaction().hide(hodProfileFragment).commit()
                    true
                }
                R.id.page_3 -> {
                    supportFragmentManager.beginTransaction().hide(dashBoardFragment).commit()
                    supportFragmentManager.beginTransaction().hide(notificationsFragment).commit()
                    supportFragmentManager.beginTransaction().show(hodProfileFragment).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun addFragmentsToFragmentContainer() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, dashBoardFragment, "dashBoardFragment")
            .add(R.id.fragmentContainer, notificationsFragment, "notificationsFragment")
            .add(R.id.fragmentContainer, hodProfileFragment, "hodProfileFragment")
            .commit()
        Log.d("ActivitySavedInstanceState", "onCreate: null instance ")
    }

    private fun getOldFragInstances() {
        val oldDashBoardFragment =
            supportFragmentManager.findFragmentByTag("dashBoardFragment") as HODDashBoardFragment
        val oldNotificationsFragment =
            supportFragmentManager.findFragmentByTag("notificationsFragment") as NotificationsFragment
        val oldHODProfileFragment =
            supportFragmentManager.findFragmentByTag("hodProfileFragment") as HODProfileFragment

        dashBoardFragment = oldDashBoardFragment
        notificationsFragment = oldNotificationsFragment
        hodProfileFragment = oldHODProfileFragment

    }

}