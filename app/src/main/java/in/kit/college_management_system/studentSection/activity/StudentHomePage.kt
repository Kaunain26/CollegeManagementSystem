package `in`.kit.college_management_system.studentSection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivityStudentHomePageBinding
import `in`.kit.college_management_system.facultySection.fragments.LeavesFragment
import `in`.kit.college_management_system.singin_signup.AuthenticationActivity
import `in`.kit.college_management_system.studentSection.fragments.StudentAttendancesFragment
import `in`.kit.college_management_system.studentSection.fragments.StudentLeavesFragment
import `in`.kit.college_management_system.studentSection.fragments.StudentProfileFragment
import `in`.kit.college_management_system.utils.AlertDialogHelperClass
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import `in`.kit.college_management_system.utils.Preferences
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class StudentHomePage : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHomePageBinding
    private var studentAttendancesFragment = StudentAttendancesFragment()
    private var leavesFragment = StudentLeavesFragment()
    private var profileFragment = StudentProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MakeStatusBarTransparent().transparent(this)

        if (savedInstanceState == null) {
            addFragmentsToFragmentContainer()
        } else {
            // if activity recreates then instead of adding new fragment get the old instances of fragment and assign it to all the fragments
            // this helps to prevent creation of duplicates fragment
            getOldFragInstances()
        }

        binding.studentBottomNavView.selectedItemId = R.id.page_1
        setUpBottomNavigationView()

    }

    private fun setUpBottomNavigationView() {
        //when app load then show Class fragment and hide all the fragment
        supportFragmentManager.beginTransaction().show(studentAttendancesFragment).commit()
        supportFragmentManager.beginTransaction().hide(leavesFragment).commit()
        supportFragmentManager.beginTransaction().hide(profileFragment).commit()

        binding.studentBottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 -> {
                    supportFragmentManager.beginTransaction().show(studentAttendancesFragment)
                        .commit()
                    supportFragmentManager.beginTransaction().hide(leavesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(profileFragment).commit()
                    true
                }
                R.id.page_2 -> {
                    supportFragmentManager.beginTransaction().hide(studentAttendancesFragment)
                        .commit()
                    supportFragmentManager.beginTransaction().show(leavesFragment).commit()
                    supportFragmentManager.beginTransaction().hide(profileFragment).commit()
                    true
                }
                R.id.page_3 -> {
                    supportFragmentManager.beginTransaction().hide(studentAttendancesFragment)
                        .commit()
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
            .add(R.id.fragmentContainer, studentAttendancesFragment, "studentAttendancesFragment")
            .add(R.id.fragmentContainer, leavesFragment, "leavesFragment")
            .add(R.id.fragmentContainer, profileFragment, "profileFragment")
            .commit()
        Log.d("ActivitySavedInstanceState", "onCreate: null instance ")
    }

    private fun getOldFragInstances() {
        val oldStudentAttendancesFragment =
            supportFragmentManager.findFragmentByTag("studentAttendancesFragment") as StudentAttendancesFragment
        val oldLeavesFragment =
            supportFragmentManager.findFragmentByTag("leavesFragment") as StudentLeavesFragment
        val oldProfileFragment =
            supportFragmentManager.findFragmentByTag("profileFragment") as StudentProfileFragment

        studentAttendancesFragment = oldStudentAttendancesFragment
        leavesFragment = oldLeavesFragment
        profileFragment = oldProfileFragment
    }


}