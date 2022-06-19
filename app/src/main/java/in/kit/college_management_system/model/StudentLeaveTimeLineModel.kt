package `in`.kit.college_management_system.model

data class StudentLeaveTimeLineModel(
    var title: String,
    var date: String,
    var status: LeaveStatus,
    var message: String
)