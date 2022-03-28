package `in`.kit.college_management_system.utils

import `in`.kit.college_management_system.facultySection.model.ClassesModel
import `in`.kit.college_management_system.facultySection.model.FacultyDetails
import `in`.kit.college_management_system.interfaces.OnFirebaseActionCallback
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FirebaseHelperClass {

    fun getFacultyRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("faculty")

    fun getHodRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("hod")

    fun getStudentRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("student")

    fun getPrincipalRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("principal")

    fun getClassesRef(): DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("classes")

    fun getFacultyDetails(mAuth: FirebaseAuth, onFirebaseActionCallback: OnFirebaseActionCallback) {
        getFacultyRef().child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val facultyDetails = snapshot.getValue(FacultyDetails::class.java)
                    onFirebaseActionCallback.getAllFacultyDetailsCallback(facultyDetails!!)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun addClassToFirebase(
        classNameOrSubName: String,
        subjectCode: String,
        selectedBranch: String,
        selectedSem: String,
        batchOrYear: Int,
        uid: String?,
        context: Context
    ) {

        val classDetails = HashMap<String, Any>()
        classDetails["className"] = classNameOrSubName
        classDetails["subjectCode"] = subjectCode
        classDetails["branch"] = selectedBranch
        classDetails["batchOrYear"] = batchOrYear.toString()
        classDetails["sem"] = selectedSem
        classDetails["facultyUid"] = uid!!

        val child =
            getClassesRef().child(selectedBranch).child(batchOrYear.toString()).child(selectedSem)
                .child(uid)
        val newKey = child.push()
        newKey.setValue(classDetails).addOnCompleteListener {
            if (it.isSuccessful) Toast.makeText(
                context,
                "Class added successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getAllClassesFromFirebase(
        mAuth: FirebaseAuth,
        branch: String,
        onFirebaseActionCallback: OnFirebaseActionCallback, context: Context
    ) {

        getClassesRef().child(branch).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (batches in snapshot.children) {
                        //iterate through all batches
                        Log.d("batches11", "onDataChange: ${batches.key} ")
                        for (sem in batches.children) {
                            //iterate through semesters
                            Log.d("sem222", "onDataChange: $sem ")
                            for (faculty in sem.children) {
                                // iterate through faculty
                                Log.d("faculty222", "onDataChange: ${sem.children} ")
                                if (mAuth.uid.toString() == faculty.child(mAuth.uid.toString()).key) {
                                    for (classes in faculty.children) {
                                        //iterate through classes - i.e., how many classes they are taking in this sem
                                        //this will be happen in rare case
                                        for (classDetails in classes.children) {
                                            // classDetails iteration
                                            val className =
                                                classDetails.child("className").value.toString()
                                            val batchOrYear =
                                                classDetails.child("batchOrYear").value.toString()
                                            val _branch =
                                                classDetails.child("branch").value.toString()
                                            val _sem = classDetails.child("sem").value.toString()
                                            val subjectCode =
                                                classDetails.child("subjectCode").value.toString()
                                            val classModel = ClassesModel(
                                                classDetails.key.toString(),
                                                className,
                                                subjectCode,
                                                _sem,
                                                _branch,
                                                batchOrYear
                                            )

                                            Log.d("ClassesDetails", "onDataChange: $classModel ")
                                            onFirebaseActionCallback.getAllClassesCallback(
                                                classModel, context
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun filterClassesFromFirebase(
        mAuth: FirebaseAuth,
        branch: String,
        batch: String,
        sem: String,
        context: Context,
        onFirebaseActionCallback: OnFirebaseActionCallback,
    ) {
        getClassesRef().child(branch).child(batch).child(sem).child(mAuth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (classes in snapshot.children) {
                        // classDetails iteration
                        val classModel =
                            classes.getValue(ClassesModel::class.java)
                        classModel?.key = classes.key.toString()
                        //Log.d("filterdClasses", "onDataChange:$classModel ")

                        onFirebaseActionCallback.getFilteredClass(classModel!!, context)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}