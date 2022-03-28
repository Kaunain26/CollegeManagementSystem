package `in`.kit.college_management_system.interfaces

import `in`.kit.college_management_system.facultySection.model.ClassesModel
import `in`.kit.college_management_system.facultySection.model.FacultyDetails
import android.content.Context

interface OnFirebaseActionCallback {

    fun getAllFacultyDetailsCallback(facultyDetails: FacultyDetails) {}
    fun getAllClassesCallback(classModel: ClassesModel, context: Context) {}
    fun getFilteredClass(classModel: ClassesModel, context: Context) {}
}