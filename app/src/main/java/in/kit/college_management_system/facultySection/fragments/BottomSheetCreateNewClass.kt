package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.BottomSheetCreateNewClassBinding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.util.*

class BottomSheetCreateNewClass(var mContext: Context) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetCreateNewClassBinding? = null
    private val binding get() = _binding
    private var selectedBranchPos = 0
    private var selectedSemPos = 0
    private var selectedYear = 0
    private var selectedBranch = ""
    private var selectedSem = ""
    lateinit var listener: OnAddClassListener


    interface OnAddClassListener {
        fun onAddClass(
            classNameOrSubName: String,
            subjectCode: String,
            selectedYear: Int,
            selectedBranchPos: Int,
            selectedSemPos: Int,
            selectedSem: String,
            selectedBranch: String,
            context: Context
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetCreateNewClassBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.rlBack?.setOnClickListener {
            dismiss()
        }

        openYearPicker()

        handleSemMenuOption()
        handleBranchMenuOption()

        binding?.createClassBtn?.setOnClickListener {
            val classNameOrSubName = binding?.etClassNameOrSubName?.text?.trim().toString()
            val subjectCode = binding?.etSubjectCode?.text?.trim().toString()

            if (validateFields(classNameOrSubName, subjectCode)) {
                listener.onAddClass(
                    classNameOrSubName,
                    subjectCode,
                    selectedYear,
                    selectedBranchPos,
                    selectedSemPos,
                    selectedSem,
                    selectedBranch,
                    mContext
                )
            }

        }

        return view

    }

    private fun validateFields(classNameOrSubName: String, subjectCode: String): Boolean {
        return if (classNameOrSubName.length >= 3 && classNameOrSubName.isNotEmpty()) {
            binding?.tilClassNameOrSubName?.error = null
            binding?.tilClassNameOrSubName?.endIconDrawable =
                ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
            if (classNameOrSubName.length > 40) {
                binding?.tilClassNameOrSubName?.error = "Name length is not in range."
                false
            } else {
                /*binding?.tilClassNameOrSubName?.error = null
                binding?.tilClassNameOrSubName?.endIconDrawable =
                    ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)*/

                //sub code validation
                if (subjectCode.isNotEmpty() && subjectCode.length in 6..7) {
                    binding?.tilSubCode?.error = null
                    binding?.tilSubCode?.endIconDrawable =
                        ContextCompat.getDrawable(activity as Context, R.drawable.ic_check_circle)
                    if (subjectCode.length > 7) {
                        binding?.tilSubCode?.error = "Subject code is not in range."
                        false
                    } else {

                        //Branch validation
                        if (selectedBranch.isNotEmpty()) {
                            binding?.chooseBranchMenu?.error = null

                            //sem validation
                            if (selectedSem.isNotEmpty()) {
                                binding?.chooseSemesterMenu?.error = null

                                //year validation
                                if (selectedYear != 0) {
                                    true
                                } else {
                                    Toast.makeText(
                                        activity as Context,
                                        "Please choose batch",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    false
                                }
                            } else {
                                binding?.chooseSemesterMenu?.error = "Please choose semester"
                                false
                            }
                        } else {
                            binding?.chooseBranchMenu?.error = "Please choose branch"
                            false
                        }
                    }

                } else {
                    binding?.tilSubCode?.error = "Subject code is not in range."
                    false
                }
            }

        } else {
            binding?.tilClassNameOrSubName?.error = "Minimum required length is 3"
            false
        }
    }

    private fun handleSemMenuOption() {
        val semList = listOf(
            "1st Sem",
            "2nd Sem",
            "3rd Sem",
            "4th Sem",
            "5th Sem",
            "6th Sem",
            "7th Sem",
            "8th Sem"
        )
        val semListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, semList)
        (binding?.chooseSemesterMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            semListArrayAdapter
        )

        (binding?.chooseSemesterMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                selectedSem = p0.getItemAtPosition(position).toString()
                selectedSemPos = position

            }
    }

    private fun handleBranchMenuOption() {
        val branchList =
            listOf("BS - Basic Science", "CS", "EC", "EE", "ME", "AE", "CV")
        val branchListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, branchList)
        (binding?.chooseBranchMenu?.editText as? AutoCompleteTextView)?.setAdapter(
            branchListArrayAdapter
        )

        (binding?.chooseBranchMenu!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                //0 -> BS - Basic Science
                //1 -> CSE
                //2 -> E&C
                //3 -> EE
                //4 -> Mech
                //5 -> Aero
                //6 -> Civil
                val branchName = p0.getItemAtPosition(position).toString()
                selectedBranch = if (branchName == "BS - Basic Science") {
                    branchName.split(" ")[0]
                } else {
                    branchName
                }

                selectedBranchPos = position

            }
    }

    private fun openYearPicker() {
        binding?.yearPickerCard?.setOnClickListener {
            val today = Calendar.getInstance()

            val builder = MonthPickerDialog.Builder(
                activity as Context,
                { selectedMonth, selectedYear -> },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH)
            )

            builder.setActivatedMonth(Calendar.JULY)
                .setMinYear(2018)
                .setActivatedYear(2022)
                .setMaxYear(2050)
                .setTitle("Select Batch Year")
                .showYearOnly()
                .setOnYearChangedListener {
                    //Toast.makeText(activity as Context, "$it", Toast.LENGTH_SHORT).show()
                    binding?.batchTV?.text = "Batch - $it"
                    selectedYear = it
                }
                .build()
                .show()

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnAddClassListener
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}
