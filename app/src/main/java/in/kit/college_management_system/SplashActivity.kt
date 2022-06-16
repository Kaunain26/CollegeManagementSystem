package `in`.kit.college_management_system

import `in`.kit.college_management_system.databinding.ActivitySplashBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.singin_signup.AuthenticationActivity
import `in`.kit.college_management_system.studentSection.activity.StudentHomePage
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import `in`.kit.college_management_system.utils.Preferences
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference
    private lateinit var firebaseHelperClass: FirebaseHelperClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firebaseHelperClass = FirebaseHelperClass()

        MakeStatusBarTransparent().transparent(this)

        // Handler(Looper.getMainLooper()).postDelayed({
        val currentUser = mAuth.currentUser

        if(currentUser!=null){
            try {
                val userRoleObject: JSONObject = Preferences(this).getInstance(this).userRole
                val selectedRole = userRoleObject.getLong("selectedRole")
                mRef = when (selectedRole) {
                    0L -> {
                        firebaseHelperClass.getStudentRef()
                    }
                    else -> {
                        firebaseHelperClass.getFacultyRef()
                    }
                }
                if (selectedRole == 0L) {
                    //student
                    mRef.child(mAuth.uid.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild("sem") &&
                                    snapshot.hasChild("batch") &&
                                    snapshot.hasChild("branch") &&
                                    snapshot.hasChild("address") &&
                                    snapshot.hasChild("gender") &&
                                    snapshot.hasChild("usn")
                                ) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            StudentHomePage::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            AuthenticationActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                } else {
                    //faculty
                    mRef.child(mAuth.uid.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild("branch")) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            FacultyHomePage::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            AuthenticationActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            } catch (e: Exception) {
                //no data found in shared prefs..
                mAuth.signOut()
                startActivity(Intent(this, AuthenticationActivity::class.java))
                finish()
                e.printStackTrace()
            }
        }else{
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }



        //}, 2000)
    }
}