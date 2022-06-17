package `in`.kit.college_management_system.studentSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentStudentAttendancesBinding
import `in`.kit.college_management_system.facultySection.adapter.ClassesShimmerAdapter
import `in`.kit.college_management_system.facultySection.database.DatabaseClient
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CopyOnWriteArrayList


class StudentAttendancesFragment : Fragment() {

    private var _binding: FragmentStudentAttendancesBinding? = null
    private val binding get() = _binding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mAuth: FirebaseAuth
    private var tempStudentDetails = StudentDetailsModel()
    private val classList = CopyOnWriteArrayList<ClassesModel>()
    private lateinit var studentAttendanceAdapter: StudentAttendanceAdapter
    private var databaseClient: DatabaseClient? = null
    private var selectedSem = ""
    private var selectedSemPos = 0
    private var prevSelectedSemForMenu = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentAttendancesBinding.inflate(inflater, container, false)
        val view = binding?.root

        binding!!.arrowBack.setOnClickListener {
            (activity as AppCompatActivity).finish()
        }

        binding!!.headerLoadingAnimation.startShimmerAnimation()

        mAuth = FirebaseAuth.getInstance()
        firebaseHelperClass = FirebaseHelperClass()
        firebaseHelperClass.getSingleStudentDetails(
            mAuth.uid.toString(),
            object : IOnFirebaseActionCallback {
                @SuppressLint("SetTextI18n")
                override fun getSingleStudentDetailsCallback(studentDetails: StudentDetailsModel) {
                    tempStudentDetails = studentDetails
                    binding!!.studentNameTV.text = studentDetails.name
                    //binding!!.branchNameTV.text = facultyDetails.branch
                    binding!!.greetingTV.text = "Welcome Back,"
                    // binding!!.facultyDesignationTV.text = "Assistance Professor"
                    binding!!.headerLoadingAnimation.stopShimmerAnimation()
                    binding!!.headerLoadingAnimation.visibility = View.GONE

                    (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setText(
                        studentDetails.sem
                    )
                    handleSemMenuOption()

                    setUpRecyclerView()
                    getAllClassesFromFirebase(studentDetails.sem, false)

                }
            })


        loadShimmer(activity as Context)

        //getFilterClassesFromRDB()

        //clickOnFilterClasses()


        return view
        //return inflater.inflate(R.layout.fragment_classes, container, false)
    }

//    private fun getFilterClassesFromRDB() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val allChips: List<FilterClassesChip> = getAllChips(activity as Context)
//            if (allChips.isNotEmpty()) {
//                val list = ArrayList<String>()
//                for (chipTag in allChips) {
//                    list.add(chipTag.chip)
//                }
//                filterClasses(list, activity as Context)
//
//                // we are fetching all the classes from server but not notifying the recycler view
//                // since filtered class is available
//                getAllClassesFromFirebase(notifyAdapter = false)
//            } else {
//                // we are fetching all the classes and also notifying the recycler view
//                getAllClassesFromFirebase(notifyAdapter = true)
//            }
//        }
//    }

//    private fun clickOnFilterClasses() {
//        binding?.filterChip?.setOnClickListener {
//            val bottomSheetFilterClasses =
//                BottomSheetFilterClasses(
//                    classList, tempFacultyDetails.branch,
//                    activity as Context
//                )
//            bottomSheetFilterClasses.show(
//                (activity as AppCompatActivity).supportFragmentManager,
//                "bottomSheetFilterClasses"
//            )
//            bottomSheetFilterClasses.listener = object : BottomSheetFilterClasses.OnSaveFilter {
//                override fun onFilterClass(allSelectedChip: ArrayList<String>, mContext: Context) {
//                    Log.d("allSelectedChip", "onFilterClass: $allSelectedChip")
//
//                    filterClasses(allSelectedChip, mContext)
//                    bottomSheetFilterClasses.dismiss()
//                }
//            }
//
//        }
//    }

//    private fun filterClasses(allSelectedChip: ArrayList<String>, mContext: Context) {
//        val tempList = CopyOnWriteArrayList<ClassesModel>()
//
//        binding?.filterChipGroup?.removeAllViews()
//
//        lifecycleScope.launch(Dispatchers.IO) {
//            deleteAllChipToDatabase(mContext)
//        }
//
//        if (allSelectedChip.isNotEmpty()) {
//            loadShimmer(mContext)
//        }
//
//        for (selectedChips in allSelectedChip) {
//            val chip = Chip(mContext)
//            chip.isCloseIconVisible = true
//            chip.closeIcon =
//                ContextCompat.getDrawable(mContext, R.drawable.ic_close_circular)
//            chip.text = selectedChips
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                insertChipToDatabase(chip.text.toString(), mContext)
//            }
//
//            binding?.filterChipGroup!!.addView(chip)
//
//            val batch = selectedChips.split(" ")[0]
//            val sem = selectedChips.split(" ")[2]
//            val branch = selectedChips.split(" ")[3]
//            Log.d("splitedChips", "onFilterClass: $batch , $sem , $branch")
//
//            firebaseHelperClass.filterClassesFromFirebase(
//                mAuth,
//                branch,
//                "20$batch",
//                "$sem Sem",
//                mContext,
//                object : IOnFirebaseActionCallback {
//                    @SuppressLint("NotifyDataSetChanged")
//                    override fun getFilteredClass(
//                        classModel: ClassesModel,
//                        context: Context
//                    ) {
//                        if (!tempList.contains(classModel)) {
//                            //classList.add(classModel)
//                            tempList.add(classModel)
//                            classesAdapter.submitList(tempList.toSet().toList())
//                            // classesAdapter.notifyDataSetChanged()
//                            Log.d("FilteredClass", "getFilteredClass: $tempList")
//                        }
//                        if (tempList.isNotEmpty()) {
//                            // there is a class - so hide shimmer
//                            disableShimmer(context)
//                            binding?.llNoClassesFound?.visibility = View.GONE
//                            binding?.filterTV?.visibility = View.VISIBLE
//                            binding?.filterChipScrollView?.visibility = View.VISIBLE
//                            binding?.classesTV?.visibility = View.VISIBLE
//                        } else {
//                            disableShimmer(context)
//                            binding?.llNoClassesFound?.visibility = View.VISIBLE
//                            binding?.filterTV?.visibility = View.GONE
//                            binding?.filterChipScrollView?.visibility = View.GONE
//                            binding?.classesTV?.visibility = View.GONE
//                        }
//                    }
//                }
//            )
//
//            chip.setOnCloseIconClickListener {
//                binding?.filterChipGroup!!.removeView(chip)
//                allSelectedChip.remove(chip.text.toString())
//                lifecycleScope.launch(Dispatchers.IO) {
//                    deleteOneChip(chip.text.toString(), mContext)
//                }
//                if (allSelectedChip.isNotEmpty()) {
//                    filterClasses(allSelectedChip, mContext)
//                } else {
//                    classesAdapter.submitList(classList)
//                    Handler(Looper.myLooper()!!).postDelayed({
//                        binding?.rvAllClasses?.scrollToPosition(0)
//                    }, 600)
//                }
//            }
//        }
//    }

    private fun setUpRecyclerView() {
        studentAttendanceAdapter = StudentAttendanceAdapter(activity as Context, tempStudentDetails)
        binding?.rvAllClasses?.adapter = studentAttendanceAdapter
    }

    private fun getAllClassesFromFirebase(studentSem: String, showToastForNoClass: Boolean) {
        firebaseHelperClass.getAllClassesFromFirebase(
            tempStudentDetails.branch,
            tempStudentDetails.batch,
            studentSem,
            tempStudentDetails.usn,
            object : IOnFirebaseActionCallback {
                override fun getAllClassesCallback(classModel: ClassesModel?, context: Context) {
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

    private fun disableShimmer(context: Context) {
        //disable shimmer
        binding?.shimmerFilterChips!!.stopShimmerAnimation()
        val classesShimmerAdapter = ClassesShimmerAdapter(context, false, 0)
        binding?.rvAllClassesShimmer?.adapter = classesShimmerAdapter
        binding?.llShimmerClasses?.visibility = View.GONE
        binding?.llClassesContainer?.visibility = View.VISIBLE
    }

    private fun loadShimmer(mContext: Context) {
        binding?.shimmerFilterChips!!.startShimmerAnimation()
        val classesShimmerAdapter = ClassesShimmerAdapter(mContext, true, 4)
        binding?.llShimmerClasses?.visibility = View.VISIBLE
        binding?.llClassesContainer?.visibility = View.INVISIBLE
        binding?.rvAllClassesShimmer?.adapter = classesShimmerAdapter
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
                selectedSemPos = position

                classList.clear()
                getAllClassesFromFirebase(selectedSem, true)

            }
    }


/*private suspend fun insertChipToDatabase(chip: String, mContext: Context) {
    val filterClassesChip = FilterClassesChip(System.currentTimeMillis().toString(), chip)
    DatabaseClient.getInstance(mContext)?.filterClassChipDao()?.insert(filterClassesChip)
}

private suspend fun deleteAllChipToDatabase(mContext: Context) {
    //val filterClassesChip = FilterClassesChip(System.currentTimeMillis().toString(), chip)
    DatabaseClient.getInstance(mContext)?.filterClassChipDao()?.deleteAll()
}

private suspend fun deleteOneChip(chip: String, mContext: Context) {
    //val filterClassesChip = FilterClassesChip(System.currentTimeMillis().toString(), chip)
    DatabaseClient.getInstance(mContext)?.filterClassChipDao()?.deleteOneChip(chip)
}

private suspend fun getAllChips(mContext: Context): List<FilterClassesChip> {
    return withContext(Dispatchers.IO) {
        return@withContext DatabaseClient.getInstance(mContext)?.filterClassChipDao()!!
            .getAllChip()
    }
}*/
}