package `in`.kit.college_management_system.model

data class FacultySectionStudtLeaveTimeLineModel(
    var title: String,
    var date: String,
    var status: LeaveStatus,
    var message: String,
    var leave_type: String,
    var leave_reason: String,
    var no_of_days: String,
    var requested_to: String,
    var to_date: String,
    var from_date: String,
    var is_hod_permission_granted: Int,
    var is_principal_permission_granted: Int,
    var leave_sent_date: String,
    var sem:String,
) {
    constructor() : this("", "", LeaveStatus.AWAITING, "", "", "", "", "", "", "", 0, 0, "","")
}