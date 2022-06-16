package `in`.kit.college_management_system.facultySection.model

data class StudentDetailsModel(
    var uid: String,
    var name: String,
    var usn: String,
    var sem: String,
    var branch: String,
    var batch: Int,
    var address: String,
    var email: String,
    var gender: String,
    var photo_url: String,
) {

    var leaves: String = ""
    var isExpanded: Boolean = false
    var isSelected: Boolean = false


    constructor() : this("", "", "", "", "", 0, "", "", "", "")


}