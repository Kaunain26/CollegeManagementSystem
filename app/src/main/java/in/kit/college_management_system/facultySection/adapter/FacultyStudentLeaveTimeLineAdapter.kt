package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.model.FacultySectionStudtLeaveTimeLineModel
import `in`.kit.college_management_system.model.LeaveStatus
import `in`.kit.college_management_system.utils.VectorDrawableUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView

class FacultyStudentLeaveTimeLineAdapter(
    private val timeLineList: List<FacultySectionStudtLeaveTimeLineModel>,
    var listener: IOnTimeLineClicked
) :
    RecyclerView.Adapter<FacultyStudentLeaveTimeLineAdapter.FacultyStudentLeaveTimeLineViewHolder>() {

    interface IOnTimeLineClicked {

        fun onTimeLineClicked(timeLineModel: FacultySectionStudtLeaveTimeLineModel)

    }

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FacultyStudentLeaveTimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return FacultyStudentLeaveTimeLineViewHolder(
            mLayoutInflater.inflate(
                R.layout.item_leave_time_line_faculty_section,
                parent,
                false
            ), viewType
        )
    }

    override fun onBindViewHolder(
        holderFacultyStudentLeave: FacultyStudentLeaveTimeLineViewHolder,
        position: Int
    ) {

        val timeLineModel = timeLineList[position]

        holderFacultyStudentLeave.llLeave.setOnClickListener {
            listener.onTimeLineClicked(timeLineModel)
        }

        when (timeLineModel.status) {
            LeaveStatus.AWAITING -> {
                setMarker(
                    holderFacultyStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.yellow
                )

                holderFacultyStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.yellow
                    ), holderFacultyStudentLeave.itemViewType
                )

                holderFacultyStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.yellow
                    ), holderFacultyStudentLeave.itemViewType
                )
            }
            LeaveStatus.APPROVED -> {
                setMarker(
                    holderFacultyStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.green
                )

                holderFacultyStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.green
                    ), holderFacultyStudentLeave.itemViewType
                )

                holderFacultyStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.green
                    ), holderFacultyStudentLeave.itemViewType
                )

            }
            LeaveStatus.REJECTED -> {
                setMarker(
                    holderFacultyStudentLeave,
                    R.drawable.ic_marker_approved,
                    R.color.red
                )
                holderFacultyStudentLeave.timeline.setEndLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.red
                    ), holderFacultyStudentLeave.itemViewType
                )
                holderFacultyStudentLeave.timeline.setStartLineColor(
                    ContextCompat.getColor(
                        holderFacultyStudentLeave.timeline.context,
                        R.color.red
                    ), holderFacultyStudentLeave.itemViewType
                )
            }
            else -> {
                setMarker(
                    holderFacultyStudentLeave,
                    R.drawable.ic_marker,
                    R.color.grey
                )
            }
        }

        if (timeLineModel.date.isNotEmpty()) {
            holderFacultyStudentLeave.date.isVisible = true
            holderFacultyStudentLeave.date.text = timeLineModel.date
        } else
            holderFacultyStudentLeave.date.isVisible = false

        holderFacultyStudentLeave.status.text = timeLineModel.title
    }

    private fun setMarker(
        holderFacultyStudentLeave: FacultyStudentLeaveTimeLineViewHolder,
        drawableResId: Int,
        colorFilter: Int
    ) {
        holderFacultyStudentLeave.timeline.marker = VectorDrawableUtils.getDrawable(
            holderFacultyStudentLeave.itemView.context,
            drawableResId,
            ContextCompat.getColor(holderFacultyStudentLeave.itemView.context, colorFilter)
        )
    }

    override fun getItemCount() = timeLineList.size

    inner class FacultyStudentLeaveTimeLineViewHolder(itemView: View, viewType: Int) :
        RecyclerView.ViewHolder(itemView) {

        val date = itemView.findViewById<TextView>(R.id.text_timeline_date)
        val status = itemView.findViewById<TextView>(R.id.text_timeline_title)
        val timeline = itemView.findViewById<TimelineView>(R.id.timeline)
        val llLeave = itemView.findViewById<LinearLayout>(R.id.llLeave)

        init {
            timeline.initLine(viewType)
        }
    }

}