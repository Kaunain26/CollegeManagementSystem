package `in`.kit.college_management_system.studentSection.adapters

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerAllClassesItemsForStdBinding
import `in`.kit.college_management_system.facultySection.activity.StudentAttendanceDetailsActivity
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.FacultyDetails
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.utils.Constants
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class StudentAttendanceAdapter(
    private var context: Context,
    private var studentDetailsModel: StudentDetailsModel
) :
    ListAdapter<ClassesModel, StudentAttendanceAdapter.StudentAttendanceViewHolder>(
        AllClassesCallback()
    ) {
    class StudentAttendanceViewHolder(var binding: RecyclerAllClassesItemsForStdBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(
            classData: ClassesModel,
            context: Context,
            studentDetailsModel: StudentDetailsModel
        ) {
            binding.classNameTV.text = classData.className
            binding.subjectCodeTV.text = classData.subjectCode
            val batch =
                classData.batchOrYear[classData.batchOrYear.length - 2].toString() + classData.batchOrYear[classData.batchOrYear.length - 1].toString()
            binding.batchAndsemTv.text = "$batch - ${classData.sem}"

            if (classData.totalAttendancePercentage.toInt() < 85) {
                binding.totalStudentPercentage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
                binding.totalStudentPercentage.text = classData.totalAttendancePercentage
            } else if (classData.totalAttendancePercentage.toInt() >= 85) {
                binding.totalStudentPercentage.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.dark_grey
                    )
                )
            }
            binding.totalStudentPercentage.text = classData.totalAttendancePercentage


            FirebaseHelperClass().getFacultyDetails(classData.facultyUid,
                object : IOnFirebaseActionCallback {
                    override fun getAllFacultyDetailsCallback(facultyDetails: FacultyDetails) {
                        binding.facultyNameTV.text = "${facultyDetails.name}'s"
                    }
                })
            //binding.batchTV.text = "${batch}th Batch"

            binding.root.setOnClickListener {
                val gson = Gson()
                val studentDataModel: String = gson.toJson(studentDetailsModel)
                context.startActivity(
                    Intent(
                        context,
                        StudentAttendanceDetailsActivity::class.java
                    ).apply {
                        putExtra(Constants.STUDENT_DATA_MODEL, studentDataModel)
                        putExtra(Constants.CLASS_KEY, classData.classSubKey)
                        putExtra(Constants.CLASS_SEM, classData.sem)
                        putExtra(Constants.FACULTY_UID, classData.facultyUid)
                        putExtra(Constants.IS_STUDENT, true)
                    })
            }
        }

    }

    class AllClassesCallback : DiffUtil.ItemCallback<ClassesModel>() {
        override fun areItemsTheSame(oldItem: ClassesModel, newItem: ClassesModel): Boolean {
            return oldItem.classSubKey == newItem.classSubKey
        }

        override fun areContentsTheSame(oldItem: ClassesModel, newItem: ClassesModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceViewHolder {
        return StudentAttendanceViewHolder(
            RecyclerAllClassesItemsForStdBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentAttendanceViewHolder, position: Int) {
        holder.bind(getItem(position), context, studentDetailsModel)
    }

}