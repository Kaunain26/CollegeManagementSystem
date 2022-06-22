package `in`.kit.college_management_system.model

data class FacultySecStudentLeaveModel(
    var leave_type: String,
    var leave_reason: String,
    var no_of_days: String,
    var requested_to: String,
    var to_date: String,
    var from_date: String,
    var is_hod_permission_granted: Int,
    var is_principal_permission_granted: Int,
    var leave_sent_date: String,
    var stdName: String,
    var stdUsn: String,
    var sem: String,
    var batch: String,
    var facultySectionStudLeaveTimeLineList: ArrayList<FacultySectionStudtLeaveTimeLineModel>,
    var photo_url: String
) {
}