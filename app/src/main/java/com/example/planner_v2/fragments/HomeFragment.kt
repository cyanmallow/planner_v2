package com.example.planner_v2.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.planner_v2.R
import com.example.planner_v2.databinding.ConfirmDeleteBinding
import com.example.planner_v2.databinding.FragmentHomeBinding
import com.example.planner_v2.utils.ToDoAdapter
import com.example.planner_v2.utils.ToDoData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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


//        binding.textViewSignin.setOnClickListener {
//            navControl.navigate(R.id.action_signupFragment_to_signinFragment)
//        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()

        // dang xuat
        binding.logout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Đăng xuất...", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.action_homeFragment_to_signinFragment)
        }
    }
    //phan nay la phan toolbar









    private fun registerEvents(){
        // nut addBtnHome
        binding.addTaskBtn.setOnClickListener {
            if (popupFragment != null)
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            popupFragment = AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                AddTodoPopupFragment.TAG)
        }
        // dang xuat khoi game
        // chua hoat dong
//            binding.logout.setOnClickListener {
//                FirebaseAuth.getInstance().signOut()
//        }
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

    override fun onSaveTask(todoPopup: String, todoEt: TextInputEditText) {

        databaseRef
            .push().setValue(todoPopup)
            .addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
                todoEt.text = null
                //popupFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        databaseRef.updateChildren(map).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
            //todoEt.text = null
            popupFragment!!.dismiss()
        }
    }

    // dialog hien ra man hinh confirm_delete
    fun confirmDialog(){
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Thong bao")
                .setView(R.layout.confirm_delete)
                .show()
        }
    }
    // Confirm delete
    //chua hoat dong, can them onButtonClick
    override fun onDeleteTaskBtnClicked(toDoData: ToDoData, position: Int) {
        //confirmDialog()

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

