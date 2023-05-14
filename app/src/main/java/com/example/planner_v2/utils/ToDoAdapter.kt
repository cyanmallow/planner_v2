package com.example.planner_v2.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.planner_v2.databinding.EachTodoItemBinding

class ToDoAdapter (private val list: MutableList<ToDoData>) :
    RecyclerView.Adapter<ToDoAdapter.TodoViewHolder>(){

    private var listener: ToDoAdapterClicksInterface? = null
    fun setListener(listener: ToDoAdapterClicksInterface){
        this.listener = listener
    }


    inner class TodoViewHolder(val binding: EachTodoItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder){
            with(list[position]){

                // note, this part taskid or task idk
                binding.todoTask.text = this.taskId

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this , position)

                }

                binding.editTask.setOnClickListener{
                    listener?.onEditItemClicked(this , position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface ToDoAdapterClicksInterface{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData , position : Int)
        fun onEditItemClicked(toDoData: ToDoData , position : Int)
    }

}