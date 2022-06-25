package `in`.kit.college_management_system.singin_signup

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivitySignInSignUpHostBinding
import `in`.kit.college_management_system.singin_signup.fragments.ChooseRoleFragment
import `in`.kit.college_management_system.singin_signup.fragments.FacultyOrHODExtraDetailsFragment
import `in`.kit.college_management_system.singin_signup.fragments.SignInWithGoogleFragment
import `in`.kit.college_management_system.singin_signup.fragments.StudentExtraDetailsFragment
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInSignUpHostBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInSignUpHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MakeStatusBarTransparent().transparent(this)


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment").commit()

    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.fragmentContainer)) {

            is ChooseRoleFragment -> {
                super.onBackPressed()
            }

            is StudentExtraDetailsFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment")
                    .commit()
            }

            is FacultyOrHODExtraDetailsFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment")
                    .commit()
            }

            is SignInWithGoogleFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment")
                    .commit()
            }

        }
    }
}