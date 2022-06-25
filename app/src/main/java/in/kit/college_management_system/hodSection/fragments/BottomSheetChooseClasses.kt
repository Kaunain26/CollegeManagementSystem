package `in`.kit.college_management_system.hodSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.BottomSheetChooseClassesBinding
import `in`.kit.college_management_system.facultySection.adapter.ClassesShimmerAdapter
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.studentSection.adapters.StudentAttendanceAdapter
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.concurrent.CopyOnWriteArrayList

class BottomSheetChooseClasses(var selectedSem: String, var studentModel: StudentDetailsModel) :
    BottomSheetDialogFragment() {

    private var _binding: BottomSheetChooseClassesBinding? = null
    private val binding get() = _binding
    private val firebaseHelperClass = FirebaseHelperClass()
    private val classList = CopyOnWriteArrayList<ClassesModel>()
    private var prevSelectedSemForMenu = ""
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChooseClassesBinding.inflate(inflater, container, false)
        val view = binding!!.root


        firebaseHelperClass.getSingleStudentDetails(
            studentModel.uid,
            object : IOnFirebaseActionCallback {
                @SuppressLint("SetTextI18n")
                override fun getSingleStudentDetailsCallback(studentDetails: StudentDetailsModel) {
                    //tempStudentDetails = studentDetails
                    binding!!.stdName.text = studentDetails.name
                    binding!!.stdUsn.text = studentDetails.usn

                    (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setText(
                        studentDetails.sem
                    )
                    handleSemMenuOption()

                    setUpRecyclerView(studentDetails)
                    getAllClassesFromFirebase(selectedSem, true)

                }
            })

        return view

    }

    private fun setUpRecyclerView(studentDetails: StudentDetailsModel) {
        studentAttendanceAdapter = StudentAttendanceAdapter(activity as Context, studentDetails)
        binding?.rvAllClasses?.adapter = studentAttendanceAdapter
    }

    private fun getAllClassesFromFirebase(studentSem: String, showToastForNoClass: Boolean) {
        firebaseHelperClass.getAllClassesFromFirebase(
            studentModel.branch,
            studentModel.batch,
            studentSem,
            studentModel.usn,
            object : IOnFirebaseActionCallback {
                override fun getFacultyAllClassesCallback(
                    classModel: ClassesModel?,
                    context: Context
                ) {
                    if (classModel != null) {
                        if (!classList.contains(classModel)) {
                            prevSelectedSemForMenu = studentSem
                            classList.add(classModel)

                            //if (notifyAdapter) {
                            Log.d("classList", "getAllClassesCallback:$classList ")
                            /** this is a hack to refresh the recycler view */
                            // making a newTempList and adding all data to it, In this way adapter refresh itself when we submit this newTempLl̥ist
                            val newTempClassList = ArrayList<ClassesModel>()
                            newTempClassList.addAll(classList)

                            studentAttendanceAdapter.submitList(newTempClassList)
                            //}
                        }
                    }

                    if (classList.isNotEmpty()) {
                        // there is a class - so hide shimmer
                        disableShimmer(context)
                        binding?.llNoClassesFound?.visibility = View.GONE
                        binding?.filterTV?.visibility = View.VISIBLE
                        binding?.filterClassesBySem?.visibility = View.VISIBLE
                        binding?.classesTV?.visibility = View.VISIBLE
                    } else {
                        if (showToastForNoClass) {
                            Toast.makeText(
                                context,
                                "No Classes found in $studentSem",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding?.showNote?.text = "No classes found in $studentSem "
                            Log.d(
                                "prevSelectedSem",
                                "getAllClassesCallback:$prevSelectedSemForMenu "
                            )
                            if (prevSelectedSemForMenu != "") {
                                (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setText(
                                    prevSelectedSemForMenu
                                )
                                (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.clearFocus()
                                handleSemMenuOption()
                            }
                        } else {
                            disableShimmer(context)
                            binding?.llNoClassesFound?.visibility = View.VISIBLE
                            binding?.filterTV?.visibility = View.GONE
                            binding?.filterClassesBySem?.visibility = View.GONE
                            binding?.classesTV?.visibility = View.GONE
                        }
                    }
                }

            }, activity as Context
        )
    }

    private fun handleSemMenuOption() {
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
        val semListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, semList)
        (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            semListArrayAdapter
        )
        (binding?.chooseSemesterMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->

                selectedSem = p0.getItemAtPosition(position).toString()
                //selectedSemPos = position

                classList.clear()
                getAllClassesFromFirebase(selectedSem, true)

            }
    }

    private fun disableShimmer(context: Context) {
        //disable shimmer
        binding?.shimmerFilterChips!!.stopShimmerAnimation()
        val classesShimmerAdapter = ClassesShimmerAdapter(context, false, 0)
        binding?.rvAllClassesShimmer?.adapter = classesShimmerAdapter
        binding?.llShimmerClasses?.visibility = View.GONE
        binding?.llClassesContainer?.visibility = View.VISIBLE
    }
}