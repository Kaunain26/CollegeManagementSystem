package `in`.kit.college_management_system.studentSection.activity

import `in`.kit.college_management_system.databinding.ActivityLeaveDetailsBinding
import `in`.kit.college_management_system.model.LeaveStatus
import `in`.kit.college_management_system.model.StudentLeaveTimeLineModel
import `in`.kit.college_management_system.studentSection.adapters.StudentLeaveTimeLineAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentLeaveDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveDetailsBinding
    private lateinit var mAdapter: StudentLeaveTimeLineAdapter
    private val mDataList = ArrayList<StudentLeaveTimeLineModel>()
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDataList.add(StudentLeaveTimeLineModel("Leave requested", "", LeaveStatus.AWAITING))
        mDataList.add(
            StudentLeaveTimeLineModel(
                "HOD Approved your leave application.",
                "2017-02-12 08:00",
                LeaveStatus.APPROVED
            )
        )
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvLeaveTimeline.layoutManager = mLayoutManager
        mAdapter = StudentLeaveTimeLineAdapter(mDataList)
        binding.rvLeaveTimeline.adapter = mAdapter
    }
}