package `in`.kit.college_management_system.studentSection.adapters

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.model.LeaveStatus
import `in`.kit.college_management_system.model.StudentLeaveTimeLineModel
import `in`.kit.college_management_system.utils.VectorDrawableUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView

class StudentLeaveTimeLineAdapter(private val mFeedList: List<StudentLeaveTimeLineModel>) :
    RecyclerView.Adapter<StudentLeaveTimeLineAdapter.StudentLeaveTimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentLeaveTimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return StudentLeaveTimeLineViewHolder(
            mLayoutInflater.inflate(
                R.layout.item_leave_time_line_std_sec,
                parent,
                false
            ), viewType
        )
    }

    override fun onBindViewHolder(
        holderStudentLeave: StudentLeaveTimeLineViewHolder,
        position: Int
    ) {

        val timeLineModel = mFeedList[position]

        when (timeLineModel.status) {
            LeaveStatus.REQUESTED -> {
                setMarker(
                    holderStudentLeave,
                    R.drawable.ic_marker_awaiting,
                    R.color.yellow
                )
                holderStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.yellow
                    ), holderStudentLeave.itemViewType
                )
                holderStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.yellow
                    ), holderStudentLeave.itemViewType
                )
            }
            LeaveStatus.AWAITING -> {
                setMarker(
                    holderStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.grey
                )

                holderStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.grey
                    ), holderStudentLeave.itemViewType
                )

                holderStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.grey
                    ), holderStudentLeave.itemViewType
                )
            }
            LeaveStatus.APPROVED -> {
                setMarker(
                    holderStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.green
                )

                holderStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.green
                    ), holderStudentLeave.itemViewType
                )

                holderStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.green
                    ), holderStudentLeave.itemViewType
                )

            }
            LeaveStatus.REJECTED -> {
                setMarker(
                    holderStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.red
                )
                holderStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.red
                    ), holderStudentLeave.itemViewType
                )
                holderStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderStudentLeave.timeline.context,
                        R.color.red
                    ), holderStudentLeave.itemViewType
                )
            }
        }

        if (timeLineModel.date.isNotEmpty()) {
            holderStudentLeave.date.isVisible = true
            holderStudentLeave.date.text = timeLineModel.date
        } else
            holderStudentLeave.date.isVisible = false

        holderStudentLeave.title.text = timeLineModel.title
        holderStudentLeave.message.text = timeLineModel.message
    }

    private fun setMarker(
        holderStudentLeave: StudentLeaveTimeLineViewHolder,
        drawableResId: Int,
        colorFilter: Int
    ) {
        holderStudentLeave.timeline.marker = VectorDrawableUtils.getDrawable(
            holderStudentLeave.itemView.context,
            drawableResId,
            ContextCompat.getColor(holderStudentLeave.itemView.context, colorFilter)
        )
    }

    override fun getItemCount() = mFeedList.size

    inner class StudentLeaveTimeLineViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {

        val date = itemView.findViewById<TextView>(R.id.text_timeline_date)
        val title = itemView.findViewById<TextView>(R.id.text_timeline_title)
        val message = itemView.findViewById<TextView>(R.id.text_timeline_des)
        val timeline = itemView.findViewById<TimelineView>(R.id.timeline)

        init {
            timeline.initLine(viewType)
        }
    }

}