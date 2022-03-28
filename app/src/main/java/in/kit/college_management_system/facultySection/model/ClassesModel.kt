package `in`.kit.college_management_system.facultySection.model

data class ClassesModel(
    var key: String,
    var className: String,
    var subjectCode: String,
    var sem: String,
    var branch: String,
    var batchOrYear: String,
) {
    constructor() : this("", "", "", "", "", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ClassesModel

        if (key != other.key) return false
        if (subjectCode != other.subjectCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + subjectCode.hashCode()
        return result
    }


}