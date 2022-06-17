package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.databinding.BottomsheetAttendanceHistoryBinding
import `in`.kit.college_management_system.facultySection.adapter.StudentNoOfAttendancePerDayAdapter
import `in`.kit.college_management_system.model.StudentAttendanceHistoryModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.interfaces.IMarkAbsentAndPresent
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys
import `in`.kit.college_management_system.utils.FirebaseKeys.Attendances_history
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAttendanceHistory(
    private var studentAttendanceHistoryModelList: ArrayList<StudentAttendanceHistoryModel>,
    private var date: String,
    private var studentData: StudentDetailsModel,
    private var classKey: String,
    private var facultyUid: String,
    private var isStudent: Boolean
) :
    BottomSheetDialogFragment(), IMarkAbsentAndPresent {

    private var _binding: BottomsheetAttendanceHistoryBinding? = null
    private val binding get() = _binding
    private lateinit var adapter: StudentNoOfAttendancePerDayAdapter
    private var firebaseHelperClass = FirebaseHelperClass()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAttendanceHistoryBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.arrowBack?.setOnClickListener { dismiss() }
        binding?.title?.text = date

        setUpRecyclerView()

        return view
    }

    private fun setUpRecyclerView() {
        adapter = StudentNoOfAttendancePerDayAdapter(requireContext(), this, isStudent)
        binding?.rvAttendanceHistory?.adapter = adapter
        adapter.submitList(studentAttendanceHistoryModelList)
    }

    override fun onMarkAbsentOrPresent(studentPresentAbsentList: ArrayList<StudentAttendanceHistoryModel>) {
        if (studentPresentAbsentList.isNotEmpty()) {
            binding?.rlSave?.isVisible = true
            binding?.rlSave?.setOnClickListener {
                val classesRef = firebaseHelperClass.getClassDataRef(
                    studentData.branch,
                    studentData.batch,
                    studentData.sem,
                    classKey,
                    facultyUid
                )
                for (updatedAttendances in studentPresentAbsentList) {
                    val attendanceRef =
                        classesRef.child(Attendances_history)
                            .child(date)
                            .child(updatedAttendances.time)

                    val map = HashMap<String, Any>()
                    map[FirebaseKeys.UID] = studentData.uid
                    map[FirebaseKeys.USN] = studentData.usn

                    if (updatedAttendances.isPresent) {
                        attendanceRef.child(studentData.usn).setValue(map)
                    } else {
                        attendanceRef.child(studentData.usn).setValue(null)
                    }
                }
                dismiss()
            }
        } else {
            binding?.rlSave?.isVisible = false
        }
    }
}