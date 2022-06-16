package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentFacultyExtraDetailsBinding
import `in`.kit.college_management_system.facultySection.activity.FacultyHomePage
import `in`.kit.college_management_system.utils.AlertDialogHelperClass
import `in`.kit.college_management_system.utils.Branches.AERO
import `in`.kit.college_management_system.utils.Branches.CIVIL
import `in`.kit.college_management_system.utils.Branches.CSE
import `in`.kit.college_management_system.utils.Branches.EC
import `in`.kit.college_management_system.utils.Branches.EE
import `in`.kit.college_management_system.utils.Branches.MECH
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys.BRANCH
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class FacultyExtraDetailsFragment : Fragment() {

    private var _binding: FragmentFacultyExtraDetailsBinding? = null
    private val binding get() = _binding
    private var selectedBranch = ""
    private var selectedBranchPos = -1
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFacultyExtraDetailsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        if (selectedBranchPos == -1) binding?.floatingBtn?.visibility = View.INVISIBLE

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseHelperClass().getFacultyRef()

        binding?.arrowBack?.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }
        showFacultyDetails()
        handleBranchMenuOption()

        openFacultyHomePage()

        return view

    }

    private fun showFacultyDetails() {
        val currentUser = mAuth.currentUser
        binding?.facultyName?.text = currentUser?.displayName
        binding?.facultyEmail?.text = currentUser?.email

        if (currentUser?.displayName != null) {
            Glide.with(requireContext()).load(currentUser.photoUrl).into(binding?.profileImg!!)
        } else {
            val firstChar = currentUser?.displayName?.get(0).toString()
            val secondChar = currentUser?.displayName?.get(1).toString()
            binding?.nameInitialsTV?.text = "$firstChar$secondChar"
        }
    }

    private fun handleBranchMenuOption() {
        val branchList =
            listOf("BS - Basic Science", CSE, EC, EE, MECH, AERO, CIVIL)
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

                binding?.floatingBtn?.visibility = View.VISIBLE
            }
    }

    private fun openFacultyHomePage() {
        binding?.floatingBtn?.setOnClickListener {
            val alertDialogHelperClass = AlertDialogHelperClass(requireContext())
            alertDialogHelperClass.apply {
                this.build(
                    "Confirmation!!",
                    "Would you like to proceed as Asst. Prof. Of $selectedBranch?",
                    "Yes",
                    "Cancel"
                )
                this.isCancelable(true)
                this.show()

                this.listener = object : AlertDialogHelperClass.OnAlertDialogActionPerformed {
                    override fun positiveAction(dialog: DialogInterface) {
                        binding?.floatingBtn?.visibility = View.INVISIBLE
                        binding?.progressBar?.visibility = View.VISIBLE

                        mRef.child(mAuth.uid.toString()).child(BRANCH).setValue(selectedBranch)
                            .addOnCompleteListener {
                                binding?.floatingBtn?.visibility = View.VISIBLE
                                binding?.progressBar?.visibility = View.GONE

                                startActivity(
                                    Intent(
                                        activity as Context,
                                        FacultyHomePage::class.java
                                    )
                                )
                                (activity as AppCompatActivity).finishAffinity()
                            }
                            .addOnFailureListener {
                                binding?.floatingBtn?.visibility = View.VISIBLE
                                binding?.progressBar?.visibility = View.GONE
                            }
                    }

                    override fun negativeAction(dialog: DialogInterface) {
                        dialog.dismiss()

                    }
                }

            }

        }
    }

}