package `in`.kit.college_management_system.studentSection.fragments

import `in`.kit.college_management_system.databinding.FragmentStudentLeavesBinding
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.model.StudentLeaveHelperModel
import `in`.kit.college_management_system.studentSection.adapters.StudentLeavesAdapter
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth


class StudentLeavesFragment : Fragment() {

    private var _binding: FragmentStudentLeavesBinding? = null
    private val binding get() = _binding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mAuth: FirebaseAuth
    private var studentLeaveList = ArrayList<StudentLeaveHelperModel>()
    private lateinit var studentLeavesAdapter: StudentLeavesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentLeavesBinding.inflate(inflater, container, false)
        val view = binding?.root

        mAuth = FirebaseAuth.getInstance()
        binding?.noLeavesLottie?.playAnimation()
        binding?.noLeavesLottie?.loop(true)
        firebaseHelperClass = FirebaseHelperClass()

        setUpRecyclerView()
        getLeaves("All leaves")

        binding?.chipCasual?.setOnClickListener {
            getLeaves("Casual")
        }

        binding?.chipAllLeaves?.setOnClickListener {
            getLeaves("All leaves")

        }
        binding?.chipSick?.setOnClickListener {
            getLeaves("Sick")
        }

        binding?.requestLeavesFloatingBtn?.setOnClickListener {
            val bottomSheetSendLeaves = BottomSheetSendLeaveRequests(requireContext())
            bottomSheetSendLeaves.show(childFragmentManager, "bottomSheetSendLeaves")
        }

        return view
    }

    private fun getLeaves(getLeaveOnType: String) {
        firebaseHelperClass.getSingleStudentDetails(mAuth.uid.toString(),
            object : IOnFirebaseActionCallback {
                override fun getSingleStudentDetailsCallback(studentDetails: StudentDetailsModel) {
                    firebaseHelperClass.getSingleStudentsLeaves(
                        studentDetails.branch,
                        studentDetails.batch.toString(),
                        studentDetails.sem,
                        mAuth.uid.toString(),
                        getLeaveOnType,
                        object : IOnFirebaseActionCallback {
                            override fun getSingleStudentLeaveCallback(studentLeaveList: ArrayList<StudentLeaveHelperModel>) {
                                this@StudentLeavesFragment.studentLeaveList.clear()
                                Log.d(
                                    "studentLeaveList",
                                    "getSingleStudentLeaveCallback: $studentLeaveList"
                                )
                                if (studentLeaveList.isNotEmpty()) {
                                    binding?.llNoLeavesFound?.isVisible = false
                                    this@StudentLeavesFragment.studentLeaveList = studentLeaveList

                                    val tempLeaveList = ArrayList<StudentLeaveHelperModel>()
                                    tempLeaveList.addAll(studentLeaveList)
                                    studentLeavesAdapter.studentLeaveList = tempLeaveList
                                    studentLeavesAdapter.notifyDataSetChanged()

                                } else {
                                    binding?.llNoLeavesFound?.isVisible = true
                                }
                            }
                        })
                }
            })

    }

    private fun setUpRecyclerView() {
        studentLeavesAdapter = StudentLeavesAdapter(requireContext())

        binding?.rvAllLeaves?.apply {
            adapter = studentLeavesAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        binding?.requestLeavesFloatingBtn?.shrink()
                        Handler(Looper.myLooper()!!).postDelayed({
                            binding?.requestLeavesFloatingBtn?.hide()
                        }, 500)
                    } else if (dy < 0) {
                        binding?.requestLeavesFloatingBtn?.show()
                        Handler(Looper.myLooper()!!).postDelayed({
                            binding?.requestLeavesFloatingBtn?.extend()
                        }, 500)
                    }
                }
            })
        }
    }
}