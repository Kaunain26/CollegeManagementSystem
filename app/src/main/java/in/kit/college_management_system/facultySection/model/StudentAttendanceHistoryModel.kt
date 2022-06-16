package `in`.kit.college_management_system.facultySection.model

data class StudentAttendanceHistoryModel(
    var time: String,
    var classNumber: String,
    var isPresent: Boolean,
) {
    var usn: String = ""
    var uid: String = ""
    var date: String = ""

    constructor() : this("", "", false)
}