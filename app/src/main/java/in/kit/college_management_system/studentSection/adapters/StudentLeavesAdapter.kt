package `in`.kit.college_management_system.studentSection.adapters

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerStudentLeavesItemsBinding
import `in`.kit.college_management_system.databinding.SubHeaderBinding
import `in`.kit.college_management_system.model.StudentLeaveHelperModel
import `in`.kit.college_management_system.studentSection.viewHolder.StudentLeavesHelperViewHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class StudentLeavesAdapter(
    private var context: Context,
) :
    RecyclerView.Adapter<StudentLeavesHelperViewHolder>() {

    var studentLeaveList = listOf<StudentLeaveHelperModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentLeavesHelperViewHolder {

        return when (viewType) {
            R.layout.sub_header -> {
                StudentLeavesHelperViewHolder.StudentLeaveSubHeaderHolder(
                    SubHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            R.layout.recycler_student_leaves_items -> {
                StudentLeavesHelperViewHolder.StudentLeaveHolder(
                    RecyclerStudentLeavesItemsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), context
                )
            }
            else -> throw IllegalArgumentException("Invalid ViewType Provider")
        }
    }

    override fun onBindViewHolder(holder: StudentLeavesHelperViewHolder, position: Int) {
        when (holder) {
            is StudentLeavesHelperViewHolder.StudentLeaveHolder -> holder.bind(studentLeaveList[position] as StudentLeaveHelperModel.StudentLeaveModel)
            is StudentLeavesHelperViewHolder.StudentLeaveSubHeaderHolder -> holder.bind(
                studentLeaveList[position] as StudentLeaveHelperModel.SubHeader
            )
        }
    }

    override fun getItemCount() = studentLeaveList.size

    override fun getItemViewType(position: Int): Int {
        return when (studentLeaveList[position]) {
            is StudentLeaveHelperModel.SubHeader -> R.layout.sub_header

            is StudentLeaveHelperModel.StudentLeaveModel -> R.layout.recycler_student_leaves_items
        }

    }


}