package `in`.kit.college_management_system.hodSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentDashBoardBinding
import `in`.kit.college_management_system.hodSection.activity.StudentListBranchWiseActivity
import `in`.kit.college_management_system.hodSection.adapter.FacultiesListAdapter
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.FacultyOrHODDetails
import `in`.kit.college_management_system.utils.Branches
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys.BRANCH
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class HODDashBoardFragment : Fragment() {

    private var _binding: FragmentDashBoardBinding? = null
    private val binding get() = _binding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mAuth: FirebaseAuth
    private var tempHODDetails = FacultyOrHODDetails()
    private lateinit var adapter: FacultiesListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        val view = binding?.root

        firebaseHelperClass = FirebaseHelperClass()
        mAuth = FirebaseAuth.getInstance()

        setUpRecyclerView()

        getHODDetails()

        binding?.cardStdList?.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    StudentListBranchWiseActivity::class.java
                ).putExtra(BRANCH, tempHODDetails.branch)
            )
        }

        return view
    }

    private fun setUpRecyclerView() {
        adapter = FacultiesListAdapter(requireContext())
        binding?.rvFacultyList?.adapter = adapter
    }

    private fun getFacultyList() {
        firebaseHelperClass.getFacultyDetailsBranchWise(tempHODDetails.branch,
            object : IOnFirebaseActionCallback {
                override fun getFacultyDetailsBranchWiseCallBack(facultyList: ArrayList<FacultyOrHODDetails>) {
                    if (facultyList.isNotEmpty()) {
                        binding?.flNoFacultiesFound?.isVisible = false

                        val tempFacultyList = ArrayList<FacultyOrHODDetails>()
                        tempFacultyList.addAll(facultyList)
                        adapter.submitList(tempFacultyList)
                        //Log.d("tempFacultyList", "getFacultyDetailsBranchWiseCallBack: $tempFacultyList ")
                    } else {
                        //shimmer
                        binding?.flNoFacultiesFound?.isVisible = true
                    }
                }
            })
    }

    private fun getHODDetails() {
        firebaseHelperClass.getHODDetails(
            mAuth.uid.toString(),
            object : IOnFirebaseActionCallback {
                @SuppressLint("SetTextI18n")
                override fun getFacultyOrHODDetailsCallback(
                    facultyOrHODDetails: FacultyOrHODDetails,
                    context: Context
                ) {
                    tempHODDetails = facultyOrHODDetails
                    val facultyName: StringBuilder = StringBuilder()
                    SplitAndGetNameInitials().apply {
                        val splitName: List<String> = splitName(facultyOrHODDetails.name)
                        facultyName.append("${splitName[0]}")
                        if (splitName.size >= 2) {
                            facultyName.append(" ${splitName[1][0]} .")
                        }
                    }
                    binding!!.hodNameTV.text = facultyName
                    binding!!.designationDes.text = "HOD of ${facultyOrHODDetails.branch}"
                    binding!!.greetingTV.text = "Hello,"

                    if (facultyOrHODDetails.photo_url != "") {
                        Glide.with(context).load(facultyOrHODDetails.photo_url)
                            .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                            .into(binding!!.profileImg)
                    } else {
                        var nameInitials = ""
                        SplitAndGetNameInitials().apply {
                            val splitNameList = splitName(facultyOrHODDetails.name)
                            nameInitials = getNameInitials(splitNameList)
                        }
                        binding!!.nameInitialsTV.text = nameInitials
                    }

                    getFacultyList()

                    // binding!!.headerLoadingAnimation.stopShimmerAnimation()
                    //binding!!.headerLoadingAnimation.visibility = View.GONE

                    //extractTwoCharFromName(facultyOrHODDetails.name)
                    //getAllClassesFromFirebase(b)
                }
            }, requireContext()
        )

    }

}