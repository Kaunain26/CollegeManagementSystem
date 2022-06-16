package `in`.kit.college_management_system.facultySection.adapter

import `in`.kit.college_management_system.databinding.RecyclerAllClassesItemsBinding
import `in`.kit.college_management_system.facultySection.activity.StudentInClassActivity
import `in`.kit.college_management_system.facultySection.model.ClassesModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

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
            binding.batchAndsemTv.text = "$batch - ${_class.sem}"
            //binding.batchTV.text = "${batch}th Batch"

            binding.root.setOnClickListener {
                val gson = Gson()
                val classModelGson: String = gson.toJson(_class)
                context.startActivity(Intent(context, StudentInClassActivity::class.java).apply {
                    putExtra("batch_and_sem", binding.batchAndsemTv.text.toString())
                    putExtra("classModelGson", classModelGson)
                })
            }
        }

    }

    class AllClassesCallback : DiffUtil.ItemCallback<ClassesModel>() {
        override fun areItemsTheSame(oldItem: ClassesModel, newItem: ClassesModel): Boolean {
            return oldItem.classSubKey == newItem.classSubKey
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