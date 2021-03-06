package `in`.kit.college_management_system.facultySection.fragments

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.BottomSheetFilterClassesBinding
import `in`.kit.college_management_system.model.ClassesModel
import `in`.kit.college_management_system.utils.Branches.AERO
import `in`.kit.college_management_system.utils.Branches.BS
import `in`.kit.college_management_system.utils.Branches.CIVIL
import `in`.kit.college_management_system.utils.Branches.CSE
import `in`.kit.college_management_system.utils.Branches.EC
import `in`.kit.college_management_system.utils.Branches.EE
import `in`.kit.college_management_system.utils.Branches.MECH
import `in`.kit.college_management_system.utils.FirebaseHelperClass
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.CopyOnWriteArrayList

class BottomSheetFilterClasses(
    var classList: CopyOnWriteArrayList<ClassesModel>,
    var branch: String,
    var mContext: Context
) :
    BottomSheetDialogFragment() {
    private var _binding: BottomSheetFilterClassesBinding? = null
    private val binding get() = _binding
    lateinit var listener: OnSaveFilter
    private var selectedCSEChipList = ArrayList<String>()
    private var selectedMechChipList = ArrayList<String>()
    private var selectedAeroChipList = ArrayList<String>()
    private var selectedCivilChipList = ArrayList<String>()
    private var selectedEEChipList = ArrayList<String>()
    private var selectedEandCChipList = ArrayList<String>()
    private var selectedBasicScienceChipList = ArrayList<String>()
    private var allSelectedChip = ArrayList<String>()
    private lateinit var firebaseHelperClass: FirebaseHelperClass
    private lateinit var mAuth: FirebaseAuth

    interface OnSaveFilter {
        fun onFilterClass(allSelectedChip: ArrayList<String>, mContext: Context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFilterClassesBinding.inflate(inflater, container, false)
        val view = binding!!.root

        binding?.rlBack?.setOnClickListener {
            dismiss()
        }

        mAuth = FirebaseAuth.getInstance()
        firebaseHelperClass = FirebaseHelperClass()

        when (branch) {
            CSE -> {
                binding?.rlCSE?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            MECH -> {
                binding?.rlMech?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            AERO -> {
                binding?.rlAero?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            CIVIL -> {
                binding?.rlCivil?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            EE -> {
                binding?.rlEE?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            EC -> {
                binding?.rlEandC?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

            BS -> {
                binding?.rlBasicScience?.background =
                    ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            }

        }

        addChip(branch)

        handleBranchClickListener()

        applyFilters()

        return view
    }

    private fun applyFilters() {
        binding?.rlSave?.setOnClickListener {
            if (selectedBasicScienceChipList.isEmpty() &&
                selectedEandCChipList.isEmpty() &&
                selectedEEChipList.isEmpty() &&
                selectedCivilChipList.isEmpty() &&
                selectedAeroChipList.isEmpty() &&
                selectedMechChipList.isEmpty() &&
                selectedCSEChipList.isEmpty()
            ) {
                Toast.makeText(activity as Context, "No filters applied", Toast.LENGTH_SHORT)
                    .show()
                dismiss()
            } else {
                if (selectedCSEChipList.isNotEmpty()) {
                    for ((index, chips) in selectedCSEChipList.withIndex()) {
                        selectedCSEChipList[index] = "$chips ${CSE}"
                    }
                    allSelectedChip.addAll(selectedCSEChipList)
                }
                if (selectedMechChipList.isNotEmpty()) {
                    for ((index, chips) in selectedMechChipList.withIndex()) {
                        selectedMechChipList[index] = "$chips ${MECH}"
                    }
                    allSelectedChip.addAll(selectedMechChipList)
                }
                if (selectedAeroChipList.isNotEmpty()) {
                    for ((index, chips) in selectedAeroChipList.withIndex()) {
                        selectedAeroChipList[index] = "$chips ${AERO}"
                    }
                    allSelectedChip.addAll(selectedAeroChipList)
                }
                if (selectedEEChipList.isNotEmpty()) {
                    for ((index, chips) in selectedEEChipList.withIndex()) {
                        selectedEEChipList[index] = "$chips ${EE}"
                    }
                    allSelectedChip.addAll(selectedEEChipList)
                }
                if (selectedEandCChipList.isNotEmpty()) {
                    for ((index, chips) in selectedEandCChipList.withIndex()) {
                        selectedEandCChipList[index] = "$chips ${EC}"
                    }
                    allSelectedChip.addAll(selectedEandCChipList)
                }
                if (selectedCivilChipList.isNotEmpty()) {
                    for ((index, chips) in selectedCivilChipList.withIndex()) {
                        selectedCivilChipList[index] = "$chips ${CIVIL}"
                    }
                    allSelectedChip.addAll(selectedCivilChipList)
                }
                if (selectedBasicScienceChipList.isNotEmpty()) {
                    for ((index, chips) in selectedBasicScienceChipList.withIndex()) {
                        selectedBasicScienceChipList[index] = "$chips ${BS}"
                    }
                    allSelectedChip.addAll(selectedBasicScienceChipList)
                }
                listener.onFilterClass(allSelectedChip, mContext)
            }

        }

    }


    private fun handleBranchClickListener() {

        binding?.rlCSE!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlCSE?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvCse?.setTextColor(ContextCompat.getColor(activity as Context, R.color.white))
            binding?.filterChipGroup?.removeAllViews()
            addChip(CSE)
        }

        binding?.rlMech!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlMech?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvMech?.setTextColor(
                ContextCompat.getColor(
                    activity as Context,
                    R.color.white
                )
            )
            binding?.filterChipGroup?.removeAllViews()

            addChip(MECH)
        }

        binding?.rlCivil!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlCivil?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvCivil?.setTextColor(
                ContextCompat.getColor(
                    activity as Context,
                    R.color.white
                )
            )
            binding?.filterChipGroup?.removeAllViews()
            addChip(CIVIL)
        }

        binding?.rlAero!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlAero?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvAero?.setTextColor(
                ContextCompat.getColor(
                    activity as Context,
                    R.color.white
                )
            )
            binding?.filterChipGroup?.removeAllViews()
            addChip(AERO)
        }

        binding?.rlEE!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlEE?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvEE?.setTextColor(ContextCompat.getColor(activity as Context, R.color.white))
            binding?.filterChipGroup?.removeAllViews()
            addChip(EE)
        }

        binding?.rlEandC!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlEandC?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvEandC?.setTextColor(
                ContextCompat.getColor(
                    activity as Context,
                    R.color.white
                )
            )
            binding?.filterChipGroup?.removeAllViews()
            addChip(EC)
        }

        binding?.rlBasicScience!!.setOnClickListener {
            binding?.loadingBar?.visibility = View.VISIBLE
            setAllBranchBackgroundLightPink()
            binding?.rlBasicScience?.background =
                ContextCompat.getDrawable(activity as Context, R.color.dark_purple)
            binding?.tvBasicScience?.setTextColor(
                ContextCompat.getColor(
                    activity as Context,
                    R.color.white
                )
            )
            binding?.filterChipGroup?.removeAllViews()
            addChip(BS)// Basic Science
        }

    }

    private fun setAllBranchBackgroundLightPink() {
        binding?.rlCSE?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlAero?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlMech?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlEE?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlEandC?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlCivil?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.rlBasicScience?.background =
            ContextCompat.getDrawable(activity as Context, R.color.light_purple)
        binding?.tvCse?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvMech?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvAero?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvCivil?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvEE?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvEandC?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )
        binding?.tvBasicScience?.setTextColor(
            ContextCompat.getColor(
                activity as Context,
                R.color.night_theme_color
            )
        )

    }

    @SuppressLint("SetTextI18n")
    private fun addChip(branch: String) {
        //Log.d("classList", "addChip:$classList ")
        val chipList = ArrayList<String>()
        for (_class in classList) {
            Log.d("addChip", "addChip: $branch , ${_class.branch}")
            if (_class.branch == branch) {
                val batch =
                    _class.batchOrYear[_class.batchOrYear.length - 2].toString() + _class.batchOrYear[_class.batchOrYear.length - 1].toString()
                val sem = _class.sem.split(" ")[0]
                chipList.add("$batch - $sem")

            } else {
                binding?.noResultFound?.visibility = View.VISIBLE
                binding?.hintTv?.visibility = View.GONE
            }
        }

        // using this iteration to just remove the duplicate chips in chip group
        for (chipTag in chipList.toSet().toList()) {
            val chip = Chip(activity as Context)
            chip.text = chipTag

            chip.isCheckedIconVisible = true
            chip.isCheckable = true
            binding?.filterChipGroup?.addView(chip)

            //selecting chips if it's selected
            when (branch) {
                CSE -> {
                    if (selectedCSEChipList.isNotEmpty()) {
                        for (cseChips in selectedCSEChipList) {
                            if (chip.text.toString() == cseChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                MECH -> {
                    if (selectedMechChipList.isNotEmpty()) {
                        for (mechChips in selectedMechChipList) {
                            if (chip.text.toString() == mechChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                AERO -> {
                    if (selectedAeroChipList.isNotEmpty()) {
                        for (aeroChips in selectedAeroChipList) {
                            if (chip.text.toString() == aeroChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                CIVIL -> {
                    if (selectedCivilChipList.isNotEmpty()) {
                        for (civilChips in selectedCivilChipList) {
                            if (chip.text.toString() == civilChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                EE
                -> {
                    if (selectedEEChipList.isNotEmpty()) {
                        for (eeChips in selectedEEChipList) {
                            if (chip.text.toString() == eeChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                EC -> {
                    if (selectedEandCChipList.isNotEmpty()) {
                        for (eAndcChips in selectedEandCChipList) {
                            if (chip.text.toString() == eAndcChips) {
                                chip.isChecked = true
                            }
                        }
                    }
                }

                BS -> {
                    if (selectedBasicScienceChipList.isNotEmpty()) {
                        for (basicScienceChips in selectedBasicScienceChipList) {
                            if (chip.text.toString() == basicScienceChips) {
                                chip.isChecked = true
                            }
                        }
                    }

                }

            }

            //adding selected chips to list
            chip.setOnCheckedChangeListener { compoundButton, boolean ->
                if (boolean) {
                    // if checked then add to particular list...
                    when (branch) {
                        CSE -> {
                            selectedCSEChipList.add(chip.text.toString())
                        }

                        MECH -> {
                            selectedMechChipList.add(chip.text.toString())
                        }

                        AERO -> {
                            selectedAeroChipList.add(chip.text.toString())
                        }

                        CIVIL -> {

                            selectedCivilChipList.add(chip.text.toString())
                        }

                        EE -> {
                            selectedEEChipList.add(chip.text.toString())
                        }

                        EC -> {
                            selectedEandCChipList.add(chip.text.toString())

                        }

                        BS -> {
                            selectedBasicScienceChipList.add(chip.text.toString())

                        }

                    }

                } else {
                    when (branch) {
                        CSE -> {
                            selectedCSEChipList.remove(chip.text.toString())
                        }

                        MECH -> {
                            selectedMechChipList.remove(chip.text.toString())
                        }

                        AERO -> {
                            selectedAeroChipList.remove(chip.text.toString())
                        }

                        CIVIL -> {

                            selectedCivilChipList.remove(chip.text.toString())
                        }

                        EE -> {
                            selectedEEChipList.remove(chip.text.toString())
                        }

                        EC -> {
                            selectedEandCChipList.remove(chip.text.toString())

                        }

                        BS -> {
                            selectedBasicScienceChipList.remove(chip.text.toString())

                        }

                    }
                }
            }

            binding?.noResultFound?.visibility = View.GONE
            binding?.hintTv?.visibility = View.VISIBLE
        }
        binding?.loadingBar?.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnSaveFilter
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}