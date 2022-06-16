package `in`.kit.college_management_system.interfaces

import `in`.kit.college_management_system.facultySection.model.StudentAttendanceHistoryModel

interface IMarkAbsentAndPresent {

    fun onMarkAbsentOrPresent(studentPresentAbsentList: ArrayList<StudentAttendanceHistoryModel>)
}