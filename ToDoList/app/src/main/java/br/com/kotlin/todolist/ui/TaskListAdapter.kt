package br.com.kotlin.todolist.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewDebug
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.kotlin.todolist.R
import br.com.kotlin.todolist.databinding.ItemTaskBinding
import br.com.kotlin.todolist.model.Task

class TaskListAdapter :ListAdapter<Task,TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var listenerEdit : (Task) -> Unit = {}
    var listenerDelete : (Task) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater,parent,false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding:ItemTaskBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Task) {
            binding.tvTitle.text = item.title
            binding.tvDate.text = "${item.date} ${item.hour}"
            binding.idMore.setOnClickListener {
                showPopup(item)
            }

        }

        private fun showPopup(item: Task) {
            val idMore = binding.idMore
            val popupmenu = PopupMenu(idMore.context,idMore)
            popupmenu.menuInflater.inflate(R.menu.popupmenu,popupmenu.menu)
            popupmenu.setOnMenuItemClickListener {
                when (it.itemId){
                    R.id.action_edit -> listenerEdit(item)
                    R.id.action_delete -> listenerDelete(item)
                }

                return@setOnMenuItemClickListener true
            }
            popupmenu.show()

        }

    }
}

class DiffCallback : DiffUtil.ItemCallback<Task>(){
    override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id
}