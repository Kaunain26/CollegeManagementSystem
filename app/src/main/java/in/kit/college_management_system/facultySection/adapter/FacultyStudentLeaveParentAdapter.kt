package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerFacultyStdLeaveSectionItemsBinding
import `in`.kit.college_management_system.model.FacultySecStudentLeaveModel
import `in`.kit.college_management_system.model.FacultySectionStudtLeaveTimeLineModel
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FacultyStudentLeaveParentAdapter(var context: Context) :
    ListAdapter<FacultySecStudentLeaveModel, FacultyStudentLeaveParentAdapter.FacultyStudentLeaveHolder>(
        FacultyStudentLeaveCallback()
    ) {

    //item callback
    class FacultyStudentLeaveCallback : DiffUtil.ItemCallback<FacultySecStudentLeaveModel>() {
        override fun areItemsTheSame(
            oldItem: FacultySecStudentLeaveModel,
            newItem: FacultySecStudentLeaveModel
        ): Boolean {
            return oldItem.stdUsn == newItem.stdUsn
        }

        override fun areContentsTheSame(
            oldItem: FacultySecStudentLeaveModel,
            newItem: FacultySecStudentLeaveModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    class FacultyStudentLeaveHolder(var binding: RecyclerFacultyStdLeaveSectionItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var timeLineAdapter: FacultyStudentLeaveTimeLineAdapter

        fun bind(item: FacultySecStudentLeaveModel, context: Context) {
            binding.leaveDay.text = "${item.no_of_days} Application"
            binding.leaveType.text = "${item.leave_type} Leave"
            binding.sentDate.text = item.leave_sent_date
            if (item.requested_to == "HOD") {
                binding.requestedTo.text = "requested to : HOD"
            } else {
                binding.requestedTo.text = "requested to : HOD -> Principal"
            }
            binding.studentName.text = item.stdName
            binding.studentUsn.text = item.stdUsn
            binding.studentSem.text = item.sem

            setProfilePicOfStd(item, context)

            val mLayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            binding.rvLeaveTimeline.layoutManager = mLayoutManager

            Log.d(
                "StdLeavesAdapter",
                "onDataChange:${item.facultySectionStudLeaveTimeLineList.toSet().toList()} "
            )
            timeLineAdapter =
                FacultyStudentLeaveTimeLineAdapter(item.facultySectionStudLeaveTimeLineList.toSet()
                    .toList().reversed(),
                    object : FacultyStudentLeaveTimeLineAdapter.IOnTimeLineClicked {
                        override fun onTimeLineClicked(timeLineModel: FacultySectionStudtLeaveTimeLineModel) {
                            Toast.makeText(
                                context,
                                "Leave request ${timeLineModel.title}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            binding.leaveDay.text = "${timeLineModel.no_of_days} Application"
                            binding.leaveType.text = "${timeLineModel.leave_type} Leave"
                            binding.sentDate.text = timeLineModel.leave_sent_date
                            if (timeLineModel.requested_to == "HOD") {
                                binding.requestedTo.text = "requested to : HOD"
                            } else {
                                binding.requestedTo.text = "requested to : HOD -> Principal"
                            }
                        }
                    })
            binding.rvLeaveTimeline.adapter = timeLineAdapter

        }

        private fun setProfilePicOfStd(item: FacultySecStudentLeaveModel, context: Context) {
            if (item.photo_url != "") {
                binding.profileImg.isVisible = true
                Log.d("setProfileOfStudent", "setProfileOfStudent: its selected")
                Glide.with(context).load(item.photo_url)
                    .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                    .into(binding.profileImg)
            } else {
                var nameInitials = ""
                SplitAndGetNameInitials().apply {
                    val splitNameList = splitName(item.stdName)
                    nameInitials = getNameInitials(splitNameList)
                }
                binding.nameInitialsTV.text = nameInitials
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyStudentLeaveHolder {
        return FacultyStudentLeaveHolder(
            RecyclerFacultyStdLeaveSectionItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FacultyStudentLeaveHolder, position: Int) {
        holder.bind(getItem(position), context)
    }
}