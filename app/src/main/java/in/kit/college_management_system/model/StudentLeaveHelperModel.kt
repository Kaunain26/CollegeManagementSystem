package `in`.kit.college_management_system.model

sealed class StudentLeaveHelperModel {

    class SubHeader(
        var id: Int,
        var title: String
    ) : StudentLeaveHelperModel()

    class StudentLeaveModel(
        var leaveKey: String,
        var leave_type: String,
        var leave_reason: String,
        var no_of_days: String,
        var requested_to: String,
        var to_date: String,
        var from_date: String,
        var is_hod_permission_granted: Int,
        var is_principal_permission_granted: Int,
        var leave_sent_date: String
    ) : StudentLeaveHelperModel() {
        constructor() : this("", "", "", "", "", "", "", 0, 0, "")
    }

}