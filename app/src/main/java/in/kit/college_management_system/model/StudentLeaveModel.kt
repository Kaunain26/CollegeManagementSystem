package `in`.kit.college_management_system.model

data class StudentLeaveModel(
    var leaveKey: String,
    var leave_type: String,
    var leave_reason: String,
    var no_of_days: String,
    var requestedTo: String,
    var toDate: String,
    var fromDate: String,
    var is_hod_permission_granted: Boolean,
    var is_principal_permission_granted: Boolean
) {
    var leaveSentDate: String = ""

    constructor() : this("", "", "", "", "", "", "", false, false)
}