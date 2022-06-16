package `in`.kit.college_management_system.studentSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.FragmentStudentLeavesBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment


class StudentLeavesFragment : Fragment() {

    private var _binding: FragmentStudentLeavesBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStudentLeavesBinding.inflate(inflater, container, false)
        val view = binding?.root

        binding?.noLeavesLottie?.playAnimation()
        binding?.noLeavesLottie?.loop(true)

        binding?.requestLeavesFloatingBtn?.setOnClickListener {
            val bottomSheetSendLeaves = BottomSheetSendLeaveRequests(requireContext())
            bottomSheetSendLeaves.show(childFragmentManager, "bottomSheetSendLeaves")
        }

        return view
    }
}