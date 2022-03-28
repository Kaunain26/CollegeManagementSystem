package `in`.kit.college_management_system.singin_signup

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivitySignInSignUpHostBinding
import `in`.kit.college_management_system.singin_signup.fragments.ChooseRoleFragment
import `in`.kit.college_management_system.singin_signup.fragments.ChooseSignInOrSignUpFrag
import `in`.kit.college_management_system.singin_signup.fragments.SignInFragment
import `in`.kit.college_management_system.singin_signup.fragments.SignUpFragment
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SignInSignUpHostActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInSignUpHostBinding


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

            is SignUpFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment")
                    .commit()
            }

            is SignInFragment -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "signUpFragment")
                    .commit()
            }

            is ChooseSignInOrSignUpFrag -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ChooseRoleFragment(), "chooseRoleFragment")
                    .commit()
            }

        }
    }
}