package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentStudentExtraDetailsBinding
import `in`.kit.college_management_system.studentSection.activity.StudentHomePage
import `in`.kit.college_management_system.utils.AlertDialogHelperClass
import `in`.kit.college_management_system.utils.Branches.AERO
import `in`.kit.college_management_system.utils.Branches.CIVIL
import `in`.kit.college_management_system.utils.Branches.CSE
import `in`.kit.college_management_system.utils.Branches.EC
import `in`.kit.college_management_system.utils.Branches.EE
import `in`.kit.college_management_system.utils.Branches.MECH
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
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.util.*

class StudentExtraDetailsFragment : Fragment() {

    private var _binding: FragmentStudentExtraDetailsBinding? = null
    private val binding get() = _binding
    private var selectedBatchYear = 0
    private var selectedBranch = ""
    private var selectedSem = ""
    private var selectedBranchPos = 0
    private var selectedSemPos = 0
    private var selectedGender = ""
    private var selectedGenderPos = 0
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStudentExtraDetailsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.arrowBack?.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseHelperClass().getStudentRef()

        showStudentDetails()

        openYearPicker()
        handleSemMenuOption()
        handleBranchMenuOption()
        handleGenderMenuOption()

        openStudentHomePage()

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun showStudentDetails() {
        val currentUser = mAuth.currentUser
        binding?.studentName?.text = currentUser?.displayName
        binding?.studentEmail?.text = currentUser?.email

        if (currentUser?.photoUrl != null) {
            Glide.with(requireContext()).load(currentUser.photoUrl)
                .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                .into(binding?.profileImg!!)
        } else {
            var nameInitials = ""
            SplitAndGetNameInitials().apply {
                val splitNameList = splitName(currentUser?.displayName!!)
                nameInitials = getNameInitials(splitNameList)
            }
            //val firstChar = currentUser?.displayName?.get(0).toString()
            //val secondChar = currentUser?.displayName?.get(1).toString()
            binding?.nameInitialsTV?.text = nameInitials
        }

    }

    private fun openStudentHomePage() {
        binding?.floatingBtn?.setOnClickListener {
            val usn = binding?.etUsn?.text?.trim().toString()
            val address = binding?.etAddress?.text?.trim().toString()

            if (validateFields(usn, address)) {
                val alertDialogHelperClass = AlertDialogHelperClass(requireContext())
                alertDialogHelperClass.apply {
                    this.build(
                        "Confirmation!!", "Would you like to proceed with these details?",
                        "Yes", "Cancel"
                    )
                    this.isCancelable(true)
                    this.show()

                    this.listener = object : AlertDialogHelperClass.OnAlertDialogActionPerformed {
                        override fun positiveAction(dialog: DialogInterface) {
                            binding?.floatingBtn?.visibility = View.INVISIBLE
                            binding?.progressBar?.visibility = View.VISIBLE

                            val user = mAuth.currentUser
                            val studentDetails = HashMap<String, Any>()
                            studentDetails[UID] = mAuth.uid.toString()
                            studentDetails[EMAIL] = user?.email.toString()
                            studentDetails[NAME] = user?.displayName.toString()
                            studentDetails[SEM] = selectedSem
                            studentDetails[BATCH] = selectedBatchYear
                            studentDetails[ADDRESS] = address
                            studentDetails[GENDER] = selectedGender
                            studentDetails[BRANCH] = selectedBranch
                            if (user?.photoUrl != null) {
                                studentDetails[PHOTO_URL] = user.photoUrl?.toString()!!
                            } else {
                                studentDetails[PHOTO_URL] = ""
                            }
                            studentDetails[USN] =
                                "4KM${
                                    selectedBatchYear.toString().trim('2', '0')
                                }$selectedBranch$usn"

                            /** add to student_auth_node */
                            mRef.child(mAuth.uid.toString()).setValue(studentDetails)
                                .addOnCompleteListener {

                                    /** add to all_student_details node */
                                    //studentDetails[PRESENT] = 0
                                    //studentDetails[ABSENT] = 0
                                    studentDetails[LEAVES] = 0
                                    FirebaseHelperClass().getAllStudentDetailsRef()
                                        .child(selectedBranch)
                                        .child(selectedBatchYear.toString()).child("details").child(mAuth.uid.toString())
                                        .setValue(studentDetails).addOnCompleteListener {

                                            binding?.floatingBtn?.visibility = View.VISIBLE
                                            binding?.progressBar?.visibility = View.GONE

                                            startActivity(
                                                Intent(
                                                    activity as Context,
                                                    StudentHomePage::class.java
                                                )
                                            )
                                            (activity as AppCompatActivity).finishAffinity()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                requireContext(),
                                                "${it.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding?.floatingBtn?.visibility = View.VISIBLE
                                            binding?.progressBar?.visibility = View.GONE
                                        }

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

    private fun validateFields(usn: String, address: String): Boolean {
        return if (usn.length == 3 && usn.isNotEmpty()) {
            binding?.ilUsn?.error = null
            binding?.ilUsn?.endIconDrawable =
                ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
            //Branch validation
            if (selectedBranch.isNotEmpty()) {
                binding?.chooseBranchMenu?.error = null

                if (selectedGender.isNotEmpty()) {
                    binding?.chooseGenderMenu?.error = null

                    //year validation
                    if (selectedBatchYear != 0) {

                        //sem validation
                        if (selectedSem.isNotEmpty()) {
                            binding?.chooseSemesterMenu?.error = null

                            if (address.isNotEmpty()) {
                                binding?.ilAddress?.error = null
                                true

                            } else {
                                binding?.ilAddress?.error = "Please enter address."
                                false
                            }

                        } else {
                            binding?.chooseSemesterMenu?.error = "Please choose semester"
                            false
                        }
                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Please choose batch",
                            Toast.LENGTH_SHORT
                        ).show()
                        false
                    }

                } else {
                    binding?.chooseGenderMenu?.error = "Please choose gender"
                    false
                }

            } else {
                binding?.chooseBranchMenu?.error = "Please choose branch"
                false
            }

        } else {
            binding?.ilUsn?.error = "Required length is 3"
            false
        }
    }

    private fun handleSemMenuOption() {
        val semList = listOf(
            "1st Sem",
            "2nd Sem",
            "3rd Sem",
            "4th Sem",
            "5th Sem",
            "6th Sem",
            "7th Sem",
            "8th Sem"
        )
        val semListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, semList)
        (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            semListArrayAdapter
        )

        (binding?.chooseSemesterMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                //0 -> CSE
                //1 -> E&C
                //2 -> EE
                //3 -> Mech
                //4 -> Aero
                //4 -> Civil

                selectedSem = p0.getItemAtPosition(position).toString()
                selectedSemPos = position

            }
    }

    private fun handleGenderMenuOption() {
        val semList = listOf(
            "Male",
            "Female"
        )
        val genderListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, semList)
        (binding?.chooseGenderMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            genderListArrayAdapter
        )

        (binding?.chooseGenderMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                //0 -> Male
                //1 -> Female

                selectedGender = p0.getItemAtPosition(position).toString()
                selectedGenderPos = position

            }
    }

    private fun handleBranchMenuOption() {
        val branchList =
            listOf(CSE, EC, EE, MECH, AERO, CIVIL)
        val branchListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, branchList)
        (binding?.chooseBranchMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            branchListArrayAdapter
        )

        (binding?.chooseBranchMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                //0 -> BS - Basic Science
                //1 -> CSE
                //2 -> E&C
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

    private fun openYearPicker() {
        binding?.yearPickerCard?.setOnClickListener {
            val today = Calendar.getInstance()

            val builder = MonthPickerDialog.Builder(
                activity as Context,
                { selectedMonth, selectedYear -> },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH)
            )

            builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(2018)
                .setActivatedYear(2022)
                .setMaxYear(2050)
                .setTitle("Select Batch Year")
                .showYearOnly()
                .setOnYearChangedListener {
                    //Toast.makeText(activity as Context, "$it", Toast.LENGTH_SHORT).show()
                    binding?.batchTV?.text = "Batch - $it"
                    selectedBatchYear = it
                }
                .build()
                .show()

        }
    }

}