package `in`.kit.college_management_system.facultySection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.ActivityStudentAttendanceDetailsBinding
import `in`.kit.college_management_system.facultySection.fragments.BottomSheetAttendanceHistory
import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.StudentAttendanceHistoryModel
import `in`.kit.college_management_system.model.StudentDetailsModel
import `in`.kit.college_management_system.utils.*
import `in`.kit.college_management_system.utils.Constants.ABSENT
import `in`.kit.college_management_system.utils.Constants.CLASS_KEY
import `in`.kit.college_management_system.utils.Constants.CLASS_SEM
import `in`.kit.college_management_system.utils.Constants.FACULTY_UID
import `in`.kit.college_management_system.utils.Constants.IS_FACULTY
import `in`.kit.college_management_system.utils.Constants.PRESENT
import `in`.kit.college_management_system.utils.Constants.PRESENT_ABSENT
import `in`.kit.college_management_system.utils.Constants.STUDENT_DATA_MODEL
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.naishadhparmar.zcustomcalendar.CustomCalendar
import org.naishadhparmar.zcustomcalendar.OnNavigationButtonClickedListener
import org.naishadhparmar.zcustomcalendar.Property
import java.util.*
import kotlin.math.round


class StudentAttendanceDetailsActivity : AppCompatActivity(), IOnFirebaseActionCallback,
    OnNavigationButtonClickedListener {
    private lateinit var binding: ActivityStudentAttendanceDetailsBinding
    private var studentData = StudentDetailsModel()
    private lateinit var firebaseHelperClass: FirebaseHelperClass

    // private lateinit var mAuth: FirebaseAuth
    private var attendanceHistoryMap = HashMap<Int, HashMap<Int, Any>>()
    private var attendanceTakenMonth = HashMap<Int, Any>()
    private var classKey = ""
    private var classSem = ""
    private var facultyUid = ""
    private var isfaculty = false //default value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentAttendanceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseHelperClass = FirebaseHelperClass()
        //mAuth = FirebaseAuth.getInstance()

        binding.rlArrowBack.setOnClickListener {
            onBackPressed()
        }

        val studentDataModel = intent?.getStringExtra(STUDENT_DATA_MODEL)!!
        classKey = intent?.getStringExtra(CLASS_KEY)!!
        classSem = intent?.getStringExtra(CLASS_SEM)!!
        facultyUid = intent?.getStringExtra(FACULTY_UID)!!
        isfaculty = intent?.getBooleanExtra(IS_FACULTY, false)!!

        Log.d(
            "students_details",
            "onDataChange:classKey $classKey , classSem$classSem , studentDataModel $studentDataModel , facultyUid $facultyUid "
        )

        val gson = Gson()
        studentData =
            gson.fromJson(
                studentDataModel,
                object : TypeToken<StudentDetailsModel>() {}.type
            )
        setUpStudentDetailsToViews()


        binding.customCalender.setOnNavigationButtonClickedListener(CustomCalendar.PREVIOUS, this);
        binding.customCalender.setOnNavigationButtonClickedListener(CustomCalendar.NEXT, this);

        firebaseHelperClass.getSingleStudentAttendance(
            studentData.branch,
            studentData.batch,
            classSem, /* here we are passing class sem instead user current sem*/
            classKey,
            facultyUid,//faculty uid
            studentData.usn,
            this
        )

        firebaseHelperClass.getStudentPresentAndAbsentData(
            studentData.branch,
            studentData.batch,
            classSem, /* here we are passing class sem instead user current sem*/
            classKey,
            facultyUid,//faculty uid
            this
        )

        binding.editReqBtn.isVisible = isfaculty

        binding.customCalender.setOnDateSelectedListener { view, selectedDate, desc ->
            val formattedDate =
                CalendarHelperClass().getFormattedDate(
                    selectedDate.time,
                    "MMM dd yyyy"
                ) // Jun 11 2022
            when (desc) {
                PRESENT_ABSENT -> {
                    firebaseHelperClass.getNoOfAttendancePerDay(
                        studentData.branch,
                        studentData.batch,
                        studentData.sem,
                        classKey,
                        facultyUid,
                        studentData.usn,
                        formattedDate,
                        this
                    )
                }
                ABSENT -> {
                    //mark his/her present
                    if (isfaculty) {
                        //Student are not allowed to edit attendance
                        updateAttendancePresentOrAbsent(formattedDate, false, "PRESENT")
                    }

                }
                PRESENT -> {
                    //mark his/her absent
                    if (isfaculty) {
                        //Student are not allowed to edit attendance
                        updateAttendancePresentOrAbsent(formattedDate, true, "ABSENT")
                    }
                }
            }
        }

    }

    private fun updateAttendancePresentOrAbsent(
        formattedDate: String,
        isPresent: Boolean,
        absentOrPresent: String
    ) {
        val alertDialogHelperClass = AlertDialogHelperClass(this)
        alertDialogHelperClass.build(
            "Confirmation",
            "Do you want to mark his/her $absentOrPresent on $formattedDate ?",
            "Yes", "No"
        )
        alertDialogHelperClass.isCancelable(true)
        alertDialogHelperClass.show()

        alertDialogHelperClass.listener =
            object : AlertDialogHelperClass.OnAlertDialogActionPerformed {
                override fun positiveAction(dialog: DialogInterface) {

                    binding.rlUpdatingProgress.isVisible = true
                    val classesRef = firebaseHelperClass.getClassDataRef(
                        studentData.branch,
                        studentData.batch,
                        studentData.sem,
                        classKey,
                        facultyUid //faculty uid
                    )

                    val attendanceRef =
                        classesRef.child(FirebaseKeys.Attendances_history)
                            .child(formattedDate)

                    attendanceRef.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            //its a hack to get a child for updating attendance
                            for (data in snapshot.children) {
                                // this for loop will iterate only once since it has only one data.
                                val map = HashMap<String, Any>()
                                map[FirebaseKeys.UID] = studentData.uid
                                map[FirebaseKeys.USN] = studentData.usn
                                if (!isPresent)
                                //mark present
                                    attendanceRef.child(data.key.toString())
                                        .child(studentData.usn).setValue(map)
                                        .addOnCompleteListener {
                                            binding.rlUpdatingProgress.isVisible = false
                                        }
                                else
                                // mark absent
                                    attendanceRef.child(data.key.toString())
                                        .child(studentData.usn).setValue(null)
                                        .addOnCompleteListener {
                                            binding.rlUpdatingProgress.isVisible = false
                                        }

                                break
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            binding.rlUpdatingProgress.isVisible = false
                        }
                    })
                }

                override fun negativeAction(dialog: DialogInterface) {
                    dialog.dismiss()
                }
            }
    }

    private fun setUpAttendanceInCalendar(mapDateToDesc: HashMap<Int, HashMap<Int, Any>>) {
        val mapDescToProp: HashMap<Any, Property> = HashMap()

        val propPresent = Property()
        propPresent.layoutResource = R.layout.cal_present_view
        propPresent.dateTextViewResource = R.id.present_date_textview
        mapDescToProp[PRESENT] = propPresent


        val propAbsent = Property()
        propAbsent.layoutResource = R.layout.cal_absent_view
        propAbsent.dateTextViewResource = R.id.absent_date_textview
        mapDescToProp[ABSENT] = propAbsent

        val propPresentAbsent = Property()
        propPresentAbsent.layoutResource = R.layout.cal_present_absent_view
        propPresentAbsent.dateTextViewResource = R.id.present_absent_date_textview
        mapDescToProp[PRESENT_ABSENT] = propPresentAbsent

        binding.customCalender.setMapDescToProp(mapDescToProp)

        //val mapDateToDesc: HashMap<Int, Any> = HashMap()
        val calendar: Calendar = Calendar.getInstance()
        binding.customCalender.setDate(calendar, mapDateToDesc[calendar[Calendar.MONTH]])
    }

    private fun setUpStudentDetailsToViews() {
        binding.studentName.text = studentData.name
        binding.studentUsn.text = studentData.usn
        updateAttendanceProgress(0) //default to zero

        if (studentData.photo_url != "") {
            Glide.with(this).load(studentData.photo_url)
                .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                .into(binding.profileImg)
        } else {
            var nameInitials = ""
            SplitAndGetNameInitials().apply {
                val splitNameList = splitName(studentData.name)
                nameInitials = getNameInitials(splitNameList)
            }
            binding.nameInitialsTV.text = nameInitials
        }
    }

    private fun updateAttendanceProgress(attendancePercentage: Int) {
        val animator = ObjectAnimator.ofInt(binding.progressBar, "progress", attendancePercentage)
        animator.duration = 1000
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    override fun getSingleStudentAttendanceHist(
        mapDateToDesc: HashMap<Int, HashMap<Int, Any>>,
        monthMap: HashMap<Int, Any>
    ) {
        super.getSingleStudentAttendanceHist(mapDateToDesc, monthMap)
        this.attendanceHistoryMap = mapDateToDesc
        this.attendanceTakenMonth = monthMap

        Log.d("mapDateToDesc", "getSingleStudentAttendanceHist:$mapDateToDesc ")
        setUpAttendanceInCalendar(mapDateToDesc)
    }

    @SuppressLint("SetTextI18n")
    override fun getStudentPresentAndAbsentData(
        studentPresentMap: HashMap<String, Int>?,
        totalAttendanceValue: String
    ) {
        super.getStudentPresentAndAbsentData(studentPresentMap, totalAttendanceValue)

        if (studentPresentMap?.get(studentData.usn) != null) {
            val presentDays = studentPresentMap[studentData.usn].toString()
            binding.presentDaysValue.text = "$presentDays days"
            val absentDays =
                totalAttendanceValue.toInt() - studentPresentMap[studentData.usn].toString().toInt()
            binding.absentDaysValue.text = "$absentDays days"

            if (presentDays.toInt() != 0) {
                val attendancePercentage =
                    round((presentDays.toDouble() / totalAttendanceValue.toDouble()) * 100).toInt()
                        .toString()
                binding.totalAttendancePercent.text = "$attendancePercentage "
                updateAttendanceProgress(attendancePercentage.toInt())
            } else {
                //binding.progressBar.progress = 0
                updateAttendanceProgress(0)
                binding.totalAttendancePercent.text = "0 "
            }
        } else {
            val presentDays = 0
            binding.presentDaysValue.text = "$presentDays day"
            val absentDays = totalAttendanceValue.toInt()
            binding.absentDaysValue.text = "$absentDays days"
            updateAttendanceProgress(0)
            // binding.progressBar.progress = 0
            binding.totalAttendancePercent.text = "0 "
        }
    }

    override fun getNoOfAttendancePerDay(
        studentAttendanceHistoryModelList: ArrayList<StudentAttendanceHistoryModel>,
        date: String
    ) {
        super.getNoOfAttendancePerDay(studentAttendanceHistoryModelList, date)
        val bottomSheetAttendanceHistory =
            BottomSheetAttendanceHistory(
                studentAttendanceHistoryModelList,
                date,
                studentData, classKey, facultyUid, isfaculty
            )
        bottomSheetAttendanceHistory.show(supportFragmentManager, "bottomSheetAttendanceHistory")

        Log.d(
            "studentAttendanceHistoryModelList",
            "getNoOfAttendancePerDay:$studentAttendanceHistoryModelList "
        )
    }

    override fun onNavigationButtonClicked(
        whichButton: Int,
        newMonth: Calendar
    ): Array<MutableMap<Int, Any>?> {
        val currentCalendar = Calendar.getInstance()
        val currentYear = currentCalendar[Calendar.YEAR]
        val year = newMonth[Calendar.YEAR]

        val arr: Array<MutableMap<Int, Any>?> = arrayOfNulls<MutableMap<Int, Any>?>(2)
        if (currentYear == year) {
            val nextOrPrevMonthAttendance: HashMap<Int, Any>? =
                attendanceHistoryMap[newMonth[Calendar.MONTH]]
            if (attendanceHistoryMap[newMonth[Calendar.MONTH]] == null) {
                //this is hack to avoid app crash if there is no prev attendance
                val hashMap = HashMap<Int, Any>()
                hashMap[1] = ""
                arr[0] = hashMap
            } else {
                arr[0] = nextOrPrevMonthAttendance
            }
        } else {
            //this is hack to avoid app crash if there is no prev attendance
            val hashMap = HashMap<Int, Any>()
            hashMap[1] = ""
            arr[0] = hashMap
        }
        return arr
    }

    override fun onDestroy() {
        super.onDestroy()
        updateAttendanceProgress(0)
    }

}