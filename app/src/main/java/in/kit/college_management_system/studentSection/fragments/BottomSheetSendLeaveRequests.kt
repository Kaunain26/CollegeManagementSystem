package `in`.kit.college_management_system.studentSection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.BottomSheetLeaveRequestBinding
import `in`.kit.college_management_system.utils.CalendarHelperClass
import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class BottomSheetSendLeaveRequests(private var mContext: Context) : BottomSheetDialogFragment() {
    private var _binding: BottomSheetLeaveRequestBinding? = null
    private val binding get() = _binding
    private var selectedDaysForLeavePos = 0
    private var selectedLeaveType = ""
    private var selectedDaysForLeave = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetLeaveRequestBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.tiLChooseToDateOfLeave?.isEnabled = false
        binding?.tiLChooseFromDateOfLeave?.isEnabled = false

        handleLeaveTypeMenuOption()
        handleNumberOfDaysMenuOption()

        getFromDateOfLeave()

        getToDateOfLeave()

        binding?.rlBack?.setOnClickListener {
            dismiss()
        }

        binding?.rlSendLeaveRequest?.setOnClickListener {
            if (selectedDaysForLeave == "2 or More than 2") {
                if (validateAllFields()) {
                    Toast.makeText(mContext, "validated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "Please fill all required fields", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                if (validateOnlyTopThreeFields()) {
                    Toast.makeText(mContext, "validated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "Please fill all required fields", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return view
    }

    @SuppressLint("RestrictedApi")
    private fun getToDateOfLeave() {
        (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setOnClickListener {
            val mCalendar = Calendar.getInstance()
            val mYear: Int = mCalendar.get(Calendar.YEAR)
            val mMonth: Int = mCalendar.get(Calendar.MONTH)
            val mDayOfMonth: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                mContext,
                { p0, year, month, dayOfTheMonth ->
                    val dateFromDatePicker =
                        getDateFromDatePicker(year, month, dayOfTheMonth, mCalendar)
                    (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setText("$dateFromDatePicker")
                    showWarningNote()
                },
                mYear, mMonth, mDayOfMonth
            )
            datePickerDialog.show()
        }
    }

    private fun getFromDateOfLeave() {
        (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setOnClickListener {
            val mCalendar = Calendar.getInstance()
            val mYear: Int = mCalendar.get(Calendar.YEAR)
            val mMonth: Int = mCalendar.get(Calendar.MONTH)
            val mDayOfMonth: Int = mCalendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                mContext,
                { p0, year, month, dayOfTheMonth ->
                    val dateFromDatePicker =
                        getDateFromDatePicker(year, month, dayOfTheMonth, mCalendar)
                    (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                        "$dateFromDatePicker"
                    )
                    showWarningNote()
                },
                mYear, mMonth, mDayOfMonth
            )
            datePickerDialog.show()
        }
    }

    private fun getDateFromDatePicker(
        year: Int,
        month: Int,
        dayOfTheMonth: Int,
        mCalendar: Calendar
    ): String {
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfTheMonth
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.YEAR] = year

        return CalendarHelperClass().getFormattedDate(mCalendar.time, "dd MMMM yyyy")
    }

    private fun handleLeaveTypeMenuOption() {
        val leaveTypeList = listOf("Casual", "Sick")
        val leaveTypeListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, leaveTypeList)
        (binding?.chooseLeaveType?.editText as? AutoCompleteTextView)?.setAdapter(
            leaveTypeListArrayAdapter
        )

        (binding?.chooseLeaveType!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                selectedLeaveType = p0.getItemAtPosition(position).toString()

            }
    }

    private fun handleNumberOfDaysMenuOption() {
        val numberOfDaysLeaveList = listOf("Half Day", "Full day", "2 or More than 2")
        val numberOfDaysListArrayAdapter =
            ArrayAdapter(activity as Context, R.layout.choose_item_layout, numberOfDaysLeaveList)
        (binding?.tiLChooseDaysOfLeave?.editText as? AutoCompleteTextView)?.setAdapter(
            numberOfDaysListArrayAdapter
        )

        (binding?.tiLChooseDaysOfLeave!!.editText as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, p2 ->
                // ...  Positions ... //

                selectedDaysForLeave = p0.getItemAtPosition(position).toString()
                selectedDaysForLeavePos = position
                binding?.llNote?.isVisible = true
                binding?.noteText1?.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.green
                    )
                )
                when (position) {
                    0 -> {
                        //half day
                        binding?.noteText1?.text = getString(R.string.half_day_leave_waring)
                        binding?.noteText2?.text = getString(R.string.faculty_note)
                        binding?.tiLChooseToDateOfLeave?.isEnabled = false
                        binding?.tiLChooseFromDateOfLeave?.isEnabled = false
                        (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                        (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                    }
                    1 -> {
                        //full day
                        binding?.noteText1?.text = getString(R.string.full_day_leave_waring)
                        binding?.noteText2?.text = getString(R.string.hod_note)
                        binding?.tiLChooseToDateOfLeave?.isEnabled = false
                        binding?.tiLChooseFromDateOfLeave?.isEnabled = false
                        (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                        (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                    }
                    2 -> {
                        // 2 or more than 2 days
                        binding?.llNote?.isVisible = false
                        Toast.makeText(
                            mContext,
                            "Please select dates for leave.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.tiLChooseToDateOfLeave?.helperText = "Required*"
                        binding?.tiLChooseFromDateOfLeave?.helperText = "Required*"
                        binding?.tiLChooseToDateOfLeave?.isEnabled = true
                        binding?.tiLChooseFromDateOfLeave?.isEnabled = true
                    }
                    else -> {
                        binding?.tiLChooseToDateOfLeave?.helperText = ""
                        binding?.tiLChooseFromDateOfLeave?.helperText = ""
                        binding?.tiLChooseToDateOfLeave?.isEnabled = false
                        binding?.tiLChooseFromDateOfLeave?.isEnabled = false
                    }
                }
            }
    }

    private fun validateOnlyTopThreeFields(): Boolean {
        return if (selectedLeaveType.isNotEmpty()) {
            binding?.chooseLeaveType?.isErrorEnabled = false
            binding?.chooseLeaveType?.error = null
            val causeOfLeave = binding?.etCauseOfLeave?.text!!.toString()
            if (causeOfLeave.length <= 150 && causeOfLeave.isNotEmpty()) {
                binding?.tilCauseOfLeave?.isErrorEnabled = true
                binding?.tilCauseOfLeave?.error = null
                if (selectedDaysForLeave.isNotEmpty()) {
                    binding?.tiLChooseDaysOfLeave?.isErrorEnabled = false
                    binding?.tiLChooseDaysOfLeave?.error = null
                    true
                } else {
                    binding?.tiLChooseDaysOfLeave?.isErrorEnabled = true
                    binding?.tiLChooseDaysOfLeave?.error = "Please select days for leaves."
                    false
                }
            } else {
                binding?.tilCauseOfLeave?.isErrorEnabled = true
                binding?.tilCauseOfLeave?.error = "Leave cause length is not in range."
                false
            }
        } else {
            binding?.chooseLeaveType?.isErrorEnabled = true
            binding?.chooseLeaveType?.error = "Leave type is required."
            false
        }
    }

    private fun validateAllFields(): Boolean {
        return if (selectedLeaveType.isNotEmpty()) {
            binding?.chooseLeaveType?.isErrorEnabled = false
            binding?.chooseLeaveType?.error = null
            val causeOfLeave = binding?.etCauseOfLeave?.text!!.toString()
            if (causeOfLeave.length <= 100 && causeOfLeave.isNotEmpty()) {
                binding?.tilCauseOfLeave?.isErrorEnabled = true
                binding?.tilCauseOfLeave?.error = null
                if (selectedDaysForLeave.isNotEmpty()) {
                    binding?.tiLChooseDaysOfLeave?.isErrorEnabled = false
                    binding?.tiLChooseDaysOfLeave?.error = null
                    if ((binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.text.toString()
                            .isNotEmpty()
                    ) {
                        binding?.tiLChooseFromDateOfLeave?.isErrorEnabled = false
                        binding?.tiLChooseFromDateOfLeave?.error = null

                        if ((binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.text.toString()
                                .isNotEmpty()
                        ) {
                            binding?.tiLChooseToDateOfLeave?.isErrorEnabled = false
                            binding?.tiLChooseToDateOfLeave?.error = null
                            true
                        } else {
                            binding?.tiLChooseToDateOfLeave?.isErrorEnabled = true
                            binding?.tiLChooseToDateOfLeave?.error = "Choose date."
                            false
                        }
                    } else {
                        binding?.tiLChooseFromDateOfLeave?.isErrorEnabled = true
                        binding?.tiLChooseFromDateOfLeave?.error = "Choose date."
                        false
                    }
                } else {
                    binding?.tiLChooseDaysOfLeave?.isErrorEnabled = true
                    binding?.tiLChooseDaysOfLeave?.error = "Please select days for leaves."
                    false
                }
            } else {
                binding?.tilCauseOfLeave?.isErrorEnabled = true
                binding?.tilCauseOfLeave?.error = "Leave cause length is not in range."
                false
            }
        } else {
            binding?.chooseLeaveType?.isErrorEnabled = true
            binding?.chooseLeaveType?.error = "Leave type is required."
            false
        }
    }

    private fun showWarningNote() {
        val chooseFromDateOfLeave =
            (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.text.toString()
        val chooseToDateOfLeave =
            (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.text.toString()
        if (chooseToDateOfLeave.isNotEmpty() && chooseFromDateOfLeave.isNotEmpty()) {
            binding?.llNote?.isVisible = true

            val currentDate =
                CalendarHelperClass().getDateOfTheMonth(chooseFromDateOfLeave, "dd MMMM yyyy")
            val nextDayDate =
                CalendarHelperClass().getDateOfTheMonth(chooseToDateOfLeave, "dd MMMM yyyy")

            if (currentDate < nextDayDate) {
                when {
                    nextDayDate - currentDate == 1 -> {
                        Toast.makeText(
                            mContext,
                            "Please select 2 days or more than 2.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding?.llNote?.isVisible = false
                        (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                        (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setText(
                            ""
                        )
                    }
                    nextDayDate - currentDate == 2 -> {
                        binding?.noteText1?.text = getString(R.string.two_days_leave_waring)
                        binding?.noteText2?.text = getString(R.string.hod_note)
                        binding?.noteText1?.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.green
                            )
                        )
                    }
                    nextDayDate - currentDate == 3 -> {
                        binding?.noteText1?.text = getString(R.string.three_days_leave_waring)
                        binding?.noteText2?.text = getString(R.string.hod_note)
                        binding?.noteText1?.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.green
                            )
                        )
                    }
                    nextDayDate - currentDate > 3 -> {
                        binding?.noteText1?.text =
                            getString(R.string.more_than_three_days_leave_waring)
                        binding?.noteText2?.text = getString(R.string.hod_and_principal_note)
                        binding?.noteText1?.setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.red
                            )
                        )
                    }
                }
            } else {
                binding?.llNote?.isVisible = false
                (binding?.tiLChooseToDateOfLeave!!.editText as? AutoCompleteTextView)?.setText("")
                (binding?.tiLChooseFromDateOfLeave!!.editText as? AutoCompleteTextView)?.setText("")
                Toast.makeText(mContext, "Invalid date selected.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}