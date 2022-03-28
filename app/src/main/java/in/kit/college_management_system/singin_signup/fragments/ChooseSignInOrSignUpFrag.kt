package `in`.kit.college_management_system.singin_signup.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentChooseSignInOrSignUpBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ChooseSignInOrSignUpFrag : Fragment() {


    private var _binding: FragmentChooseSignInOrSignUpBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentChooseSignInOrSignUpBinding.inflate(inflater, container, false)
        val view = binding!!.root

        val selectedRole = arguments?.getLong("role")

        val bundle = Bundle()
        bundle.putLong("role", selectedRole!!)
        val signUpFragment = SignUpFragment()
        val signInFragment = SignInFragment()
        signUpFragment.arguments = bundle
        signInFragment.arguments = bundle

        binding?.signUpBtn?.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, signUpFragment, "signUpFragment").commit()
        }

        binding?.signInBtn?.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, signInFragment, "signInFragment").commit()
        }

        return view
    }

}