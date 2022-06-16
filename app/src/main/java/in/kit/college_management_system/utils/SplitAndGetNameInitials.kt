package `in`.kit.college_management_system.utils

class SplitAndGetNameInitials {

    fun splitName(name: String): List<String> {
        return name.split(" ")
    }

    fun getNameInitials(splitNameList: List<String>): String {
        val firstChar = splitNameList[0][0]
        var secondChar = ""
        if (splitNameList.size > 1) {
            secondChar = splitNameList[1][0].toString()
        }
        return "$firstChar$secondChar"
    }

}