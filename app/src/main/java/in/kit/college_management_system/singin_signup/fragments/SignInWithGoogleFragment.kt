package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentSignInWithGoogleBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.hodSection.activity.HODHomePage
import `in`.kit.college_management_system.principalSection.PrincipalHomePage
import `in`.kit.college_management_system.studentSection.activity.StudentHomePage
import `in`.kit.college_management_system.utils.Constants.FACULTY
import `in`.kit.college_management_system.utils.Constants.HOD
import `in`.kit.college_management_system.utils.Constants.PRINCIPAL
import `in`.kit.college_management_system.utils.Constants.STUDENT
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys.ADDRESS
import `in`.kit.college_management_system.utils.FirebaseKeys.BATCH
import `in`.kit.college_management_system.utils.FirebaseKeys.BRANCH
import `in`.kit.college_management_system.utils.FirebaseKeys.EMAIL
import `in`.kit.college_management_system.utils.FirebaseKeys.GENDER
import `in`.kit.college_management_system.utils.FirebaseKeys.LEAVES
import `in`.kit.college_management_system.utils.FirebaseKeys.NAME
import `in`.kit.college_management_system.utils.FirebaseKeys.PHOTO_URL
import `in`.kit.college_management_system.utils.FirebaseKeys.SEM
import `in`.kit.college_management_system.utils.FirebaseKeys.UID
import `in`.kit.college_management_system.utils.FirebaseKeys.USN
import `in`.kit.college_management_system.utils.Preferences
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject

class SignInWithGoogleFragment : Fragment() {


    private var _binding: FragmentSignInWithGoogleBinding? = null
    private val binding get() = _binding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var requestGoolgeSign: ActivityResultLauncher<Intent>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPrefs: Preferences
    private var selectedRole: Long = 0L
    private lateinit var mRef: DatabaseReference
    private lateinit var firebaseHelperClass: FirebaseHelperClass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentSignInWithGoogleBinding.inflate(inflater, container, false)
        val view = binding!!.root

        selectedRole = arguments?.getLong("role")!!

        binding?.textView?.text =
            if (selectedRole == 0L) "See your attendance status, Leaves request status and more" else "Manage your college at finger tips."

        /* val bundle = Bundle()
         bundle.putLong("role", selectedRole!!)
         val signUpFragment = SignUpFragment()
         val signInFragment = SignInFragment()
         signUpFragment.arguments = bundle
         signInFragment.arguments = bundle*/

        firebaseHelperClass = FirebaseHelperClass()

        mRef = when (selectedRole) {
            STUDENT -> {
                firebaseHelperClass.getStudentRef()
            }
            PRINCIPAL -> {
                firebaseHelperClass.getPrincipalRef()
            }
            HOD -> {
                firebaseHelperClass.getHodRef()
            }
            FACULTY -> {
                firebaseHelperClass.getFacultyRef()
            }
            else -> throw IllegalArgumentException("Invalid ViewType Provider")
        }

        mAuth = FirebaseAuth.getInstance()
        sharedPrefs = Preferences(activity as Context).getInstance(activity as Context)

        /* binding?.signUpBtn?.setOnClickListener {
             (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                 .replace(R.id.fragmentContainer, signUpFragment, "signUpFragment").commit()
         }*/

        /*binding?.signInBtn?.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, signInFragment, "signInFragment").commit()
        }*/

        activityResult()

        // sign up with google
        signUpWithGoogle()

        return view
    }

    private fun signUpWithGoogle() {
        binding?.signInWithGoogleBtn?.setOnClickListener {
            binding?.progressBar?.visibility = View.VISIBLE
            binding?.signInWithGoogleBtn?.visibility = View.GONE
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
                    binding?.signInWithGoogleBtn?.visibility = View.VISIBLE
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information

                when (selectedRole) {
                    STUDENT -> signInAsStudent(task)

                    PRINCIPAL -> signInAsPrincipal()

                    HOD -> signInAsFacultyAndHod(task)

                    FACULTY -> signInAsFacultyAndHod(task)

                }

            } else {
                // If sign in fails, display a message to the user.
                //Log.w(TAG, "signInWithCredential:failure", task.exception)
                Toast.makeText(
                    activity as Context,
                    "Signed in un-successful",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun signInAsFacultyAndHod(task: Task<AuthResult>) {
        val user = mAuth.currentUser

        val facultyOrHODDetails = HashMap<String, Any>()
        facultyOrHODDetails[UID] = mAuth.uid.toString()
        facultyOrHODDetails[EMAIL] = user?.email.toString()
        facultyOrHODDetails[NAME] = user?.displayName.toString()
        if (user?.photoUrl != null) {
            facultyOrHODDetails[PHOTO_URL] = user.photoUrl?.toString()!!
        } else {
            facultyOrHODDetails[PHOTO_URL] = ""
        }

        val facultyOrHodExtraDetailsFrag = FacultyOrHODExtraDetailsFragment()

        mRef.child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(BRANCH) && !task.result.additionalUserInfo?.isNewUser!!) {
                        //old user
                        binding?.progressBar?.visibility = View.GONE
                        binding?.signInWithGoogleBtn?.visibility = View.VISIBLE

                        //save role of user
                        val jsonObject = JSONObject()
                        jsonObject.put("selectedRole", selectedRole)
                        sharedPrefs.saveUserRole(jsonObject)

                        if (selectedRole == FACULTY) {
                            //faculty
                            startActivity(
                                Intent(
                                    activity as Context,
                                    FacultyHomePage::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    activity as Context,
                                    HODHomePage::class.java
                                )
                            )
                        }

                        (activity as AppCompatActivity).finishAffinity()
                    } else {
                        //new user
                        mRef.child(mAuth.uid.toString()).setValue(facultyOrHODDetails)
                            .addOnCompleteListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE

                                Toast.makeText(
                                    activity as Context,
                                    "Signed in successful!!",
                                    Toast.LENGTH_SHORT
                                ).show()


                                //save role of user
                                val jsonObject = JSONObject()
                                jsonObject.put("selectedRole", selectedRole)
                                sharedPrefs.saveUserRole(jsonObject)

                                val bundle = Bundle()
                                bundle.putLong("selectedRole", selectedRole)
                                facultyOrHodExtraDetailsFrag.arguments = bundle

                                (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragmentContainer,
                                        facultyOrHodExtraDetailsFrag,
                                        "fragmentFacultyExtraDetailsFrag"
                                    ).commit()

                            }.addOnFailureListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE
                            }

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun signInAsPrincipal() {
        val user = mAuth.currentUser

        val principalDetails = HashMap<String, Any>()
        principalDetails[UID] = mAuth.uid.toString()
        principalDetails[EMAIL] = user?.email.toString()
        principalDetails[NAME] = user?.displayName.toString()
        if (user?.photoUrl != null) {
            principalDetails[PHOTO_URL] = user.photoUrl?.toString()!!
        } else {
            principalDetails[PHOTO_URL] = ""
        }

        mRef.child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mRef.child(mAuth.uid.toString()).setValue(principalDetails)
                        .addOnCompleteListener {
                            binding?.progressBar?.visibility = View.GONE
                            binding?.signInWithGoogleBtn?.visibility = View.VISIBLE

                            Toast.makeText(
                                activity as Context,
                                "Signed in successful!!",
                                Toast.LENGTH_SHORT
                            ).show()

                            //save role of user
                            val jsonObject = JSONObject()
                            jsonObject.put("selectedRole", selectedRole)
                            sharedPrefs.saveUserRole(jsonObject)

                            startActivity(
                                Intent(
                                    activity as Context,
                                    PrincipalHomePage::class.java
                                )
                            )

                        }.addOnFailureListener {
                            binding?.progressBar?.visibility = View.GONE
                            binding?.signInWithGoogleBtn?.visibility = View.VISIBLE
                        }


                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun signInAsStudent(task: Task<AuthResult>) {
        val user = mAuth.currentUser
        val studentDetails = HashMap<String, Any>()
        studentDetails[UID] = mAuth.uid.toString()
        studentDetails[EMAIL] = user?.email.toString()
        studentDetails[NAME] = user?.displayName.toString()

        val fragmentStudentExtraDetails = StudentExtraDetailsFragment()

        mRef.child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(SEM) &&
                        snapshot.hasChild(BATCH) &&
                        snapshot.hasChild(BRANCH) &&
                        snapshot.hasChild(ADDRESS) &&
                        snapshot.hasChild(GENDER) &&
                        snapshot.hasChild(USN) &&
                        !task.result.additionalUserInfo?.isNewUser!!
                    ) {
                        // home Page

                        val branch = snapshot.child("branch").value.toString()
                        val batch = snapshot.child("batch").value.toString().toInt()

                        /** add details in all_student_details */
                        val stdDetails = HashMap<String, Any>()
                        stdDetails[UID] = snapshot.child(UID).value.toString()
                        stdDetails[EMAIL] = snapshot.child(EMAIL).value.toString()
                        stdDetails[NAME] = snapshot.child(NAME).value.toString()
                        stdDetails[SEM] = snapshot.child(SEM).value.toString()
                        stdDetails[BATCH] = batch
                        stdDetails[ADDRESS] = snapshot.child(ADDRESS).value.toString()
                        stdDetails[GENDER] = snapshot.child(GENDER).value.toString()
                        stdDetails[BRANCH] = branch
                        //stdDetails[PRESENT] = 0
                        //stdDetails[ABSENT] = 0
                        stdDetails[LEAVES] = 0
                        stdDetails[USN] = snapshot.child(USN).value.toString()
                        if (user?.photoUrl != null) {
                            studentDetails[PHOTO_URL] = user.photoUrl?.toString()!!
                        } else {
                            studentDetails[PHOTO_URL] = ""
                        }
                        FirebaseHelperClass().getAllStudentDetailsRef()
                            .child(branch)
                            .child(batch.toString()).child("details")
                            .child(mAuth.uid.toString())
                            .setValue(stdDetails).addOnCompleteListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE

                                //save role of user
                                val jsonObject = JSONObject()
                                jsonObject.put("selectedRole", selectedRole)
                                sharedPrefs.saveUserRole(jsonObject)

                                startActivity(
                                    Intent(
                                        activity as Context,
                                        StudentHomePage::class.java
                                    )
                                )

                                (activity as AppCompatActivity).finishAffinity()
                            }.addOnFailureListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE
                            }

                    } else {
                        //new user
                        mRef.child(mAuth.uid.toString()).setValue(studentDetails)
                            .addOnCompleteListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE

                                Toast.makeText(
                                    activity as Context,
                                    "Signed in successful!!",
                                    Toast.LENGTH_SHORT
                                ).show()


                                //save role of user
                                val jsonObject = JSONObject()
                                jsonObject.put("selectedRole", selectedRole)
                                sharedPrefs.saveUserRole(jsonObject)

                                (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.fragmentContainer,
                                        fragmentStudentExtraDetails,
                                        "fragmentStudentExtraDetails"
                                    ).commit()

                            }.addOnFailureListener {
                                binding?.progressBar?.visibility = View.GONE
                                binding?.signInWithGoogleBtn?.visibility = View.VISIBLE
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }
}