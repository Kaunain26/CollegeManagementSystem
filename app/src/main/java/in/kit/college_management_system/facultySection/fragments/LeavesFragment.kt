package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.databinding.FragmentLeavesBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class LeavesFragment : Fragment() {

    private var _binding: FragmentLeavesBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLeavesBinding.inflate(inflater, container, false)
        val view = binding?.root



        return view
    }

}