package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.databinding.RecyclerAllClassesItemsBinding
import `in`.kit.college_management_system.facultySection.activity.StudentInCLassActivity
import `in`.kit.college_management_system.facultySection.model.ClassesModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ClassesAdapter(var context: Context) :
    ListAdapter<ClassesModel, ClassesAdapter.ClassAdapterViewHolder>(AllClassesCallback()) {


    class ClassAdapterViewHolder(var binding: RecyclerAllClassesItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(_class: ClassesModel, context: Context) {
            binding.classNameTV.text = _class.className
            binding.subjectCodeTV.text = _class.subjectCode
            val batch =
                _class.batchOrYear[_class.batchOrYear.length - 2].toString() + _class.batchOrYear[_class.batchOrYear.length - 1].toString()
            binding.semTv.text = "$batch - ${_class.sem}"
            binding.batchTV.text = "${batch}th Batch"

            binding.root.setOnClickListener {
                context.startActivity(Intent(context, StudentInCLassActivity::class.java))
            }
        }

    }

    class AllClassesCallback : DiffUtil.ItemCallback<ClassesModel>() {
        override fun areItemsTheSame(oldItem: ClassesModel, newItem: ClassesModel): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: ClassesModel, newItem: ClassesModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassAdapterViewHolder {
        return ClassAdapterViewHolder(
            RecyclerAllClassesItemsBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ClassAdapterViewHolder, position: Int) {
        holder.bind(getItem(position), context)
    }
}