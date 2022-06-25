package `in`.kit.college_management_system.hodSection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivityStudentListBranchWiseBinding
import `in`.kit.college_management_system.hodSection.adapter.StudentListAdapter
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys.BRANCH
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.concurrent.CopyOnWriteArrayList

class StudentListBranchWiseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentListBranchWiseBinding
    private var branch: String = ""
    private val firebaseHelperClass = FirebaseHelperClass()
    private lateinit var adapter: StudentListAdapter
    private var studentDetailsList = CopyOnWriteArrayList<StudentDetailsModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentListBranchWiseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        branch = intent?.getStringExtra(BRANCH).toString()

        setUpRecyclerView()
        getStudents(false)
    }

    private fun setUpRecyclerView() {
        adapter = StudentListAdapter(this)
        binding.rvStudents.adapter = adapter
    }

    private fun getStudents(takeOneBatch: Boolean) {
        firebaseHelperClass.getStudentsAccordingToBatch(
            branch,
            this,
            object : IOnFirebaseActionCallback {
                override fun getStudentDetails(
                    studentDetailsList: CopyOnWriteArrayList<StudentDetailsModel>,
                    context: Context,
                    batchList: ArrayList<String>
                ) {
                    if (studentDetailsList.isNotEmpty() && batchList.isNotEmpty()) {
                        /* binding.rvStudents.isVisible = true
                         binding.llLottieChoose.isVisible = false
                         binding.filterTV.isVisible = true*/

                        this@StudentListBranchWiseActivity.studentDetailsList = studentDetailsList

                        handelDropDownMenu(batchList)

                    } else {
                        binding.rvStudents.isVisible = false
                        binding.llLottieChoose.isVisible = true
                        binding.filterTV.isVisible = false
                    }
                }
            }
        )
    }

    private fun handelDropDownMenu(batchList: ArrayList<String>) {
        val semListArrayAdapter =
            ArrayAdapter(this, R.layout.choose_item_layout, batchList)
        (binding.chooseBatchMenu.editText as? AutoCompleteTextView)?.setAdapter(
            semListArrayAdapter
        )
        (binding.chooseBatchMenu.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->

                val selectedBatch = p0.getItemAtPosition(position).toString()
                // selectedSemPos = position

                val tempStdList = ArrayList<StudentDetailsModel>()
                for (studentModel in studentDetailsList) {
                    if (studentModel.batch.toString() == selectedBatch) {
                        tempStdList.add(studentModel)
                    }
                }
                if (tempStdList.isNotEmpty()) {
                    //binding.rvStudents.isVisible = true
                    binding.filterTV.isVisible = true
                    adapter.submitList(tempStdList)

                    binding.llLottieChoose.animate()
                        .translationY(-binding.llLottieChoose.height.toFloat()).alpha(0f)
                        .setDuration(400).setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationStart(animation: Animator?) {
                                super.onAnimationStart(animation)
                                binding.rvStudents.isVisible = true
                                binding.rvStudents.animate()
                                    .translationY(binding.rvStudents.height.toFloat())
                                    .alpha(1f)
                                    .setDuration(1000)
                                    .setListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            super.onAnimationEnd(animation)
                                        }
                                    })

                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                super.onAnimationEnd(animation)
                                binding.llLottieChoose.isVisible = false
                            }
                        })

                } else {
                    binding.rvStudents.isVisible = false
                    binding.llLottieChoose.isVisible = true
                    binding.filterTV.isVisible = false
                }
            }
    }
}