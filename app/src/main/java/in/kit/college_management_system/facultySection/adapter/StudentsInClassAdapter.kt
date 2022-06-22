package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerStudentsBinding
import `in`.kit.college_management_system.facultySection.activity.StudentAttendanceDetailsActivity
import `in`.kit.college_management_system.interfaces.IOnClickAndLongClickListener
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.utils.Constants.CLASS_KEY
import `in`.kit.college_management_system.utils.Constants.CLASS_SEM
import `in`.kit.college_management_system.utils.Constants.FACULTY_UID
import `in`.kit.college_management_system.utils.Constants.IS_FACULTY
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.util.concurrent.CopyOnWriteArrayList

class StudentsInClassAdapter(
    var context: Context,
    var classData: ClassesModel,
    var listener: IOnClickAndLongClickListener
) :
    ListAdapter<StudentDetailsModel, StudentsInClassAdapter.StudentsInClassViewHolder>(
        StudentsCallback()
    ) {

    var selectionModeEnabled = false
    var selectedStudentList = CopyOnWriteArrayList<StudentDetailsModel>()

    class StudentsInClassViewHolder(var binding: RecyclerStudentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            studentDetailsModel: StudentDetailsModel,
            context: Context,
            selectionModeEnabled: Boolean,
            classData: ClassesModel
        ) {
            binding.studentNameTV.text = studentDetailsModel.name
            binding.usnTv.text = studentDetailsModel.usn

            Log.d("abcd", "bind: ${studentDetailsModel.isSelected}")

            if (selectionModeEnabled) {
                //if selection mode enabled
                binding.flExpandedView.isVisible = false
                binding.rlForwardArrow.isVisible = false
                binding.profileImg.isVisible = true

                Log.d("StudentAdapter", "bind: ${studentDetailsModel.isSelected}")
                binding.nameInitialsTV.isVisible = !studentDetailsModel.isSelected
                binding.profileImg.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_check_circle)
            } else {
                // if selection model is not enabled then clicking on a card will expand the view
                // Log.d("StudentAdapter", "bind: ${studentDetailsModel.isExpanded}")

                binding.rlForwardArrow.isVisible = true
                binding.flExpandedView.isVisible = studentDetailsModel.isExpanded
                if (binding.flExpandedView.isVisible) {
                    binding.stdDetailsCard.elevation = 20f
                } else {
                    binding.stdDetailsCard.elevation = 0f
                }

            }

            setProfileOfStudent(studentDetailsModel, context)

            FirebaseHelperClass().getStudentPresentAndAbsentData(
                studentDetailsModel.branch,
                studentDetailsModel.batch,
                classData.sem,
                classData.classSubKey,
                FirebaseAuth.getInstance().uid.toString(),
                object : IOnFirebaseActionCallback {
                    override fun getStudentPresentAndAbsentData(
                        studentPresentMap: HashMap<String, Int>?,
                        totalAttendanceValue: String
                    ) {
                        super.getStudentPresentAndAbsentData(
                            studentPresentMap,
                            totalAttendanceValue
                        )
                        Log.d(
                            "getStudentPresentAndAbsentData",
                            "getStudentPresentAndAbsentData: $studentPresentMap ,${studentDetailsModel.sem}"
                        )
                        if (studentPresentMap?.get(studentDetailsModel.usn) != null) {
                            val absentDays =
                                totalAttendanceValue.toInt() - studentPresentMap[studentDetailsModel.usn].toString()
                                    .toInt()
                            val presentDays =
                                studentPresentMap[studentDetailsModel.usn].toString()

                            binding.presentDays.text = "$presentDays days"
                            binding.absentDays.text = "$absentDays days"
                        } else {
                            val presentDays = 0
                            binding.presentDays.text = "$presentDays day"
                            val absentDays = totalAttendanceValue.toInt()
                            binding.absentDays.text = "$absentDays days"
                        }

                    }
                })

            //open student profile
            binding.cardProfile.setOnClickListener {
                if (!selectionModeEnabled) {
                    val gson = Gson()
                    val studentDataModel: String = gson.toJson(studentDetailsModel)
                    context.startActivity(
                        Intent(
                            context,
                            StudentAttendanceDetailsActivity::class.java
                        ).apply {
                            putExtra("studentDataModel", studentDataModel)
                            putExtra(CLASS_KEY, classData.classSubKey)
                            putExtra(CLASS_SEM, classData.sem)
                            putExtra(FACULTY_UID, classData.facultyUid)
                            putExtra(IS_FACULTY, true)
                        })
                }
            }

        }

        private fun setProfileOfStudent(
            studentDetailsModel: StudentDetailsModel,
            context: Context
        ) {

            if (studentDetailsModel.isSelected) {
                Log.d("setProfileOfStudent", "setProfileOfStudent: its selected")
                binding.profileImg.isVisible = true
                binding.profileImg.setImageResource(R.drawable.ic_check_circle)
            } else {
                if (studentDetailsModel.photo_url != "") {
                    binding.profileImg.isVisible = true
                    Log.d("setProfileOfStudent", "setProfileOfStudent: its selected")
                    Glide.with(context).load(studentDetailsModel.photo_url)
                        .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                        .into(binding.profileImg)
                } else {
                    binding.nameInitialsTV.isVisible = !studentDetailsModel.isSelected
                    binding.profileImg.isVisible = false
                    var nameInitials = ""
                    SplitAndGetNameInitials().apply {
                        val splitNameList = splitName(studentDetailsModel.name)
                        nameInitials = getNameInitials(splitNameList)
                    }
                    binding.nameInitialsTV.text = nameInitials
                }
            }
        }
    }

    class StudentsCallback : DiffUtil.ItemCallback<StudentDetailsModel>() {
        override fun areItemsTheSame(
            oldItem: StudentDetailsModel,
            newItem: StudentDetailsModel
        ): Boolean {
            return oldItem.usn == newItem.usn
        }

        override fun areContentsTheSame(
            oldItem: StudentDetailsModel,
            newItem: StudentDetailsModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsInClassViewHolder {
        return StudentsInClassViewHolder(
            RecyclerStudentsBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentsInClassViewHolder, position: Int) {
        val studentDetailsModel = getItem(position)

        holder.bind(studentDetailsModel, context, selectionModeEnabled, classData)

        if (!selectionModeEnabled) {
            holder.binding.rlForwardArrow.setOnClickListener {
                studentDetailsModel.isExpanded = !studentDetailsModel.isExpanded
                notifyItemChanged(position)
            }
        }

        holder.binding.stdDetailsCard.setOnLongClickListener {
            selectionModeEnabled = true
            studentDetailsModel.isSelected = !studentDetailsModel.isSelected

            if (studentDetailsModel.isSelected) {
                selectedStudentList.add(studentDetailsModel)
            } else {
                selectedStudentList.remove(studentDetailsModel)
                if (selectedStudentList.isEmpty()) {
                    selectionModeEnabled = false
                    notifyDataSetChanged()
                }
            }
            listener.onStudentSelected(selectedStudentList, currentList.size)
            //notifyItemChanged(position)
            notifyDataSetChanged()

            return@setOnLongClickListener true
        }


        holder.binding.stdDetailsCard.setOnClickListener {
            if (selectionModeEnabled) {
                studentDetailsModel.isSelected = !studentDetailsModel.isSelected
                if (studentDetailsModel.isSelected) {
                    selectedStudentList.add(studentDetailsModel)
                } else {
                    selectedStudentList.remove(studentDetailsModel)
                    if (selectedStudentList.isEmpty()) {
                        selectionModeEnabled = false
                        notifyDataSetChanged()
                    }
                }
                listener.onStudentSelected(selectedStudentList, currentList.size)
                //notifyItemChanged(position)
                notifyDataSetChanged()
            }
        }

    }

    fun resetTheStateOfView() {
        selectionModeEnabled = false
        for (data in currentList) {
            data.isSelected = false
            data.isExpanded = false
        }
        selectedStudentList.clear()
        notifyItemRangeChanged(0, currentList.size)
    }

    fun checkAll() {
        selectedStudentList.clear()
        selectionModeEnabled = true
        for (data in currentList) {
            data.isSelected = true
            selectedStudentList.add(data)
        }
        listener.onStudentSelected(selectedStudentList, currentList.size)
        notifyItemRangeChanged(0, currentList.size)
    }
}