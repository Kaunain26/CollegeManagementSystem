package `in`.kit.college_management_system.facultySection.activity

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.utils.MakeStatusBarTransparent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class StudentInCLassActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_in_class)

        MakeStatusBarTransparent().transparent(this)

    }
}