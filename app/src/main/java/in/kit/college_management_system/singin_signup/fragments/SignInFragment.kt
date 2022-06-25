package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentSignInBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.hodSection.activity.HODHomePage
import `in`.kit.college_management_system.principalSection.PrincipalHomePage
import `in`.kit.college_management_system.studentSection.activity.StudentHomePage
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.Preferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import org.json.JSONObject


class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var requestGoolgeSign: ActivityResultLauncher<Intent>
    private lateinit var sharedPrefs: Preferences

    //private lateinit var mRef: DatabaseReference
    private var selectedRole = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding!!.root

        selectedRole = arguments?.getLong("role")!!
        basedOnRoleManageViews()

        firebaseHelperClass = FirebaseHelperClass()
        mAuth = FirebaseAuth.getInstance()
        sharedPrefs = Preferences(activity as Context).getInstance(activity as Context)

        //mRef = firebaseHelperClass.getFacultyRef()

        binding?.arrowBack?.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        validateFieldsBeforeSignIn()

        openSignInFrag()

        activityResult()

        // sign up with google
        signUpWithGoogle()

        return view
    }

    private fun basedOnRoleManageViews() {
        when (selectedRole) {
            0L -> {
                binding?.chooseRoleTV!!.text = "as Student"
                binding?.llSignUpWithGoogle!!.visibility = View.GONE
            }
            1L -> {
                binding?.chooseRoleTV!!.text = "as Principal"
                binding?.llSignUpWithGoogle!!.visibility = View.VISIBLE
            }
            2L -> {
                binding?.chooseRoleTV!!.text = "as HOD"
                binding?.llSignUpWithGoogle!!.visibility = View.GONE
            }
            3L -> {
                binding?.chooseRoleTV!!.text = "as Faculty"
                binding?.llSignUpWithGoogle!!.visibility = View.GONE
            }
        }
    }

    private fun openSignInFrag() {
        val bundle = Bundle()
        bundle.putLong("role", selectedRole)
        val signUpFragment = SignUpFragment()
        signUpFragment.arguments = bundle
        binding?.signUpTVBtn?.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, signUpFragment).commit()
        }
    }

    private fun signUpWithGoogle() {
        binding?.googleSignUpBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE
            binding?.signInBtn?.visibility = View.GONE
            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(activity as Context, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            requestGoolgeSign.launch(signInIntent)
        }
    }

    private fun activityResult() {
        requestGoolgeSign =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it!!.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("TAG", "Google sign in failed", e)
                    Toast.makeText(
                        activity as Context,
                        "Sign In Unsuccessful!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    binding?.progressBar?.visibility = View.GONE
                    binding?.signInBtn?.visibility = View.VISIBLE
                }
            }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                // val user = mAuth.currentUser
                Toast.makeText(
                    activity as Context,
                    "Signed in successful!!",
                    Toast.LENGTH_SHORT
                ).show()

                binding?.progressBar?.visibility = View.GONE
                binding?.signInBtn?.visibility = View.VISIBLE

                //save role of user
                val jsonObject = JSONObject()
                jsonObject.put("selectedRole", selectedRole)
                sharedPrefs.saveUserRole(jsonObject)

                //open a principal home page activity........
                startActivity(Intent(activity as Context, PrincipalHomePage::class.java))
                (activity as AppCompatActivity).finishAffinity()

                /* val FacultyOrHODDetails = HashMap<String, Any>()
                 FacultyOrHODDetails["uid"] = mAuth.uid.toString()
                 FacultyOrHODDetails["email"] = user?.email.toString()
                 FacultyOrHODDetails["name"] = user?.displayName.toString()

                 mRef.child(mAuth.uid.toString()).setValue(FacultyOrHODDetails).addOnCompleteListener {

                 }.addOnFailureListener {
                     binding?.progressBar?.visibility = View.GONE
                     binding?.signInBtn?.visibility = View.VISIBLE
                 }*/
            } else {
                // If sign in fails, display a message to the user.
                //Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(activity as Context, "Signed in un-successful", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun validateFieldsBeforeSignIn() {
        binding?.signInBtn?.setOnClickListener {
            if (isValidated()) {
                binding?.progressBar?.visibility = View.VISIBLE
                binding?.signInBtn?.visibility = View.GONE
                signInWithEmailAndPassUser()
            }
        }
    }


    private fun signInWithEmailAndPassUser() {
        mAuth.signInWithEmailAndPassword(
            binding?.etEmail?.text?.trim().toString(),
            binding?.etPassword?.text?.trim().toString()
        ).addOnCompleteListener {

            if (it.isSuccessful) {
                binding?.progressBar?.visibility = View.GONE
                binding?.signInBtn?.visibility = View.VISIBLE

                //save role of user
                val jsonObject = JSONObject()
                jsonObject.put("selectedRole", selectedRole)
                sharedPrefs.saveUserRole(jsonObject)

                startActivityBasedOnSelectedRole()

                Toast.makeText(
                    activity as Context,
                    "Signed in successful!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.addOnFailureListener {
            Log.d("signInWithEmailAndPassword", "signInUser: ${it.message} ")
            Toast.makeText(activity as Context, "Password is invalid.", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.visibility = View.GONE
            binding?.signInBtn?.visibility = View.VISIBLE
        }
    }

    private fun startActivityBasedOnSelectedRole() {
        when (selectedRole) {
            0L/*Student*/ -> {
                startActivity(Intent(activity as Context, StudentHomePage::class.java))
                (activity as AppCompatActivity).finishAffinity()
            }

            2L/*HOD*/ -> {
                startActivity(Intent(activity as Context, HODHomePage::class.java))
                (activity as AppCompatActivity).finishAffinity()
            }

            3L /*Faculty*/ -> {
                startActivity(Intent(activity as Context, FacultyHomePage::class.java))
                (activity as AppCompatActivity).finishAffinity()
            }
        }
    }


    private fun isValidated(): Boolean {
        return if (binding?.etEmail?.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
                binding?.etEmail?.text!!.trim().toString()
            )
                .matches()
        ) {
            binding?.etEmail?.error = null
            if (binding?.etPassword?.text!!.isNotEmpty()) {
                binding?.etPassword?.error = null
                true
            } else {
                binding?.etPassword?.error = "Please enter your password."
                false
            }
        } else {
            binding?.etEmail?.error = "Invalid email."
            false
        }
    }
}