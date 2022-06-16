package `in`.kit.college_management_system.facultySection.model

data class ClassesModel(
    var classSubKey: String,
    var className: String,
    var subjectCode: String,
    var sem: String,
    var branch: String,
    var batchOrYear: String,
) {
    var facultyUid = ""
    var totalAttendancePercentage = "0" //For student section only

    constructor() : this("", "", "", "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassesModel

        if (classSubKey != other.classSubKey) return false
        if (subjectCode != other.subjectCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = classSubKey.hashCode()
        result = 31 * result + subjectCode.hashCode()
        return result
    }


}