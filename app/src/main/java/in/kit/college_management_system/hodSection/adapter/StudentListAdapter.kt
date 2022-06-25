package `in`.kit.college_management_system.hodSection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ChooseClassAndSemBinding
import `in`.kit.college_management_system.databinding.RecyclerStudentsV2Binding
import `in`.kit.college_management_system.hodSection.fragments.BottomSheetChooseClasses
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.CopyOnWriteArrayList

class StudentListAdapter(
    var context: Context,
    //var classData: ClassesModel,
    //var listener: IOnClickAndLongClickListener
) :
    ListAdapter<StudentDetailsModel, StudentListAdapter.StudentListViewHolder>(
        StudentsCallback()
    ) {

    var selectionModeEnabled = false
    var selectedStudentList = CopyOnWriteArrayList<StudentDetailsModel>()

    class StudentListViewHolder(var binding: RecyclerStudentsV2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(
            studentDetailsModel: StudentDetailsModel,
            context: Context
        ) {
            binding.studentNameTV.text = studentDetailsModel.name
            binding.usnTv.text = studentDetailsModel.usn

            binding.flExpandedView.isVisible = studentDetailsModel.isExpanded
            if (binding.flExpandedView.isVisible) {
                binding.stdDetailsCard.elevation = 20f
            } else {
                binding.stdDetailsCard.elevation = 0f
            }

            setProfileOfStudent(studentDetailsModel, context)


            binding.viewAttendanceBtn.setOnClickListener {
                val semList = listOf(
                    "1st Sem",
                    "2nd Sem",
                    "3rd Sem",
                    "4th Sem",
                    "5th Sem",
                    "6th Sem",
                    "7th Sem",
                    "8th Sem"
                )


                val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
                val dialogBinding =
                    ChooseClassAndSemBinding.inflate(LayoutInflater.from(context), null, false)

                var selectedSem = ""
                val semListArrayAdapter =
                    ArrayAdapter(context, R.layout.choose_item_layout, semList)
                (dialogBinding.chooseSemesterMenu.editText as? AutoCompleteTextView)?.setAdapter(
                    semListArrayAdapter
                )
                (dialogBinding.chooseSemesterMenu.editText as? AutoCompleteTextView)?.onItemClickListener =
                    AdapterView.OnItemClickListener { p0, p1, position, p2 ->

                        selectedSem = p0.getItemAtPosition(position).toString()

                        //selectedSemPos = position

                        //classList.clear()
                        //getAllClassesFromFirebase(selectedSem, true)

                    }
                materialAlertDialogBuilder.setView(dialogBinding.root)
                materialAlertDialogBuilder.setTitle("Choose")
                materialAlertDialogBuilder.setPositiveButton("Show") { dialog, _ ->

                    if (selectedSem != "") {
                        val bottomSheetChooseClasses =
                            BottomSheetChooseClasses(selectedSem, studentDetailsModel)
                        bottomSheetChooseClasses.show(
                            (context as AppCompatActivity).supportFragmentManager,
                            "bottomSheetChooseClasses"
                        )
                    } else {
                        Toast.makeText(context, "Please select semester.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    dialog.dismiss()
                }
                materialAlertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                materialAlertDialogBuilder.show()
            }

            //open student profile
            binding.cardProfile.setOnClickListener {
                /*
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
                * */
            }
        }


        private fun setProfileOfStudent(
            studentDetailsModel: StudentDetailsModel,
            context: Context
        ) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        return StudentListViewHolder(
            RecyclerStudentsV2Binding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentListViewHolder, position: Int) {
        val studentDetailsModel = getItem(position)

        holder.bind(studentDetailsModel, context)

        holder.binding.rlExtendCard.setOnClickListener {
            studentDetailsModel.isExpanded = !studentDetailsModel.isExpanded
            notifyItemChanged(position)
        }

        holder.binding.stdDetailsCard.setOnClickListener {

        }

    }

//    fun resetTheStateOfView() {
//        selectionModeEnabled = false
//        for (data in currentList) {
//            data.isSelected = false
//            data.isExpanded = false
//        }
//        selectedStudentList.clear()
//        notifyItemRangeChanged(0, currentList.size)
//    }

//    fun checkAll() {
//        selectedStudentList.clear()
//        selectionModeEnabled = true
//        for (data in currentList) {
//            data.isSelected = true
//            selectedStudentList.add(data)
//        }
//        listener.onStudentSelected(selectedStudentList, currentList.size)
//        notifyItemRangeChanged(0, currentList.size)
//    }
}