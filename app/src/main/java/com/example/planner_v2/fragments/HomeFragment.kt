package com.example.planner_v2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planner_v2.databinding.FragmentHomeBinding
import com.example.planner_v2.utils.ToDoAdapter
import com.example.planner_v2.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextBtnClickListeners,
    ToDoAdapter.ToDoAdapterClicksInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private  var popupFragment: AddTodoPopupFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList:MutableList<ToDoData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }
    private fun registerEvents(){
        // nut addBtnHome
        binding.addTaskBtn.setOnClickListener(){
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                AddTodoPopupFragment.TAG)
        }

        // dang xuat khoi game
        binding.logout.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun init(view: View){
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.mainRecyclerView.adapter = adapter
    }


    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                mList.clear()
                for (taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let {
                        ToDoData(it , taskSnapshot.value.toString() )
                    }
                    if (todoTask != null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }


        })
    }


    override fun onSaveTask(todoPopup: String, todo: TextInputEditText) {

        databaseRef
            .push().setValue(todoPopup)
            .addOnCompleteListener(){
            if (it.isSuccessful){
                Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                todo.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            //popupFragment.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                todoEt.text = null
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            popupFragment!!.dismiss()
        }
    }




    override fun onDeleteTaskBtnClicked(toDoData: ToDoData, position: Int) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()


        popupFragment = AddTodoPopupFragment.newInstance(toDoData.taskId, toDoData.task)
        popupFragment!!.setListener(this)
        popupFragment!!.show(childFragmentManager, AddTodoPopupFragment.TAG)
    }



}

