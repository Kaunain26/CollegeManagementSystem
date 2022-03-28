package `in`.kit.college_management_system

import `in`.kit.college_management_system.databinding.ActivitySplashBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.hodSection.HODHomePage
import `in`.kit.college_management_system.principalSection.PrincipalHomePage
import `in`.kit.college_management_system.singin_signup.SignInSignUpHostActivity
import `in`.kit.college_management_system.studentSection.StudentHomePage
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import `in`.kit.college_management_system.utils.Preferences
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        MakeStatusBarTransparent().transparent(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                try {
                    val userRoleObject: JSONObject = Preferences(this).getInstance(this).userRole
                    when (userRoleObject.getLong("selectedRole")) {
                        0L -> {
                            startActivity(Intent(this, StudentHomePage::class.java))
                            finish()
                        }
                        1L -> {
                            startActivity(Intent(this, PrincipalHomePage::class.java))
                            finish()
                        }
                        2L -> {
                            startActivity(Intent(this, HODHomePage::class.java))
                            finish()
                        }
                        3L -> {
                            startActivity(Intent(this, FacultyHomePage::class.java))
                            finish()
                        }

                    }
                } catch (e: Exception) {
                    //no data found in shared prefs..
                    mAuth.signOut()
                    startActivity(Intent(this, SignInSignUpHostActivity::class.java))
                    finish()
                    e.printStackTrace()
                }

            } else {
                startActivity(Intent(this, SignInSignUpHostActivity::class.java))
                finish()
            }

        }, 2000)
    }
}