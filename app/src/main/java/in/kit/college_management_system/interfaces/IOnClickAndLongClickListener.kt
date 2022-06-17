package `in`.kit.college_management_system.interfaces

import `in`.kit.college_management_system.model.StudentDetailsModel
import java.util.concurrent.CopyOnWriteArrayList

interface IOnClickAndLongClickListener {

    fun onStudentSelected(studentDataList: CopyOnWriteArrayList<StudentDetailsModel>, studentListSize: Int)

}