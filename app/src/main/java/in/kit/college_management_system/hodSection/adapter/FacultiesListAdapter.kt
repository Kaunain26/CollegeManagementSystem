package `in`.kit.college_management_system.hodSection.adapter

import `in`.kit.college_management_system.R
import `in`.kit.college_management_system.databinding.RecyclerFacultiesProfileItemsBinding
import `in`.kit.college_management_system.model.FacultyOrHODDetails
import `in`.kit.college_management_system.utils.SplitAndGetNameInitials
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FacultiesListAdapter(var context: Context) :
    ListAdapter<FacultyOrHODDetails, FacultiesListAdapter.FacultyListHolder>(
        FacultiesListItemCallback()
    ) {


    class FacultiesListItemCallback : DiffUtil.ItemCallback<FacultyOrHODDetails>() {
        override fun areItemsTheSame(
            oldItem: FacultyOrHODDetails,
            newItem: FacultyOrHODDetails
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: FacultyOrHODDetails,
            newItem: FacultyOrHODDetails
        ): Boolean {
            return oldItem == newItem
        }

    }

    class FacultyListHolder(val binding: RecyclerFacultiesProfileItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FacultyOrHODDetails?, context: Context) {
            binding.name.text = item?.name
            binding.designation.text = "Assistant Professor"
            setProfilePic(item, context)
        }

        private fun setProfilePic(item: FacultyOrHODDetails?, context: Context) {
            if (item?.photo_url != "") {
                Glide.with(context).load(item!!.photo_url)
                    .placeholder(R.drawable.student_avatar).error(R.drawable.student_avatar)
                    .into(binding.profileImg)
            } else {
                var nameInitials = ""
                SplitAndGetNameInitials().apply {
                    val splitNameList = splitName(item.name)
                    nameInitials = getNameInitials(splitNameList)
                }
                binding.nameInitialsTV.text = nameInitials
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacultyListHolder {
        return FacultyListHolder(
            RecyclerFacultiesProfileItemsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FacultyListHolder, position: Int) {
        holder.bind(getItem(position), context)
    }
}