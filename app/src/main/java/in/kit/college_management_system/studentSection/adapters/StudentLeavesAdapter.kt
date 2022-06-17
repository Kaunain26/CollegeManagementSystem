package `in`.kit.college_management_system.studentSection.adapters

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.model.StudentLeaveModel
import `in`.kit.college_management_system.utils.CalendarHelperClass
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class StudentLeavesAdapter() :
    ListAdapter<StudentLeaveModel, StudentLeavesAdapter.StudentLeavesViewHolder>(
        StudentLeavesCallback()
    ) {

    class StudentLeavesCallback : DiffUtil.ItemCallback<StudentLeaveModel>() {
        override fun areItemsTheSame(
            oldItem: StudentLeaveModel,
            newItem: StudentLeaveModel
        ): Boolean {
            return oldItem.leaveKey == newItem.leaveKey
        }

        override fun areContentsTheSame(
            oldItem: StudentLeaveModel,
            newItem: StudentLeaveModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        val calender = Calendar.getInstance()
        val currentDate = CalendarHelperClass().getFormattedDate(calender.time, "dd MMMM yyyy")
        CalendarHelperClass().getFormattedDate(currentList[position].leaveSentDate)
        return if (position == 0) {
            HEADER
        } else {
            ITEMS
        }
    }

    class StudentLeavesViewHolder(itemView: View, itemViewType: Int) :
        RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentLeavesViewHolder {
        return if (viewType == HEADER) StudentLeavesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.sub_header,
                parent,
                false
            ), viewType
        ) else {
            StudentLeavesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recycler_student_leaves_items,
                    parent,
                    false
                ), viewType
            )
        }
    }

    override fun onBindViewHolder(holder: StudentLeavesViewHolder, position: Int) {

    }

    companion object {
        private const val HEADER = 0
        private const val ITEMS = 0
    }
}