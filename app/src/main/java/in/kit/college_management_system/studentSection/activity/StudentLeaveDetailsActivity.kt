package `in`.kit.college_management_system.studentSection.activity

import `in`.kit.college_management_system.databinding.ActivityLeaveDetailsBinding
import `in`.kit.college_management_system.model.LeaveStatus
import `in`.kit.college_management_system.model.StudentLeaveHelperModel
import `in`.kit.college_management_system.model.StudentLeaveTimeLineModel
import `in`.kit.college_management_system.studentSection.adapters.StudentLeaveTimeLineAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StudentLeaveDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaveDetailsBinding
    private lateinit var mAdapter: StudentLeaveTimeLineAdapter
    private val mDataList = ArrayList<StudentLeaveTimeLineModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var studentLeaveModel: StudentLeaveHelperModel.StudentLeaveModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaveDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val studentLeaveModelGson = intent?.getStringExtra("studentLeaveModel")!!
        val gson = Gson()
        studentLeaveModel =
            gson.fromJson(
                studentLeaveModelGson,
                object : TypeToken<StudentLeaveHelperModel.StudentLeaveModel>() {}.type
            )

        binding.title.text = "${studentLeaveModel.no_of_days} Application"

        if (studentLeaveModel.requested_to == "HOD") {
            when (studentLeaveModel.is_hod_permission_granted) {
                1 -> {
                    mDataList.add(
                        StudentLeaveTimeLineModel(
                            "Approved",
                            "",
                            LeaveStatus.APPROVED,
                            "HOD Approved your leave application."
                        )
                    )
                }
                -1 -> {
                    mDataList.add(
                        StudentLeaveTimeLineModel(
                            "Rejected",
                            "",
                            LeaveStatus.REJECTED,
                            "HOD Rejected your leave application."
                        )
                    )
                }
                0 -> {
                    mDataList.add(
                        StudentLeaveTimeLineModel(
                            "Awaiting",
                            "",
                            LeaveStatus.AWAITING,
                            "Waiting for any response from HOD"
                        )
                    )
                }
            }
            mDataList.add(
                StudentLeaveTimeLineModel(
                    "Leave requested",
                    studentLeaveModel.leave_sent_date,
                    LeaveStatus.REQUESTED,
                    "This leave had been requested to HOD"
                )
            )
        } else if (studentLeaveModel.requested_to == "Principal_HOD") {

            if (studentLeaveModel.is_hod_permission_granted == 1) {
                when (studentLeaveModel.is_principal_permission_granted) {
                    1 -> {
                        // Approved
                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Approved",
                                "",
                                LeaveStatus.APPROVED,
                                "Your leave application has been approved by Principal."
                            )
                        )

                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Approved",
                                "",
                                LeaveStatus.APPROVED,
                                "HOD approved your leave application."
                            )
                        )
                    }
                    0 -> {
                        //Hod granted...Waiting for principal
                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Awaiting",
                                "",
                                LeaveStatus.AWAITING,
                                "Waiting for approval from Principal"
                            )
                        )

                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Approved",
                                "",
                                LeaveStatus.APPROVED,
                                "HOD approved your leave application."
                            )
                        )
                    }
                    -1 -> {
                        //Hod granted and principal rejected
                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Rejected",
                                "",
                                LeaveStatus.REJECTED,
                                "Your leave application has been rejected by Principal."
                            )
                        )
                        mDataList.add(
                            StudentLeaveTimeLineModel(
                                "Approved",
                                "",
                                LeaveStatus.APPROVED,
                                "HOD approved your leave application."
                            )
                        )
                    }
                }

            } else if (studentLeaveModel.is_hod_permission_granted == -1) {
                //rejected
                mDataList.add(
                    StudentLeaveTimeLineModel(
                        "Rejected",
                        "",
                        LeaveStatus.REJECTED,
                        "HOD rejected your leave application."
                    )
                )
            } else if (studentLeaveModel.is_hod_permission_granted == 0) {
                if (studentLeaveModel.is_principal_permission_granted == 0) {
                    //Awaiting
                    mDataList.add(
                        StudentLeaveTimeLineModel(
                            "Awaiting",
                            "",
                            LeaveStatus.AWAITING,
                            "Waiting for approval from HOD"
                        )
                    )
                }
            }
            mDataList.add(
                StudentLeaveTimeLineModel(
                    "Leave requested",
                    studentLeaveModel.leave_sent_date,
                    LeaveStatus.REQUESTED,
                    "This leave had been requested to HOD and Principal"
                )
            )
        }

        setViews()
        initRecyclerView()
    }

    private fun setViews() {
        binding.leaveType.text = studentLeaveModel.leave_type
        binding.leaveDesc.text = studentLeaveModel.leave_reason
        if (studentLeaveModel.from_date != "") {
            binding.leaveDates.text =
                "From ${studentLeaveModel.from_date} - To ${studentLeaveModel.to_date}"
        } else {
            binding.leaveDates.text = "${studentLeaveModel.no_of_days} Application"
        }
    }

    private fun initRecyclerView() {
        mLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvLeaveTimeline.layoutManager = mLayoutManager
        mAdapter = StudentLeaveTimeLineAdapter(mDataList)
        binding.rvLeaveTimeline.adapter = mAdapter
    }
}