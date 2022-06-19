package `in`.kit.college_management_system.interfaces

import `in`.kit.college_management_system.model.*
import android.content.Context
import java.util.concurrent.CopyOnWriteArrayList

interface IOnFirebaseActionCallback {

    fun getAllFacultyDetailsCallback(facultyDetails: FacultyDetails) {}
    fun getAllClassesCallback(classModel: ClassesModel?, context: Context) {}
    fun getFilteredClass(classModel: ClassesModel, context: Context) {}
    fun getStudentDetails(
        studentDetailsList: CopyOnWriteArrayList<StudentDetailsModel>?,
        context: Context
    ) {
    }

    fun getSingleStudentAttendanceHist(
        mapDateToDesc: HashMap<Int, HashMap<Int, Any>>,
        monthMap: HashMap<Int, Any>
    ) {
    }

    fun getStudentPresentAndAbsentData(
        studentPresentMap: HashMap<String, Int>?,
        totalAttendanceValue: String,
    ) {
    }

    fun getNoOfAttendancePerDay(
        studentAttendanceHistoryModelList: ArrayList<StudentAttendanceHistoryModel>,
        date: String
    ) {
    }

    fun getSingleStudentDetailsCallback(studentDetails: StudentDetailsModel) {}

    fun getSingleStudentLeaveCallback(studentLeaveList: ArrayList<StudentLeaveHelperModel>) {}
}