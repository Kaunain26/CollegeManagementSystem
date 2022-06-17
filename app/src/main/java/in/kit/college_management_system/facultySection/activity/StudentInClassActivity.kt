package `in`.kit.college_management_system.facultySection.activity

import `in`.kit.college_management_system.databinding.ActivityStudentInClassBinding
import `in`.kit.college_management_system.facultySection.adapter.StudentsInClassAdapter
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.interfaces.IOnClickAndLongClickListener
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.utils.CalendarHelperClass
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import `in`.kit.college_management_system.utils.FirebaseKeys
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.CopyOnWriteArrayList

class StudentInClassActivity : AppCompatActivity(), IOnClickAndLongClickListener {
    private lateinit var binding: ActivityStudentInClassBinding
    private var branch = ""
    private var batchOrYear = 0
    private var className = ""
    private var batchAndSem = ""
    private var subCode = ""
    private var key = ""
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mRef: DatabaseReference
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private var studentDetailsList = CopyOnWriteArrayList<StudentDetailsModel>()
    private var adapter: StudentsInClassAdapter? = null
    private lateinit var classData: ClassesModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentInClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MakeStatusBarTransparent().transparent(this)

        batchAndSem = intent?.getStringExtra("batch_and_sem")!!
        val classModelGson = intent?.getStringExtra("classModelGson")!!
        val gson = Gson()
        classData =
            gson.fromJson(classModelGson, object : TypeToken<ClassesModel>() {}.type)

        className = classData.className
        branch = classData.branch
        batchOrYear = classData.batchOrYear.toInt()
        subCode = classData.subjectCode
        key = classData.classSubKey

        firebaseHelperClass = FirebaseHelperClass()
        mAuth = FirebaseAuth.getInstance()

        setUpToolbarDetails()

        adapter = StudentsInClassAdapter(this, classData, this)
        binding.rvStudents.adapter = adapter

        binding.rlBack.setOnClickListener {
            onBackPressed()
        }

        markAllStudent()
        getAllStudentToThisBranch()

    }

    private fun markAllStudent() {
        binding.cbMarkAllPresent.setOnClickListener {
            if (binding.cbMarkAllPresent.isChecked) {
                binding.rlSaveAttandance.isVisible = true
                adapter?.checkAll()
            } else {
                binding.rlSaveAttandance.isVisible = false
                adapter?.resetTheStateOfView()
            }
        }
    }

    private fun setUpToolbarDetails() {
        binding.classNameTV.text = className
        ("$batchAndSem  $subCode").also { binding.semBatchSubCode.text = it }
    }

    private fun getAllStudentToThisBranch() {
        mRef = firebaseHelperClass.getAllStudentDetailsRef()
        firebaseHelperClass.getStudentsForClasses(
            mRef,
            branch,
            batchOrYear,
            this,
            object : IOnFirebaseActionCallback {
                override fun getStudentDetails(
                    studentDetailsList: CopyOnWriteArrayList<StudentDetailsModel>?,
                    context: Context
                ) {
                    this@StudentInClassActivity.studentDetailsList.clear()
                    if (studentDetailsList != null) {
                        binding.llNoStudentFound.visibility = View.GONE
                        binding.rvStudents.visibility = View.VISIBLE
                        binding.llMarkAllPresent.visibility = View.VISIBLE

                        this@StudentInClassActivity.studentDetailsList.addAll(studentDetailsList)

                        if (studentDetailsList.isNotEmpty())
                            adapter?.submitList(studentDetailsList)

                        Log.d("getStudentDetails", "getStudentDetails: $studentDetailsList")

                    } else {
                        binding.llNoStudentFound.visibility = View.VISIBLE
                        binding.rvStudents.visibility = View.GONE
                        binding.llMarkAllPresent.visibility = View.GONE
                    }
                }
            }, classData.classSubKey, mAuth.uid.toString()
        )
    }

    @SuppressLint("SimpleDateFormat")
    override fun onStudentSelected(
        studentDataList: CopyOnWriteArrayList<StudentDetailsModel>,
        studentListSize: Int
    ) {
        binding.rlSaveAttandance.isVisible = studentDataList.isNotEmpty()
        binding.cbMarkAllPresent.isChecked = studentListSize == studentDataList.size

        binding.rlSaveAttandance.setOnClickListener {

            val attendanceTakenTimeAndDate = CalendarHelperClass().getCurrentTimeAndDate()

            val classesRef = firebaseHelperClass.getClassDataRef(
                branch,
                batchOrYear,
                classData.sem,
                classData.classSubKey,
                mAuth.uid.toString()
            )
            //update TOTAL_ATTENDANCES
            var totalAttendancesTaken: Int
            classesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild(FirebaseKeys.TOTAL_ATTENDANCES)) {
                            totalAttendancesTaken =
                                snapshot.child(FirebaseKeys.TOTAL_ATTENDANCES).value.toString()
                                    .toInt()
                            classesRef.child(FirebaseKeys.TOTAL_ATTENDANCES)
                                .setValue(totalAttendancesTaken + 1)
                            //totalAttendancesTaken += 1
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            //push attendance_history
            val timeFromDate = CalendarHelperClass().getTimeFromDate()
            val attendanceRef =
                classesRef.child(FirebaseKeys.Attendances_history)
                    .child(attendanceTakenTimeAndDate)
                    .child(timeFromDate)
            for (studentData in studentDataList) {
                val map = HashMap<String, Any>()
                map[FirebaseKeys.UID] = studentData.uid
                map[FirebaseKeys.USN] = studentData.usn
                attendanceRef.child(studentData.usn).setValue(map)

                adapter?.resetTheStateOfView()
                binding.rlSaveAttandance.isVisible = false
                adapter?.selectedStudentList?.clear()
                binding.cbMarkAllPresent.isChecked = false
            }
            Log.d("TimeAttendance", "onStudentSelected: $attendanceTakenTimeAndDate ")
        }
    }
}