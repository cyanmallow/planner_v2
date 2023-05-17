package com.example.planner_v2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.planner_v2.R
import com.example.planner_v2.databinding.FragmentAddTodoPopupBinding
import com.example.planner_v2.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText


class AddTodoPopupFragment : DialogFragment() {


    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener : DialogNextBtnClickListeners
    private var toDoData : ToDoData? = null

    fun setListener(listener: DialogNextBtnClickListeners){
        this.listener = listener
    }

    companion object{
        const val TAG = "AddTodoPopupFragment"

        @JvmStatic
        fun newInstance(taskId: String, task: String) = AddTodoPopupFragment().apply {
            arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null){
            toDoData = ToDoData(arguments?.getString("taskId")
                .toString(), arguments?.getString("task").toString())

            binding.todoEt.setText(toDoData?.taskId)
        }
        registerEvents()
    }

    private fun registerEvents(){
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.todoEt.text.toString()

            if (todoTask.isNotEmpty()){
                if (toDoData == null){
                    listener.onSaveTask(todoTask, binding.todoEt)
                } else {
                    toDoData!!.task = todoTask
                    //
                    listener?.onUpdateTask(toDoData!!, binding.todoEt)
                }
            }

            // todo_Field
            //listener.onSaveTask(todoTask, binding.todoEt)

        }

        //button to close the pop up
        //binding.todoClose.setO...
        //dismiss()
    }

    interface DialogNextBtnClickListeners{
        fun  onSaveTask( todo : String, todoEt : TextInputEditText)
        fun  onUpdateTask( toDoData: ToDoData, todoEt : TextInputEditText)

    }

}