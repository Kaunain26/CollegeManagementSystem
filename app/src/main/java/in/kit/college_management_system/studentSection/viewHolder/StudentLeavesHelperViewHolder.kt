package `in`.kit.college_management_system.studentSection.viewHolder

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerStudentLeavesItemsBinding
import `in`.kit.college_management_system.databinding.SubHeaderBinding
import `in`.kit.college_management_system.model.StudentLeaveHelperModel
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

sealed class StudentLeavesHelperViewHolder(binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    class StudentLeaveHolder(
        private val binding: RecyclerStudentLeavesItemsBinding,
        private var context: Context
    ) :
        StudentLeavesHelperViewHolder(binding) {

        fun bind(item: StudentLeaveHelperModel.StudentLeaveModel) {
            binding.leaveType.text = item.leave_type
            binding.leaveDay.text = "${item.no_of_days} Application"
            if (item.no_of_days == "Half Day" || item.no_of_days == "Full Day") {
                binding.leaveDate.text = item.leave_sent_date
            } else {
                binding.leaveDate.text = "${item.from_date} - ${item.to_date}"
            }
            if (item.leave_type == "Sick") {
                binding.leaveType.setTextColor(ContextCompat.getColor(context, R.color.purple))
            } else {
                binding.leaveType.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }
            Log.d("ItemData", "bind:requested_to ${item.requested_to } , ${item.is_hod_permission_granted}, ${item.is_principal_permission_granted}")
            if (item.requested_to == "HOD") {
                Log.d("requestedTo", "onBindViewHolder:${item.is_hod_permission_granted} ")
                when (item.is_hod_permission_granted) {
                    0 -> {
                        showAwaitingStatus()
                    }
                    1 -> {
                        showApprovedStatus()
                    }
                    -1 -> {
                        showRejectedStatus()
                    }
                }
            } else if (item.requested_to == "Principal_HOD") {
                if (item.is_hod_permission_granted == 1 && item.is_principal_permission_granted == 1) {
                    //approved
                    showApprovedStatus()
                } else if (item.is_hod_permission_granted == -1) {
                    // Rejected by Hod
                    showRejectedStatus()
                } else if (item.is_hod_permission_granted == 1 && item.is_principal_permission_granted == 0) {
                    //Awaiting for principal
                    showAwaitingStatus()
                } else if (item.is_hod_permission_granted == 1 && item.is_principal_permission_granted == -1) {
                    //Rejected by principal
                    showRejectedStatus()
                }else{
                    //Awaiting
                    showAwaitingStatus()
                }
            }
        }

        private fun showAwaitingStatus() {
            binding.leaveStatus.text = "Awaiting"
            binding.leaveStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.dark_yellow
                )
            )
            binding.leaveStatus.background =
                ContextCompat.getDrawable(context, R.drawable.curved_yellow)
        }

        private fun showRejectedStatus() {
            binding.leaveStatus.text = "Rejected"
            binding.leaveStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.red
                )
            )
            binding.leaveStatus.background =
                ContextCompat.getDrawable(context, R.drawable.curved_red)
        }

        private fun showApprovedStatus() {
            binding.leaveStatus.text = "Approved"
            binding.leaveStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.parrot_green
                )
            )
            binding.leaveStatus.background =
                ContextCompat.getDrawable(context, R.drawable.curved_green)
        }
    }

    class StudentLeaveSubHeaderHolder(private val binding: SubHeaderBinding) :
        StudentLeavesHelperViewHolder(binding) {
        fun bind(item: StudentLeaveHelperModel.SubHeader) {
            binding.titleHeader.text = item.title
        }
    }
}
