package `in`.kit.college_management_system.model

data class FacultyOrHODDetails(
    var uid:String,
    var name: String,
    var email: String,
    var branch: String,
    var photo_url: String
) {

    constructor() : this("","", "", "", "")

}