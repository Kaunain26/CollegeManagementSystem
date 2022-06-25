package `in`.kit.college_management_system.utils

import `in`.kit.college_management_system.interfaces.IOnFirebaseActionCallback
import `in`.kit.college_management_system.model.*
import `in`.kit.college_management_system.utils.Constants.ABSENT
import `in`.kit.college_management_system.utils.Constants.PRESENT
import `in`.kit.college_management_system.utils.Constants.PRESENT_ABSENT
import `in`.kit.college_management_system.utils.FirebaseKeys.Attendances_history
import `in`.kit.college_management_system.utils.FirebaseKeys.BRANCH
import `in`.kit.college_management_system.utils.FirebaseKeys.FROM_DATE
import `in`.kit.college_management_system.utils.FirebaseKeys.IS_HOD_PERMISSION_GRANTED
import `in`.kit.college_management_system.utils.FirebaseKeys.IS_PRINCIPAL_PERMISSION_GRANTED
import `in`.kit.college_management_system.utils.FirebaseKeys.LEAVES
import `in`.kit.college_management_system.utils.FirebaseKeys.LEAVES_REASON
import `in`.kit.college_management_system.utils.FirebaseKeys.LEAVES_TYPE
import `in`.kit.college_management_system.utils.FirebaseKeys.LEAVE_SENT_DATE
import `in`.kit.college_management_system.utils.FirebaseKeys.NO_OF_DAYS
import `in`.kit.college_management_system.utils.FirebaseKeys.REQUESTED_TO
import `in`.kit.college_management_system.utils.FirebaseKeys.TOTAL_ATTENDANCES
import `in`.kit.college_management_system.utils.FirebaseKeys.TO_DATE
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.round

class FirebaseHelperClass {

    fun getFacultyRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("faculty")

    fun getHodRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("hod")

    fun getStudentRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("student")

    fun getAllStudentDetailsRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("all_student_details")

    fun getPrincipalRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("principal")

    fun getClassesRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("classes")

    fun getAllLeaveRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("all_leaves")

    fun getIndividualStdLeaveRef(
        branch: String,
        batch: String,
        studentSem: String,
        stdUid: String,
    ): DatabaseReference =
        getAllLeaveRef().child(branch).child(batch).child(studentSem).child(stdUid).child(
            LEAVES
        )

    fun getClassAccordingToSemRef(
        branch: String,
        batchOrYear: Int,
        sem: String,
    ): DatabaseReference {
        return getClassesRef().child(branch).child(batchOrYear.toString()).child(sem)
    }

    fun getClassDataRef(
        branch: String,
        batchOrYear: Int,
        sem: String,
        key: String,
        facultyUid: String
    ): DatabaseReference =
        getClassesRef().child(branch).child(batchOrYear.toString()).child(sem).child(facultyUid)
            .child(key)

    fun getAttendanceHistoryRef(
        branch: String,
        batchOrYear: Int,
        sem: String,
        key: String,
        facultyUid: String
    ): DatabaseReference {
        return getClassDataRef(branch, batchOrYear, sem, key, facultyUid).child(Attendances_history)
    }


    fun getSingleStudentDetails(
        studentUid: String,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        getStudentRef().child(studentUid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val studentDetails = snapshot.getValue(StudentDetailsModel::class.java)
                    IOnFirebaseActionCallback.getSingleStudentDetailsCallback(studentDetails!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getSingleFacultyDetails(
        mAuth: String,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
        context: Context
    ) {
        getFacultyRef().child(mAuth)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val facultyDetails = snapshot.getValue(FacultyOrHODDetails::class.java)
                    IOnFirebaseActionCallback.getFacultyOrHODDetailsCallback(
                        facultyDetails!!,
                        context
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getHODDetails(
        mAuth: String,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
        context: Context
    ) {
        getHodRef().child(mAuth).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hodDetails = snapshot.getValue(FacultyOrHODDetails::class.java)
                IOnFirebaseActionCallback.getFacultyOrHODDetailsCallback(hodDetails!!, context)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addClassToFirebase(
        classNameOrSubName: String,
        subjectCode: String,
        selectedBranch: String,
        selectedSem: String,
        batchOrYear: Int,
        uid: String?,
        context: Context
    ) {

        val classDetails = HashMap<String, Any>()
        classDetails["className"] = classNameOrSubName
        classDetails["subjectCode"] = subjectCode
        classDetails["branch"] = selectedBranch
        classDetails["batchOrYear"] = batchOrYear.toString()
        classDetails["sem"] = selectedSem
        classDetails["facultyUid"] = uid!!
        classDetails["total_attendances"] = 0

        val child =
            getClassesRef().child(selectedBranch).child(batchOrYear.toString()).child(selectedSem)
                .child(uid)
        val newKey = child.push()
        newKey.setValue(classDetails).addOnCompleteListener {
            if (it.isSuccessful) Toast.makeText(
                context,
                "Class added successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getFacultyClassFromFirebase(
        mAuth: FirebaseAuth,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
        context: Context
    ) {
        getClassesRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (branches in snapshot.children) {
                        for (batches in branches.children) {
                            //iterate through all batches
                            Log.d("batches11", "onDataChange: ${batches.key} ")
                            for (sem in batches.children) {
                                //iterate through semesters
                                Log.d("sem222", "onDataChange: ${sem.key} ")
                                for (faculty in sem.children) {
                                    // iterate through faculty
                                    Log.d("Fecccc222", "onDataChange: ${faculty.key} ")

                                    for (classDetails in faculty.children) {
                                        //iterate through classes - i.e., how many classes they are taking in this sem
                                        //this will be happen in rare case
                                        Log.d("classes222", "onDataChange: ${classDetails.key} ")


                                        val facultyUID = faculty.key
                                        if (mAuth.uid.toString() == facultyUID) {
                                            Log.d(
                                                "facultyChildren",
                                                "onDataChange: ${classDetails.key} "
                                            )
                                            Log.d(
                                                "faculty222",
                                                "onDataChange: ${mAuth.uid.toString()} or ${
                                                    faculty.child(
                                                        mAuth.uid.toString()
                                                    ).key
                                                } "
                                            )
                                            val className =
                                                classDetails.child("className").value.toString()
                                            val batchOrYear =
                                                classDetails.child("batchOrYear").value.toString()
                                            val _branch =
                                                classDetails.child("branch").value.toString()
                                            val _sem = classDetails.child("sem").value.toString()
                                            val subjectCode =
                                                classDetails.child("subjectCode").value.toString()
                                            val classModel = ClassesModel(
                                                classDetails.key.toString(),
                                                className,
                                                subjectCode,
                                                _sem,
                                                _branch,
                                                batchOrYear
                                            )
                                            classModel.facultyUid = facultyUID

                                            Log.d("ClassesDetails", "onDataChange: $classModel ")
                                            IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                                                classModel, context
                                            )
                                        } else {
                                            IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                                                null, context
                                            )
                                            Log.d(
                                                "faculty222..",
                                                "onDataChange: ${mAuth.uid.toString()} or ${
                                                    faculty.child(
                                                        mAuth.uid.toString()
                                                    ).key
                                                } "
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                } else {
                    IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                        null, context
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun getAllClassesFromFirebase(
        branch: String,
        batchOrYear: Int,
        studentSem: String,
        studentUsn: String,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
        context: Context
    ) {
        getClassAccordingToSemRef(branch, batchOrYear, studentSem).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (faculties in snapshot.children) {
                        for (classes in faculties.children) {
                            //iterate through classes

                            val className =
                                classes.child("className").value.toString()
                            val batch =
                                classes.child("batchOrYear").value.toString()
                            val _branch =
                                classes.child("branch").value.toString()
                            val classSem = classes.child("sem").value.toString()
                            val subjectCode =
                                classes.child("subjectCode").value.toString()
                            val facultyUid = classes.child("facultyUid").value.toString()

                            getStudentPresentAndAbsentData(
                                branch,
                                batchOrYear,
                                classSem, /* here we are passing class sem instead user current sem*/
                                classes.key.toString(),
                                facultyUid,//faculty uid
                                object : IOnFirebaseActionCallback {
                                    override fun getStudentPresentAndAbsentData(
                                        studentPresentMap: HashMap<String, Int>?,
                                        totalAttendanceValue: String
                                    ) {

                                        if (studentPresentMap != null) {
                                            val classModel = ClassesModel(
                                                classes.key.toString(),
                                                className,
                                                subjectCode,
                                                classSem,
                                                _branch,
                                                batch
                                            )
                                            classModel.facultyUid = facultyUid

                                            if (studentPresentMap[studentUsn] != null) {
                                                val presentDays =
                                                    studentPresentMap[studentUsn].toString()

                                                if (presentDays.toInt() != 0) {
                                                    val attendancePercentage =
                                                        round((presentDays.toDouble() / totalAttendanceValue.toDouble()) * 100).toInt()
                                                            .toString()

                                                    classModel.totalAttendancePercentage =
                                                        attendancePercentage
                                                } else {
                                                    classModel.totalAttendancePercentage = "0"
                                                }
                                            } else {
                                                classModel.totalAttendancePercentage = "0"
                                            }

                                            IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                                                classModel, context
                                            )
                                        } else {
                                            val classModel = ClassesModel(
                                                classes.key.toString(),
                                                className,
                                                subjectCode,
                                                classSem,
                                                _branch,
                                                batch
                                            )
                                            classModel.facultyUid = facultyUid
                                            IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                                                classModel, context
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                } else {
                    IOnFirebaseActionCallback.getFacultyAllClassesCallback(
                        null, context
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun filterClassesFromFirebase(
        mAuth: FirebaseAuth,
        branch: String,
        batch: String,
        sem: String,
        context: Context,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
    ) {
        getClassesRef().child(branch).child(batch).child(sem).child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (classes in snapshot.children) {
                        // classDetails iteration
                        val classModel = classes.getValue(ClassesModel::class.java)
                        classModel?.classSubKey = classes.key.toString()
                        //Log.d("filterdClasses", "onDataChange:$classModel ")

                        Log.d("classModel", "onDataChange:$classModel ")
                        IOnFirebaseActionCallback.getFilteredClass(classModel!!, context)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getStudentsForClasses(
        mRef: DatabaseReference,
        branch: String,
        batchOrYear: Int,
        context: Context,
        IOnFirebaseActionCallback: IOnFirebaseActionCallback,
        classSubKey: String,
        facultyUid: String
    ) {
        val studentDetailsList = CopyOnWriteArrayList<StudentDetailsModel>()

        mRef.child(branch).child(batchOrYear.toString()).child("details")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        studentDetailsList.clear()
                        for (studentsData in snapshot.children) {
                            Log.d("studentsData", "onDataChange:${studentsData} ")
                            val name =
                                studentsData.child(FirebaseKeys.NAME).value.toString()
                            val address =
                                studentsData.child(FirebaseKeys.ADDRESS).value.toString()
                            val batch =
                                studentsData.child(FirebaseKeys.BATCH).value.toString().toInt()
                            val _branch =
                                studentsData.child(BRANCH).value.toString()
                            val email =
                                studentsData.child(FirebaseKeys.EMAIL).value.toString()
                            val gender =
                                studentsData.child(FirebaseKeys.GENDER).value.toString()
                            val sem = studentsData.child(FirebaseKeys.SEM).value.toString()
                            val uid = studentsData.child(FirebaseKeys.UID).value.toString()
                            val usn = studentsData.child(FirebaseKeys.USN).value.toString()
                            val leaveDays =
                                studentsData.child(LEAVES).value.toString()
                            val photoUrl = if (studentsData.hasChild(FirebaseKeys.PHOTO_URL)) {
                                studentsData.child(FirebaseKeys.PHOTO_URL).value.toString()
                            } else {
                                ""
                            }

                            Log.d("SemValue", "onDataChange: $sem")

                            val studentDetails = StudentDetailsModel(
                                uid,
                                name,
                                usn,
                                sem,
                                _branch,
                                batch,
                                address,
                                email,
                                gender,
                                photoUrl,
                            )

                            studentDetails.leaves = "0"
                            studentDetails.isSelected = false
                            studentDetails.isExpanded = false

                            studentDetailsList.add(studentDetails)
                        }
                        IOnFirebaseActionCallback.getStudentDetails(
                            studentDetailsList,
                            context,
                            ArrayList()
                        )

                    } else {
                        IOnFirebaseActionCallback.getStudentDetails(
                            studentDetailsList,
                            context,
                            ArrayList()

                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    fun getSingleStudentAttendance(
        branch: String,
        batchOrYear: Int,
        sem: String,
        classKey: String,
        facultyUid: String,
        usn: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        Log.d(
            "dateListMapWithMonth..",
            "onDataChange:branch $branch , batchOrYear $batchOrYear , sem $sem , classKey $classKey , facultyUid $facultyUid "
        )
        getAttendanceHistoryRef(
            branch,
            batchOrYear,
            sem,
            classKey,
            facultyUid
        ).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val calendarHelperClass = CalendarHelperClass()
                        Calendar.getInstance().add(Calendar.MONTH, 1)
                        val dateListMapWithMonth = HashMap<Int, HashMap<Int, Any>>()
                        var mapDateToDesc: HashMap<Int, Any> = HashMap()
                        val monthMap: HashMap<Int, Any> = HashMap()

                        val tempAttendanceDateList = ArrayList<Int>()
                        for ((index, attendanceHistoryData) in snapshot.children.withIndex()) {
                            val attendanceDate =
                                attendanceHistoryData.key.toString()
                            //Log.d("studentsFound", "onDataChange:${attendanceDate} ")
                            val calendarInstanceFromDate =
                                calendarHelperClass.getCalendarInstanceFromDate(
                                    attendanceDate,
                                    "MMM dd yyyy"
                                )
                            tempAttendanceDateList.add(calendarInstanceFromDate[Calendar.MONTH])

                            val dateOfTheMonth =
                                calendarHelperClass.getDateOfTheMonth(attendanceDate, "MMM dd yyyy")

                            if (tempAttendanceDateList.size > 1) {
                                if (calendarInstanceFromDate[Calendar.MONTH] != tempAttendanceDateList[tempAttendanceDateList.size - 2]) {
                                    //if month differs from current month then, just store that HasMap to list and clear it for storing new dates
                                    monthMap[tempAttendanceDateList[tempAttendanceDateList.size - 2]] =
                                        tempAttendanceDateList[tempAttendanceDateList.size - 2]
                                    dateListMapWithMonth[tempAttendanceDateList[tempAttendanceDateList.size - 2]] =
                                        mapDateToDesc
                                    mapDateToDesc = HashMap()
                                }
                            }

                            var isPresent = false

                            for (attendancesInADay in attendanceHistoryData.children) {
                                //iterate through number of attendances in a day
                                if (attendanceHistoryData.childrenCount > 1) {
                                    mapDateToDesc[dateOfTheMonth] = PRESENT_ABSENT
                                } else {
                                    for (students in attendancesInADay.children) {
                                        //iterate through usn
                                        if (students.key == usn) {
                                            //present
                                            isPresent = true
                                            mapDateToDesc[dateOfTheMonth] = PRESENT
                                            Log.d(
                                                "studentsFound",
                                                "onDataChange:${mapDateToDesc} "
                                            )
                                            break
                                        }
                                    }
                                    //if student not present then set it as absent
                                    if (!isPresent) {
                                        mapDateToDesc[dateOfTheMonth] = ABSENT
                                        Log.d(
                                            "studentsFound",
                                            "onDataChange:${mapDateToDesc} "
                                        )
                                    }
                                }
                            }

                            //At last
                            if (index == (snapshot.childrenCount - 1).toInt()) {
                                monthMap[calendarInstanceFromDate[Calendar.MONTH]] =
                                    calendarInstanceFromDate[Calendar.MONTH]
                                dateListMapWithMonth[calendarInstanceFromDate[Calendar.MONTH]] =
                                    mapDateToDesc
                            }
                            Log.d(
                                "dateListMapWithMonth",
                                "onDataChange:${mapDateToDesc} "
                            )
                        }
                        iOnFirebaseActionCallback.getSingleStudentAttendanceHist(
                            dateListMapWithMonth,
                            monthMap
                        )

                    } else {
                        // this is hack to avoid app crash if there is no prev attendance
                        val dateListMapWithMonth = HashMap<Int, HashMap<Int, Any>>()
                        val mapDateToDesc: HashMap<Int, Any> = HashMap()
                        mapDateToDesc[1] = ""
                        dateListMapWithMonth[Calendar.getInstance()[Calendar.MONTH]] = mapDateToDesc
                        iOnFirebaseActionCallback.getSingleStudentAttendanceHist(
                            dateListMapWithMonth,
                            HashMap()
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        /* attendanceHistoryRef.addListenerForSingleValueEvent(
             object :
                 ValueEventListener {

                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (!snapshot.hasChild(Attendances_history)) {

                     } else {
                         // for getting prev/history attendance

                     }
                 }

                 override fun onCancelled(error: DatabaseError) {}
             })*/
    }


    fun getStudentPresentAndAbsentData(
        branch: String,
        batchOrYear: Int,
        sem: String,
        key: String,
        uid: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        Log.d("totalAttendanceValue", "bind:$branch , $batchOrYear , $sem , $key , $uid ,  ")

        getAttendanceHistoryRef(branch, batchOrYear, sem, key, uid)
            .addValueEventListener(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {

                        Calendar.getInstance().add(Calendar.MONTH, 1)
                        val studentPresentMap: HashMap<String, Int> = HashMap()

                        for (attendanceHistoryData in snapshot.children) {
                            for (attendancesInADay in attendanceHistoryData.children) {
                                for (students in attendancesInADay.children) {
                                    if (!studentPresentMap.containsKey(students.key.toString())) {
                                        studentPresentMap[students.key.toString()] = 1
                                    } else {
                                        val i: Int? = studentPresentMap[students.key.toString()]
                                        studentPresentMap[students.key.toString()] = i!! + 1
                                    }
                                }
                            }
                        }
                        getClassDataRef(
                            branch,
                            batchOrYear,
                            sem,
                            key,
                            uid
                        ).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val totalAttendanceValue =
                                    snapshot.child(TOTAL_ATTENDANCES).value.toString()
                                Log.d("totalAttendanceValue", "bind:${totalAttendanceValue} ")

                                iOnFirebaseActionCallback.getStudentPresentAndAbsentData(
                                    studentPresentMap,
                                    totalAttendanceValue
                                )
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })


                    } else {
                        iOnFirebaseActionCallback.getStudentPresentAndAbsentData(
                            null,
                            "0"
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getNoOfAttendancePerDay(
        branch: String,
        batchOrYear: Int,
        sem: String,
        key: String,
        facultyUid: String,
        usn: String,
        date: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        getAttendanceHistoryRef(
            branch,
            batchOrYear,
            sem,
            key,
            facultyUid
        ).child(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val studentAttendanceHistoryList = ArrayList<StudentAttendanceHistoryModel>()
                    for ((index, attendancesInADay) in snapshot.children.withIndex()) {
                        val studentAttendanceHistoryModel = StudentAttendanceHistoryModel()
                        studentAttendanceHistoryModel.time = attendancesInADay.key.toString()
                        studentAttendanceHistoryModel.classNumber = "Class - ${index + 1}"
                        studentAttendanceHistoryModel.isPresent = false
                        studentAttendanceHistoryModel.usn = usn
                        studentAttendanceHistoryModel.date = date
                        for (student in attendancesInADay.children) {
                            //iterate through students
                            if (student.key == usn) {
                                studentAttendanceHistoryModel.uid =
                                    student.child("uid").value.toString()
                                studentAttendanceHistoryModel.isPresent = true
                                break
                            }
                        }
                        studentAttendanceHistoryList.add(studentAttendanceHistoryModel)
                    }
                    iOnFirebaseActionCallback.getNoOfAttendancePerDay(
                        studentAttendanceHistoryList,
                        date
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    fun sendStudentLeaveToFirebase(
        leaveType: String,
        leaveReason: String,
        leaveNoOfDays: String,
        fromDate: String,
        toDate: String,
        branch: String,
        batch: Int,
        studentSem: String,
        studentUid: String,
        sendLeaveToWhom: String
    ) {

        val leaveSentDate =
            CalendarHelperClass().getFormattedDate(Calendar.getInstance().time, "dd MMM yy")
        val leaveMap = HashMap<String, Any>()
        leaveMap[LEAVES_TYPE] = leaveType
        leaveMap[LEAVES_REASON] = leaveReason
        leaveMap[NO_OF_DAYS] = leaveNoOfDays
        leaveMap[REQUESTED_TO] = sendLeaveToWhom
        leaveMap[IS_PRINCIPAL_PERMISSION_GRANTED] = 0
        leaveMap[IS_HOD_PERMISSION_GRANTED] = 0
        leaveMap[FROM_DATE] = fromDate
        leaveMap[TO_DATE] = toDate
        leaveMap[LEAVE_SENT_DATE] = leaveSentDate

        getIndividualStdLeaveRef(
            branch,
            batch.toString(),
            studentSem,
            studentUid
        ).push().setValue(leaveMap)
    }


    /* fun getSingleStudentCasualLeave(
         branch: String,
         batch: String,
         studentSem: String,
         studentUid: String,
         context: Context,
         iOnFirebaseActionCallback: IOnFirebaseActionCallback
     ) {

         getIndividualStdLeaveRef(
             branch,
             batch,
             studentSem,
             studentUid
         ).addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()) {

                 } else {

                 }
             }

             override fun onCancelled(error: DatabaseError) {}
         })
     } */

    fun getSingleStudentsLeaves(
        branch: String,
        batch: String,
        studentSem: String,
        studentUid: String,
        getLeaveOnType: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        getIndividualStdLeaveRef(
            branch,
            batch,
            studentSem,
            studentUid
        ).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("studentLeaveModel", "onDataChange: $snapshot")
                val studentNewLeaveList = ArrayList<StudentLeaveHelperModel.StudentLeaveModel>()
                val studentOldLeaveList = ArrayList<StudentLeaveHelperModel.StudentLeaveModel>()
                val studentLeaveList = ArrayList<StudentLeaveHelperModel>()

                if (snapshot.exists()) {
                    for (leaves in snapshot.children) {
                        val leaveType = leaves.child(LEAVES_TYPE).value.toString()
                        val leaveReason = leaves.child(LEAVES_REASON).value.toString()
                        val leaveDays = leaves.child(NO_OF_DAYS).value.toString()
                        val requestedTo = leaves.child(REQUESTED_TO).value.toString()
                        val leaveSentDate = leaves.child(LEAVE_SENT_DATE).value.toString()
                        val leaveFromDate = leaves.child(FROM_DATE).value.toString()
                        val leaveToDate = leaves.child(TO_DATE).value.toString()
                        val hodPerm =
                            leaves.child(IS_HOD_PERMISSION_GRANTED).value.toString().toInt()
                        val princiPerm =
                            leaves.child(IS_PRINCIPAL_PERMISSION_GRANTED).value.toString().toInt()

                        val studentLeaveModel = StudentLeaveHelperModel.StudentLeaveModel(
                            leaves.key.toString(),
                            leaveType,
                            leaveReason,
                            leaveDays,
                            requestedTo,
                            leaveToDate,
                            leaveFromDate,
                            hodPerm,
                            princiPerm,
                            leaveSentDate
                        )

                        //checking new or old leave
                        val calender = Calendar.getInstance()
                        val calendarHelperClass = CalendarHelperClass()
                        val currentDate =
                            calendarHelperClass.getFormattedDate(calender.time, "dd MMM yy")

                        val leaveSentDateInstance =
                            calendarHelperClass.getCalendarInstanceFromDate(
                                studentLeaveModel.leave_sent_date,
                                "dd MMM yy"
                            )
                        val _leaveSentDate =
                            calendarHelperClass.getFormattedDate(
                                leaveSentDateInstance.time,
                                "dd MMM yy"
                            )

                        if (leaveType == getLeaveOnType || getLeaveOnType == "All leaves") {
                            if (currentDate == _leaveSentDate) {
                                // new leave
                                studentNewLeaveList.add(studentLeaveModel)
                            } else {
                                // old leave
                                studentOldLeaveList.add(studentLeaveModel)
                            }
                        }
                    }
                    if (studentNewLeaveList.isNotEmpty())
                        studentLeaveList.add(StudentLeaveHelperModel.SubHeader(0, "Today"))
                    studentLeaveList.addAll(studentNewLeaveList)

                    if (studentOldLeaveList.isNotEmpty())
                        studentLeaveList.add(StudentLeaveHelperModel.SubHeader(1, "Older"))
                    studentLeaveList.addAll(studentOldLeaveList)

                    iOnFirebaseActionCallback.getSingleStudentLeaveCallback(studentLeaveList)

                } else {
                    //Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                    iOnFirebaseActionCallback.getSingleStudentLeaveCallback(studentLeaveList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getStdLeavesAccordingToBatchForFaculty(
        branch: String,
        batch: String,
        filter: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback,
        context: Context
    ) {

        val _batch: String = if (filter != "All Batches") {
            filter.split('-')[0]
        } else {
            batch
        }

        getAllLeaveRef().child(branch.trim()).child(_batch.trim())
            .addValueEventListener(object : ValueEventListener {

                val leaveList = ArrayList<FacultySecStudentLeaveModel>()

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("mAuthclassModel2", "onDataChange:${_batch} $branch")

                        leaveList.clear()
                        for (stdSem in snapshot.children) {

                            //iterate through sem
                            for (students in stdSem.children) {
                                //iterate through students
                                val leaveTimeLineList =
                                    ArrayList<FacultySectionStudtLeaveTimeLineModel>()
                                getSingleStudentDetails(students.key.toString(),
                                    object : IOnFirebaseActionCallback {
                                        override fun getSingleStudentDetailsCallback(
                                            studentDetails: StudentDetailsModel
                                        ) {
                                            for (leaves in students.child(LEAVES).children) {
                                                Log.d(
                                                    "CheckSemester",
                                                    "getSingleStudentDetailsCallback:  ${stdSem.key.toString()} , ${studentDetails.sem}"
                                                )

                                                if (studentDetails.sem != stdSem.key.toString()) {
                                                    break
                                                }

                                                val value = leaves.getValue(
                                                    FacultySectionStudtLeaveTimeLineModel::class.java
                                                )
                                                value!!.is_hod_permission_granted =
                                                    leaves.child(
                                                        IS_HOD_PERMISSION_GRANTED
                                                    ).value.toString()
                                                        .toInt()
                                                value.is_principal_permission_granted =
                                                    leaves.child(
                                                        IS_PRINCIPAL_PERMISSION_GRANTED
                                                    ).value.toString()
                                                        .toInt()
                                                value.sem = students.key.toString()

                                                if (value.requested_to == "HOD") {
                                                    when (value.is_hod_permission_granted) {
                                                        0 -> {
                                                            value.title = "Awaiting"
                                                            value.status =
                                                                LeaveStatus.AWAITING
                                                        }
                                                        1 -> {
                                                            value.title = "Approved"
                                                            value.status =
                                                                LeaveStatus.APPROVED
                                                        }
                                                        -1 -> {
                                                            value.title = "Rejected"
                                                            value.status =
                                                                LeaveStatus.REJECTED
                                                        }
                                                    }
                                                } else if (value.requested_to == "Principal_HOD") {
                                                    when (value.is_hod_permission_granted) {
                                                        1 -> {
                                                            when (value.is_principal_permission_granted) {
                                                                1 -> {
                                                                    value.title =
                                                                        "Approved"
                                                                    value.status =
                                                                        LeaveStatus.APPROVED
                                                                }
                                                                0 -> {
                                                                    value.title =
                                                                        "Awaiting"
                                                                    value.status =
                                                                        LeaveStatus.AWAITING
                                                                }
                                                                -1 -> {
                                                                    value.title =
                                                                        "Rejected"
                                                                    value.status =
                                                                        LeaveStatus.REJECTED
                                                                }
                                                            }

                                                        }
                                                        0 -> {
                                                            value.title = "Awaiting"
                                                            value.status =
                                                                LeaveStatus.AWAITING
                                                        }
                                                        -1 -> {
                                                            value.title = "Rejected"
                                                            value.status =
                                                                LeaveStatus.REJECTED
                                                        }
                                                    }
                                                }
                                                value.date = value.leave_sent_date

                                                leaveTimeLineList.add(value)

                                            }

                                            if (leaveTimeLineList.isNotEmpty()) {

                                                val studtLeaveTimeLineModel =
                                                    leaveTimeLineList[leaveTimeLineList.size - 1]

                                                leaveList.add(
                                                    FacultySecStudentLeaveModel(
                                                        studtLeaveTimeLineModel.leave_type,
                                                        studtLeaveTimeLineModel.leave_reason,
                                                        studtLeaveTimeLineModel.no_of_days,
                                                        studtLeaveTimeLineModel.requested_to,
                                                        studtLeaveTimeLineModel.to_date,
                                                        studtLeaveTimeLineModel.from_date,
                                                        studtLeaveTimeLineModel.is_hod_permission_granted,
                                                        studtLeaveTimeLineModel.is_principal_permission_granted,
                                                        studtLeaveTimeLineModel.leave_sent_date,
                                                        studentDetails.name,
                                                        studentDetails.usn,
                                                        studentDetails.sem,
                                                        studentDetails.batch.toString(),
                                                        leaveTimeLineList,
                                                        studentDetails.photo_url
                                                    )
                                                )

                                                Log.d(
                                                    "StdLeaves...2",
                                                    "onDataChange:${leaveList} "
                                                )

                                                //update on last index
                                                iOnFirebaseActionCallback.getStdLeavesAccordingToBatchForFacultyCallBack(
                                                    leaveList, context
                                                )

                                            }
                                        }
                                    }
                                )
                            }

                        }
                    } else {
                        //null
                        Log.d("mAuthclassModel", "onDataChange:${_batch} $branch")

                        iOnFirebaseActionCallback.getStdLeavesAccordingToBatchForFacultyCallBack(
                            leaveList,
                            context
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

    }

    fun getFacultyDetailsBranchWise(
        branch: String,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        getFacultyRef().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val facultiesList = ArrayList<FacultyOrHODDetails>()
                if (snapshot.exists()) {
                    for (faculties in snapshot.children) {
                        if (faculties.child(BRANCH).value.toString() == branch) {
                            faculties.getValue(FacultyOrHODDetails::class.java)
                                ?.let { facultiesList.add(it) }
                        }
                    }
                    Log.d(
                        "facultiesList",
                        "onDataChange: $facultiesList "
                    )
                    iOnFirebaseActionCallback.getFacultyDetailsBranchWiseCallBack(facultiesList)
                } else {
                    //no ref exist
                    iOnFirebaseActionCallback.getFacultyDetailsBranchWiseCallBack(facultiesList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getStudentsAccordingToBatch(
        branch: String,
        context: Context,
        iOnFirebaseActionCallback: IOnFirebaseActionCallback
    ) {
        getAllStudentDetailsRef().child(branch).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val studentDetailsList = CopyOnWriteArrayList<StudentDetailsModel>()
                val batchList = ArrayList<String>()

                if (snapshot.exists()) {
                    for (batches in snapshot.children) {
                        batchList.add(batches.key.toString())
                        for (studentsData in batches.child("details").children) {
                            val name =
                                studentsData.child(FirebaseKeys.NAME).value.toString()
                            val address =
                                studentsData.child(FirebaseKeys.ADDRESS).value.toString()
                            val batch =
                                studentsData.child(FirebaseKeys.BATCH).value.toString().toInt()
                            val _branch =
                                studentsData.child(BRANCH).value.toString()
                            val email =
                                studentsData.child(FirebaseKeys.EMAIL).value.toString()
                            val gender =
                                studentsData.child(FirebaseKeys.GENDER).value.toString()
                            val sem = studentsData.child(FirebaseKeys.SEM).value.toString()
                            val uid = studentsData.child(FirebaseKeys.UID).value.toString()
                            val usn = studentsData.child(FirebaseKeys.USN).value.toString()
                            val leaveDays =
                                studentsData.child(LEAVES).value.toString()
                            val photoUrl = if (studentsData.hasChild(FirebaseKeys.PHOTO_URL)) {
                                studentsData.child(FirebaseKeys.PHOTO_URL).value.toString()
                            } else {
                                ""
                            }

                            Log.d("SemValue", "onDataChange: $sem")

                            val studentDetails = StudentDetailsModel(
                                uid,
                                name,
                                usn,
                                sem,
                                _branch,
                                batch,
                                address,
                                email,
                                gender,
                                photoUrl,
                            )

                            studentDetails.leaves = "0"
                            studentDetails.isSelected = false
                            studentDetails.isExpanded = false

                            studentDetailsList.add(studentDetails)

                            Log.d("stdDetails", "onDataChange: $studentDetails")
                        }

                    }

                    iOnFirebaseActionCallback.getStudentDetails(
                        studentDetailsList,
                        context,
                        batchList
                    )

                } else {
                    iOnFirebaseActionCallback.getStudentDetails(
                        studentDetailsList,
                        context,
                        batchList
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}