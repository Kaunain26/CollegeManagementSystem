package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerNoOfAttendancePerDayItemBinding
import `in`.kit.college_management_system.facultySection.model.StudentAttendanceHistoryModel
import `in`.kit.college_management_system.interfaces.IMarkAbsentAndPresent
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class StudentNoOfAttendancePerDayAdapter(
    private var context: Context,
    private var listener: IMarkAbsentAndPresent,
    private var isStudent: Boolean
) :
    ListAdapter<StudentAttendanceHistoryModel, StudentNoOfAttendancePerDayAdapter.StudentNoOfAttendancePerDayViewHolder>(
        StudentNoOfAttendancePerDayCallback()
    ) {

    private val studentPresentAbsentList = ArrayList<StudentAttendanceHistoryModel>()

    class StudentNoOfAttendancePerDayViewHolder(var binding: RecyclerNoOfAttendancePerDayItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StudentAttendanceHistoryModel, context: Context) {
            binding.classNumber.text = item.classNumber
            binding.timeTV.text = item.time

            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled),
            )
            val presentColor = intArrayOf(
                R.color.green
            )
            val absentColor = intArrayOf(
                Color.RED,
            )
            if (item.isPresent) {
                binding.presentAbsentBtn.text = "Present"
                binding.presentAbsentBtn.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                binding.presentAbsentBtn.strokeColor = ColorStateList(states, presentColor)
                binding.presentAbsentBtn.strokeWidth = 5

            } else {
                binding.presentAbsentBtn.strokeColor = ColorStateList(states, absentColor)
                binding.presentAbsentBtn.strokeWidth = 5
                binding.presentAbsentBtn.text = "Absent"
                binding.presentAbsentBtn.setTextColor(ContextCompat.getColor(context, R.color.red))
            }

        }
    }

    class StudentNoOfAttendancePerDayCallback :
        DiffUtil.ItemCallback<StudentAttendanceHistoryModel>() {
        override fun areItemsTheSame(
            oldItem: StudentAttendanceHistoryModel,
            newItem: StudentAttendanceHistoryModel
        ): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(
            oldItem: StudentAttendanceHistoryModel,
            newItem: StudentAttendanceHistoryModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentNoOfAttendancePerDayViewHolder {
        return StudentNoOfAttendancePerDayViewHolder(
            RecyclerNoOfAttendancePerDayItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentNoOfAttendancePerDayViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, context)

        holder.binding.presentAbsentBtn.setOnClickListener {
            if (!isStudent) {
                //Student are not allowed to edit attendance
                item.isPresent = !item.isPresent
                if (!studentPresentAbsentList.contains(item)) {
                    studentPresentAbsentList.add(item)
                } else {
                    studentPresentAbsentList.remove(item)
                }
                listener.onMarkAbsentOrPresent(studentPresentAbsentList)
                notifyItemChanged(position)
            }
        }
    }
}