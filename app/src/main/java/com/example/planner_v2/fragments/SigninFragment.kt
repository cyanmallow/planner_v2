package com.example.planner_v2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.planner_v2.R
import com.example.planner_v2.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth


class SigninFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //inflate
        binding = FragmentSigninBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        binding.textViewSignUp.setOnClickListener {
            Toast.makeText(context, "Chuyển đến đăng ký...", Toast.LENGTH_SHORT).show()
            navControl.navigate(R.id.action_signinFragment_to_signupFragment)
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val pass = binding.passEt.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginUser(email, pass)
            } else {
                Toast.makeText(context, "Chưa điền đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful)
                navControl.navigate(R.id.action_signinFragment_to_homeFragment)
            else
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(view: View) {
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }
}

class AppBar : AppCompatActivity()