package `in`.kit.college_management_system.studentSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentStudentProfileBinding
import `in`.kit.college_management_system.singin_signup.AuthenticationActivity
import `in`.kit.college_management_system.utils.AlertDialogHelperClass
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.Preferences
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class StudentProfileFragment : Fragment() {

    private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        val view = binding?.root

//        binding!!.profileHeaderShimmer.startShimmerAnimation()
//        binding!!.profileDetailsShimmer.startShimmerAnimation()
//
        logOut()
//        firebaseHelperClass = FirebaseHelperClass()
//        firebaseHelperClass.getFacultyDetails(
//            FirebaseAuth.getInstance(),
//            object : IOnFirebaseActionCallback {
//                override fun getAllFacultyDetailsCallback(facultyDetails: FacultyOrHODDetails) {
//
//                    binding!!.nameDesTV.visibility = View.VISIBLE
//                    binding!!.emailDesTV.visibility = View.VISIBLE
//                    binding!!.designationDes.visibility = View.VISIBLE
//                    binding!!.branchDes.visibility = View.VISIBLE
//
//                    binding!!.facultyNameTV.text = facultyDetails.name
//                    binding!!.branchNameTV.text = facultyDetails.branch
//                    binding!!.facultyEmailTV.text = facultyDetails.email
//                    binding!!.facultyDesignationTV.text = "Assistance Professor"
//
//                    binding!!.headerFacultyName.text = facultyDetails.name
//                    binding!!.headerDesignationAndBranchTV.text =
//                        "Assistance Professor - ${facultyDetails.branch}"
//
//                    binding!!.profileHeaderShimmer.stopShimmerAnimation()
//                    binding!!.profileDetailsShimmer.stopShimmerAnimation()
//                    binding!!.profileHeaderShimmer.visibility = View.GONE
//                    binding!!.profileDetailsShimmer.visibility = View.GONE
//
//                    extractTwoCharFromName(facultyDetails.name)
//                }
//            })
        return view
    }

    private fun logOut() {
        binding!!.logOutBtn.setOnClickListener {
            AlertDialogHelperClass(activity as Context).apply {
                this.build(
                    "Log Out!",
                    "Are you sure for logging out?",
                    "Yes",
                    "Not now!"
                )
                this.isCancelable(true)
                this.show()
                this.listener = object : AlertDialogHelperClass.OnAlertDialogActionPerformed {
                    override fun positiveAction(dialog: DialogInterface) {

                        val materialAlertDialogBuilder =
                            MaterialAlertDialogBuilder(activity as Context)
                        materialAlertDialogBuilder.setTitle("Logging Out")
                        materialAlertDialogBuilder.setMessage(
                            "Please wait it will not take much time."
                        )
                        val dialogLogOut = materialAlertDialogBuilder.create()
                        dialogLogOut.show()
                        dialogLogOut.setCancelable(false)

                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()

                        val mGoogleSignInClient = GoogleSignIn.getClient(activity as Context, gso)
                        mGoogleSignInClient.signOut().addOnCompleteListener {
                            FirebaseAuth.getInstance().signOut()

                            val preferences = Preferences(activity as Context)
                            preferences.removeUserRoleValue()

                            val myIntent = Intent(context, AuthenticationActivity::class.java)
                            startActivity(myIntent)
                            ActivityCompat.finishAffinity(requireActivity())
                            dialog.dismiss()
                            dialogLogOut.dismiss()
                        }
                    }

                    override fun negativeAction(dialog: DialogInterface) {
                        dialog.dismiss()
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun extractTwoCharFromName(facultyName: String) {
        val indexOfSpaceInName = facultyName.indexOf(" ")
        binding!!.initialCharOfNameTV.text =
            "${facultyName[0]}${facultyName[indexOfSpaceInName + 1]}"
    }

}