package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.databinding.FragmentLeavesBinding
import `in`.kit.college_management_system.facultySection.adapter.FacultyStudentLeaveParentAdapter
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.FacultySecStudentLeaveModel
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth


class LeavesFragmentFacultySection : Fragment() {

    private var _binding: FragmentLeavesBinding? = null
    private val binding get() = _binding
    private val firebaseHelperClass = FirebaseHelperClass()
    private lateinit var adapter: FacultyStudentLeaveParentAdapter
    private var leaveList = ArrayList<FacultySecStudentLeaveModel>()
    private var mAuth = FirebaseAuth.getInstance()
    private val batchList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLeavesBinding.inflate(inflater, container, false)
        val view = binding?.root


        batchList.add("All Batches")
        val allBatchChip = Chip(activity as Context)
        allBatchChip.text = "All Batches"

        allBatchChip.isCheckedIconVisible = true
        allBatchChip.isCheckable = true
        allBatchChip.isChecked = true
        binding?.leaveFilterGroup?.addView(allBatchChip)

        setUpRecyclerView()

        firebaseHelperClass.getFacultyClassFromFirebase(mAuth, object : IOnFirebaseActionCallback {
            override fun getFacultyAllClassesCallback(classModel: ClassesModel?, context: Context) {
                if (classModel != null) {
                     addFilterChipsAndGetLeaves(classModel, "All Batches")
                }
            }
        }, requireContext())
        return view
    }

    private fun setUpRecyclerView() {
        adapter = FacultyStudentLeaveParentAdapter(requireContext())
        binding?.rvAllLeaves?.adapter = adapter
    }

    private fun getLeaves(filter: String, classModel: ClassesModel) {
        Log.d("mClassModel...", "getLeaves:$filter $classModel ")

        firebaseHelperClass.getStdLeavesAccordingToBatchForFaculty(
            classModel.branch,
            classModel.batchOrYear,
            filter,
            object : IOnFirebaseActionCallback {
                override fun getStdLeavesAccordingToBatchForFacultyCallBack(
                    leaveList: ArrayList<FacultySecStudentLeaveModel>,
                    context: Context
                ) {
                    super.getStdLeavesAccordingToBatchForFacultyCallBack(leaveList, context)
                    if (leaveList.isNotEmpty()) {
                        this@LeavesFragmentFacultySection.leaveList = leaveList
                        Log.d("mClassModel", "getLeaves:$leaveList ")

                        val tempLeaveList = ArrayList<FacultySecStudentLeaveModel>()
                        tempLeaveList.addAll(leaveList)
                        adapter.submitList(tempLeaveList)

                    } else {
                        Toast.makeText(context, "No Students have taken leaves.", Toast.LENGTH_SHORT).show()
                        //submit an empty list
                        val tempLeaveList = ArrayList<FacultySecStudentLeaveModel>()
                        tempLeaveList.addAll(leaveList)
                        adapter.submitList(tempLeaveList)

                        // shimmer
                        //todo shimmer

                    }
                }
            },
            requireContext()

        )


    }

    private fun addFilterChipsAndGetLeaves(classModel: ClassesModel, filter: String) {
        val chipTag = "${classModel.batchOrYear} - ${classModel.branch}"
        if (!batchList.contains(chipTag)) {
            //here i am preventing to add duplicate batches
            batchList.add(chipTag)
            val chip = Chip(activity as Context)
            chip.text = chipTag

            chip.isCheckedIconVisible = true
            chip.isCheckable = true
            binding?.leaveFilterGroup?.addView(chip)

            // if faculty taking more than one class in one batch then it will get filtered here
            // so for duplicate batches i am preventing it to fetches data from firebase
            getLeaves(filter, classModel)

            chip.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    val selectedChipTag = compoundButton.text
                    getLeaves(selectedChipTag.toString(), classModel)
                }
            }
        }
    }
}