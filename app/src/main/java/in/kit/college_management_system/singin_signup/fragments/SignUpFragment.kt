package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentSignUpBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.hodSection.HODHomePage
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import org.json.JSONObject


class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var requestGoolgeSign: ActivityResultLauncher<Intent>
    private var selectedRole = 0L
    private lateinit var sharedPrefs: Preferences
    private var selectedBranch = ""
    private var selectedBranchPos = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding!!.root

        firebaseHelperClass = FirebaseHelperClass()
        mAuth = FirebaseAuth.getInstance()
        sharedPrefs = Preferences(activity as Context).getInstance(activity as Context)

        selectedRole = arguments?.getLong("role")!!
        basedOnRoleManageViews()

        binding?.arrowBack?.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        handleBranchMenuOption()

        // signUp With EmailPass
        validateFieldsBeforeSignUp()

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
                mRef = firebaseHelperClass.getStudentRef()
            }
            1L -> {
                binding?.chooseRoleTV!!.text = "as Principal"
                binding?.llSignUpWithGoogle!!.visibility = View.VISIBLE
                mRef = firebaseHelperClass.getPrincipalRef()
            }
            2L -> {
                binding?.chooseRoleTV!!.text = "as HOD"
                binding?.llSignUpWithGoogle!!.visibility = View.GONE
                binding?.ilBranchName?.visibility = View.VISIBLE
                mRef = firebaseHelperClass.getHodRef()

            }
            3L -> {
                binding?.chooseRoleTV!!.text = "as Faculty"
                binding?.llSignUpWithGoogle!!.visibility = View.GONE
                binding?.ilBranchName?.visibility = View.VISIBLE
                mRef = firebaseHelperClass.getFacultyRef()
            }
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
                    binding?.signUpBtn?.visibility = View.VISIBLE
                }
            }
    }

    private fun signUpWithGoogle() {
        binding?.googleSignUpBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE
            binding?.signUpBtn?.visibility = View.GONE
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                //Log.d(TAG, "signInWithCredential:success")
                val user = mAuth.currentUser

                val details = HashMap<String, Any>()
                details["uid"] = mAuth.uid.toString()
                details["email"] = user?.email.toString()
                details["name"] = user?.displayName.toString()

                mRef.child(mAuth.uid.toString()).setValue(details).addOnCompleteListener {
                    Toast.makeText(
                        activity as Context,
                        "Signed up successful!!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding?.progressBar?.visibility = View.GONE
                    binding?.signUpBtn?.visibility = View.VISIBLE

                    //save role of user
                    val jsonObject = JSONObject()
                    jsonObject.put("selectedRole", selectedRole)
                    sharedPrefs.saveUserRole(jsonObject)

                    //open a principal home page activity........
                    startActivity(Intent(activity as Context, PrincipalHomePage::class.java))
                    (activity as AppCompatActivity).finishAffinity()
                }.addOnFailureListener {
                    binding?.progressBar?.visibility = View.GONE
                    binding?.signUpBtn?.visibility = View.VISIBLE
                }
            } else {
                // If sign in fails, display a message to the user.
                //Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(activity as Context, "Signed up un-successful", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun openSignInFrag() {
        val bundle = Bundle()
        bundle.putLong("role", selectedRole)
        val signInFragment = SignInFragment()
        signInFragment.arguments = bundle

        binding?.signInTVBtn?.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, signInFragment).commit()
        }
    }

    private fun handleBranchMenuOption() {
        val branchList =
            listOf("BS - Basic Science", "CSE", "EC", "EE", "Mech", "Aero", "Civil")
        val branchListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, branchList)
        (binding?.ilBranchName?.editText as? AutoCompleteTextView)?.setAdapter(
            branchListArrayAdapter
        )

        (binding?.ilBranchName!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                //0 -> BS - Basic Science
                //1 -> CSE
                //2 -> EC
                //3 -> EE
                //4 -> Mech
                //5 -> Aero
                //6 -> Civil
                val branchName = p0.getItemAtPosition(position).toString()
                selectedBranch = if (branchName == "BS - Basic Science") {
                    branchName.split(" ")[0]
                } else {
                    branchName
                }
                selectedBranchPos = position

            }
    }

    private fun validateFieldsBeforeSignUp() {
        binding?.signUpBtn?.setOnClickListener {
            if (selectedRole == 1L/*principal*/) {
                if (validateFieldsForPrincipal()) {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.signUpBtn?.visibility = View.GONE
                    signUpUsingEmailAndPassUser()
                }
            } else {
                if (validateFieldsForOthers()) {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.signUpBtn?.visibility = View.GONE
                    signUpUsingEmailAndPassUser()
                }
            }

        }
    }

    private fun signUpUsingEmailAndPassUser() {
        mAuth.createUserWithEmailAndPassword(
            binding?.etEmail?.text?.toString()!!.trim(),
            binding?.etPassword?.text?.toString()!!.trim()
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                val details = HashMap<String, Any>()
                details["uid"] = mAuth.uid.toString()
                details["email"] = binding?.etEmail?.text?.toString()!!.trim()
                details["name"] = binding?.etName?.text?.toString()!!.trim()

                if (selectedRole == 2L || selectedRole == 3L) {
                    details["branch"] = selectedBranch.trim()
                }

                mRef.child(mAuth.uid.toString()).setValue(details)
                    .addOnCompleteListener {
                        binding?.progressBar?.visibility = View.GONE
                        binding?.signUpBtn?.visibility = View.VISIBLE
                        Toast.makeText(
                            activity as Context,
                            "Signed up successful!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        //save role of user
                        val jsonObject = JSONObject()
                        jsonObject.put("selectedRole", selectedRole)
                        sharedPrefs.saveUserRole(jsonObject)

                        startActivityBasedOnSelectedRole()
                    }.addOnFailureListener {
                        binding?.progressBar?.visibility = View.GONE
                        binding?.signUpBtn?.visibility = View.VISIBLE
                    }
            }
        }.addOnFailureListener {
            Log.d("createUserWithEmailAndPassword", "signUpUser: ${it.message} ")
            Toast.makeText(activity as Context, "${it.message}", Toast.LENGTH_SHORT).show()
            binding?.progressBar?.visibility = View.GONE
            binding?.signUpBtn?.visibility = View.VISIBLE
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

    private fun validateFieldsForPrincipal(): Boolean {
        return if (binding?.etName?.text!!.isNotEmpty()) {
            binding?.tillName?.isErrorEnabled = false
            binding?.tillName?.error = null
            binding?.tillName?.endIconDrawable =
                ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
            if (binding?.etEmail?.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
                    binding?.etEmail?.text!!.trim().toString()
                ).matches()
            ) {
                binding?.tilEmail?.isErrorEnabled = false
                binding?.tilEmail?.error = null
                binding?.tilEmail?.endIconDrawable =
                    ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
                if (binding?.etPassword?.text!!.isNotEmpty()) {
                    binding?.tilPassword?.isErrorEnabled = false
                    binding?.tilPassword?.error = null
                    true
                } else {
                    binding?.tilPassword?.isErrorEnabled = true
                    binding?.tilPassword?.error = "Please enter your password."
                    false
                }
            } else {
                binding?.tilEmail?.isErrorEnabled = true
                binding?.tilEmail?.error = "Invalid email."
                false
            }
        } else {
            binding?.tillName?.isErrorEnabled = true
            binding?.tillName?.error = "Please enter your name."
            //binding?.etName?.error = "Field can't be empty"
            false
        }
    }

    private fun validateFieldsForOthers(): Boolean {
        return if (binding?.etName?.text!!.isNotEmpty()) {
            binding?.tillName?.isErrorEnabled = false
            binding?.tillName?.error = null
            binding?.tillName?.endIconDrawable =
                ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
            if (binding?.etEmail?.text!!.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
                    binding?.etEmail?.text!!.trim().toString()
                ).matches()
            ) {
                binding?.tilEmail?.isErrorEnabled = false
                binding?.tilEmail?.error = null
                binding?.tilEmail?.endIconDrawable =
                    ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
                if (selectedBranch.isNotEmpty()) {
                    //binding?.etBranchName?.error = null
                    binding?.ilBranchName?.isErrorEnabled = false
                    binding?.ilBranchName?.error = null
                    if (binding?.etPassword?.text!!.isNotEmpty()) {
                        binding?.tilPassword?.isErrorEnabled = false
                        binding?.tilPassword?.error = null
                        true
                    } else {
                        binding?.tilPassword?.isErrorEnabled = true
                        binding?.tilPassword?.error = "Please enter your password."
                        false
                    }
                } else {
                    binding?.ilBranchName?.isErrorEnabled = true
                    binding?.ilBranchName?.error = "Branch name is required."
                    Toast.makeText(
                        activity as Context,
                        "Branch name is required.",
                        Toast.LENGTH_SHORT
                    ).show()
                    //binding?.etBranchName?.error = "Please enter branch name."
                    false
                }
            } else {
                binding?.tilEmail?.isErrorEnabled = true
                binding?.tilEmail?.error = "Invalid email."
                false
            }
        } else {
            binding?.tillName?.isErrorEnabled = true
            binding?.tillName?.error = "Please enter your name."
            false
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        //updateUI(currentUser)
    }
}