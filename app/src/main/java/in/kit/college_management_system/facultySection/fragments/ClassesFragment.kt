package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentFacultyClassesBinding
import `in`.kit.college_management_system.facultySection.adapter.ClassesAdapter
import `in`.kit.college_management_system.facultySection.adapter.ClassesShimmerAdapter
import `in`.kit.college_management_system.facultySection.database.DatabaseClient
import `in`.kit.college_management_system.facultySection.database.FilterClassesChip
import `in`.kit.college_management_system.facultySection.model.ClassesModel
import `in`.kit.college_management_system.facultySection.model.FacultyDetails
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.utils.AlertDialogHelperClass
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList


class ClassesFragment : Fragment() {

    private var _binding: FragmentFacultyClassesBinding? = null
    private val binding get() = _binding
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mAuth: FirebaseAuth
    private var tempFacultyDetails = FacultyDetails()
    private val classList = CopyOnWriteArrayList<ClassesModel>()
    private lateinit var classesAdapter: ClassesAdapter
    private var databaseClient: DatabaseClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFacultyClassesBinding.inflate(inflater, container, false)
        val view = binding?.root

        binding!!.arrowBack.setOnClickListener {
            (activity as AppCompatActivity).finish()
        }

        binding!!.headerLoadingAnimation.startShimmerAnimation()

        classesAdapter = ClassesAdapter(activity as Context)
        mAuth = FirebaseAuth.getInstance()
        firebaseHelperClass = FirebaseHelperClass()
        firebaseHelperClass.getFacultyDetails(
            mAuth.uid.toString(),
            object : IOnFirebaseActionCallback {
                @SuppressLint("SetTextI18n")
                override fun getAllFacultyDetailsCallback(facultyDetails: FacultyDetails) {
                    tempFacultyDetails = facultyDetails
                    binding!!.facultyNameTV.text = facultyDetails.name
                    //binding!!.branchNameTV.text = facultyDetails.branch
                    binding!!.greetingTV.text = "Hello,"
                    // binding!!.facultyDesignationTV.text = "Assistance Professor"
                    binding!!.headerLoadingAnimation.stopShimmerAnimation()
                    binding!!.headerLoadingAnimation.visibility = View.GONE

                    extractTwoCharFromName(facultyDetails.name)
                }
            })

        loadShimmer(activity as Context)
        setUpRecyclerView()

        addNewClass()

        getFilterClassesFromRDB()

        clickOnFilterClasses()

        return view
        //return inflater.inflate(R.layout.fragment_classes, container, false)
    }

    private fun getFilterClassesFromRDB() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allChips: List<FilterClassesChip> = getAllChips(activity as Context)
            if (allChips.isNotEmpty()) {
                val list = ArrayList<String>()
                for (chipTag in allChips) {
                    list.add(chipTag.chip)
                }
                filterClasses(list, activity as Context)

                // we are fetching all the classes from server but not notifying the recycler view
                // since filtered class is available
                getAllClassesFromFirebase(notifyAdapter = false)
            } else {
                // we are fetching all the classes and also notifying the recycler view
                getAllClassesFromFirebase(notifyAdapter = true)
            }
        }
    }

    private fun clickOnFilterClasses() {
        binding?.filterChip?.setOnClickListener {
            val bottomSheetFilterClasses =
                BottomSheetFilterClasses(
                    classList, tempFacultyDetails.branch,
                    activity as Context
                )
            bottomSheetFilterClasses.show(
                (activity as AppCompatActivity).supportFragmentManager,
                "bottomSheetFilterClasses"
            )
            bottomSheetFilterClasses.listener = object : BottomSheetFilterClasses.OnSaveFilter {
                override fun onFilterClass(allSelectedChip: ArrayList<String>, mContext: Context) {
                    Log.d("allSelectedChip", "onFilterClass: $allSelectedChip")

                    filterClasses(allSelectedChip, mContext)
                    bottomSheetFilterClasses.dismiss()
                }
            }

        }
    }

    private fun filterClasses(allSelectedChip: ArrayList<String>, mContext: Context) {
        val tempList = CopyOnWriteArrayList<ClassesModel>()

        binding?.filterChipGroup?.removeAllViews()

        lifecycleScope.launch(Dispatchers.IO) {
            deleteAllChipToDatabase(mContext)
        }

        if (allSelectedChip.isNotEmpty()) {
            loadShimmer(mContext)
        }

        for (selectedChips in allSelectedChip) {
            val chip = Chip(mContext)
            chip.isCloseIconVisible = true
            chip.closeIcon =
                ContextCompat.getDrawable(mContext, R.drawable.ic_close_circular)
            chip.text = selectedChips

            lifecycleScope.launch(Dispatchers.IO) {
                insertChipToDatabase(chip.text.toString(), mContext)
            }

            binding?.filterChipGroup!!.addView(chip)

            val batch = selectedChips.split(" ")[0]
            val sem = selectedChips.split(" ")[2]
            val branch = selectedChips.split(" ")[3]
            Log.d("splitedChips", "onFilterClass: $batch , $sem , $branch")

            firebaseHelperClass.filterClassesFromFirebase(
                mAuth,
                branch,
                "20$batch",
                "$sem Sem",
                mContext,
                object : IOnFirebaseActionCallback {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun getFilteredClass(
                        classModel: ClassesModel,
                        context: Context
                    ) {
                        if (!tempList.contains(classModel)) {
                            //classList.add(classModel)
                            tempList.add(classModel)
                            classesAdapter.submitList(tempList.toSet().toList())
                            // classesAdapter.notifyDataSetChanged()
                            Log.d("FilteredClass", "getFilteredClass: $tempList")
                        }
                        if (tempList.isNotEmpty()) {
                            // there is a class - so hide shimmer
                            disableShimmer(context)
                            binding?.llNoClassesFound?.visibility = View.GONE
                            binding?.filterTV?.visibility = View.VISIBLE
                            binding?.filterChipScrollView?.visibility = View.VISIBLE
                            binding?.classesTV?.visibility = View.VISIBLE
                        } else {
                            disableShimmer(context)
                            binding?.llNoClassesFound?.visibility = View.VISIBLE
                            binding?.filterTV?.visibility = View.GONE
                            binding?.filterChipScrollView?.visibility = View.GONE
                            binding?.classesTV?.visibility = View.GONE
                        }
                    }
                }
            )

            chip.setOnCloseIconClickListener {
                binding?.filterChipGroup!!.removeView(chip)
                allSelectedChip.remove(chip.text.toString())
                lifecycleScope.launch(Dispatchers.IO) {
                    deleteOneChip(chip.text.toString(), mContext)
                }
                if (allSelectedChip.isNotEmpty()) {
                    filterClasses(allSelectedChip, mContext)
                } else {
                    classesAdapter.submitList(classList)
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding?.rvAllClasses?.scrollToPosition(0)
                    }, 600)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        binding?.rvAllClasses?.adapter = classesAdapter
    }

    private fun getAllClassesFromFirebase(notifyAdapter: Boolean) {
        firebaseHelperClass.getFacultyClassFromFirebase(
            mAuth,
            tempFacultyDetails.branch,
            object : IOnFirebaseActionCallback {
                override fun getAllClassesCallback(classModel: ClassesModel?, context: Context) {
                    if (classModel != null) {
                        if (!classList.contains(classModel)) {
                            classList.add(classModel)

                            if (notifyAdapter) {
                                Toast.makeText(context, "New class detected", Toast.LENGTH_SHORT)
                                    .show()
                                Log.d("classList", "getAllClassesCallback:$classList ")
                                /** this is a hack to refresh the recycler view */
                                // making a newTempList and adding all data to it, In this way adapter refresh itself when we submit this newTempLl̥ist
                                val newTempClassList = ArrayList<ClassesModel>()
                                newTempClassList.addAll(classList)

                                classesAdapter.submitList(newTempClassList)
                            }
                        }
                    }

                    if (classList.isNotEmpty()) {
                        // there is a class - so hide shimmer
                        disableShimmer(context)
                        binding?.llNoClassesFound?.visibility = View.GONE
                        binding?.filterTV?.visibility = View.VISIBLE
                        binding?.filterChipScrollView?.visibility = View.VISIBLE
                        binding?.classesTV?.visibility = View.VISIBLE
                    } else {
                        disableShimmer(context)
                        binding?.llNoClassesFound?.visibility = View.VISIBLE
                        binding?.filterTV?.visibility = View.GONE
                        binding?.filterChipScrollView?.visibility = View.GONE
                        binding?.classesTV?.visibility = View.GONE
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

    private fun addNewClass() {
        binding?.addNewClassFab?.setOnClickListener {
            val bottomSheetCreateNewClass = BottomSheetCreateNewClass(activity as Context)
            bottomSheetCreateNewClass.show(childFragmentManager, "bottomSheetCreateNewClass")
            bottomSheetCreateNewClass.listener =
                object : BottomSheetCreateNewClass.OnAddClassListener {
                    override fun onAddClass(
                        classNameOrSubName: String,
                        subjectCode: String,
                        selectedYear: Int,
                        selectedBranchPos: Int,
                        selectedSemPos: Int,
                        selectedSem: String,
                        selectedBranch: String,
                        context: Context
                    ) {

                        AlertDialogHelperClass(context).apply {
                            this.build(
                                "Confirmation",
                                "Do you want to add new $classNameOrSubName class of $selectedBranch $selectedSem?",
                                "Yes",
                                "Not now!"
                            )
                            this.isCancelable(false)
                            this.show()
                            this.listener =
                                object : AlertDialogHelperClass.OnAlertDialogActionPerformed {
                                    override fun positiveAction(dialog: DialogInterface) {
                                        firebaseHelperClass.addClassToFirebase(
                                            classNameOrSubName,
                                            subjectCode,
                                            selectedBranch,
                                            selectedSem,
                                            selectedYear,
                                            mAuth.uid,
                                            context
                                        )
                                    }

                                    override fun negativeAction(dialog: DialogInterface) {
                                        dialog.dismiss()
                                    }
                                }
                        }

                        bottomSheetCreateNewClass.dismiss()
                    }
                }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun extractTwoCharFromName(facultyName: String) {
        val indexOfSpaceInName = facultyName.indexOf(" ")
        binding!!.initialCharOfNameTV.text =
            "${facultyName[0]}${facultyName[indexOfSpaceInName + 1]}"
    }

    private suspend fun insertChipToDatabase(chip: String, mContext: Context) {
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
    }
}