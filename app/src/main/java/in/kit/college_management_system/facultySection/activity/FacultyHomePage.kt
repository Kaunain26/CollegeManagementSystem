package `in`.kit.college_management_system.facultySection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivityFacultyHomePageBinding
import `in`.kit.college_management_system.facultySection.fragments.ClassesFragment
import `in`.kit.college_management_system.facultySection.fragments.FacultyProfileFragment
import `in`.kit.college_management_system.facultySection.fragments.LeavesFragmentFacultySection
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class FacultyHomePage : AppCompatActivity() {

    private lateinit var binding: ActivityFacultyHomePageBinding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private var classesFragment = ClassesFragment()
    private var leavesFragment = LeavesFragmentFacultySection()
    private var profileFragment = FacultyProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacultyHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MakeStatusBarTransparent().transparent(this)

        if (savedInstanceState == null) {
            addFragmentsToFragmentContainer()
        } else {
            // if activity recreates then instead of adding new fragment get the old instances of fragment and assign it to all the fragments
            // this helps to prevent creation of duplicates fragment
            getOldFragInstances()
        }

        binding.facultyBottomNavView.selectedItemId = R.id.page_1
        setUpBottomNavigationView()

    }

    private fun setUpBottomNavigationView() {
        //when app load then show Class fragment and hide all the fragment
        supportFragmentManager.beginTransaction().show(classesFragment).commit()
        supportFragmentManager.beginTransaction().hide(leavesFragment).commit()
        supportFragmentManager.beginTransaction().hide(profileFragment).commit()

        binding.facultyBottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.beginTransaction().show(classesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(leavesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(profileFragment).commit()
                    true
                }
                R.id.page_2 -> {
                    supportFragmentManager.beginTransaction().hide(classesFragment).commit()
                    supportFragmentManager.beginTransaction().show(leavesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(profileFragment).commit()
                    true
                }
                R.id.page_3 -> {
                    supportFragmentManager.beginTransaction().hide(classesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(leavesFragment).commit()
                    supportFragmentManager.beginTransaction().show(profileFragment).commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun addFragmentsToFragmentContainer() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, classesFragment, "classesFragment")
            .add(R.id.fragmentContainer, leavesFragment, "leavesFragment")
            .add(R.id.fragmentContainer, profileFragment, "profileFragment")
            .commit()
        Log.d("ActivitySavedInstanceState", "onCreate: null instance ")
    }

    private fun getOldFragInstances() {
        val oldClassesFragment =
            supportFragmentManager.findFragmentByTag("classesFragment") as ClassesFragment
        val oldLeavesFragment =
            supportFragmentManager.findFragmentByTag("leavesFragment") as LeavesFragmentFacultySection
        val oldProfileFragment =
            supportFragmentManager.findFragmentByTag("profileFragment") as FacultyProfileFragment

        classesFragment = oldClassesFragment
        leavesFragment = oldLeavesFragment
        profileFragment = oldProfileFragment
    }

}