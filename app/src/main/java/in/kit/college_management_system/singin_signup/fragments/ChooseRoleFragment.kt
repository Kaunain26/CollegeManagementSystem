package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentChooseRoleBinding
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ChooseRoleFragment : Fragment() {

    private var _binding: FragmentChooseRoleBinding? = null
    private val binding get() = _binding
    private var selectedRole = 0L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseRoleBinding.inflate(inflater, container, false)
        val view = binding!!.root


        binding?.floatingNext?.visibility = View.INVISIBLE

        binding?.arrowBackIV?.setOnClickListener {
            (activity as AppCompatActivity).onBackPressed()
        }

        val menuOptions = listOf("Student", "Principal", "HOD", "Faculty")
        val menuListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, menuOptions)
        (binding?.chooseRoleMenu!!.editText as? AutoCompleteTextView)?.setAdapter(
            menuListArrayAdapter
        )

        (binding?.chooseRoleMenu!!.editText as? AutoCompleteTextView)?.setOnDismissListener {
            if (binding?.chooseRoleMenu!!.editText?.text!!.isEmpty()) {
                binding?.chooseRoleMenu!!.editText?.clearFocus()
                binding?.floatingNext?.visibility = View.GONE
            }
        }

        (binding?.chooseRoleMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, position ->
                binding?.floatingNext?.visibility = View.VISIBLE

                // ...  Positions ... //

                //0 -> Student
                //1 -> Principal
                //2 -> HOD
                //3 -> Faculty

                selectedRole = position

            }


        onClickNextBtn()


        return view
    }

    private fun onClickNextBtn() {
        binding?.floatingNext?.setOnClickListener {
            // if (!isStudent) {
            val bundle = Bundle()
            bundle.putLong("role", selectedRole)
            val chooseSignInOrSignUpFrag = ChooseSignInOrSignUpFrag()
            chooseSignInOrSignUpFrag.arguments = bundle
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    chooseSignInOrSignUpFrag,
                    "chooseSignInOrSignUpFrag"
                ).commit()
            // }
        }
    }

}