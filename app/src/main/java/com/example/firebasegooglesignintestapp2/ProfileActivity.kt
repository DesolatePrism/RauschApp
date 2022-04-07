package com.example.firebasegooglesignintestapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasegooglesignintestapp2.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    //view binding

    private lateinit var binding: ActivityProfileBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click logout

        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        binding.uploadBtn.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        /*
        binding.basicBtn.setOnClickListener{
            startActivity(Intent(this, Basic_Activity::class.java))
        }*/

        binding.tabbedBtn.setOnClickListener{
            startActivity(Intent(this, Tabbed_Activity::class.java))
        }

    }

    private fun checkUser() {
      //get current user
        var firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //user not logged in
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }
        else
        {
            //user logged in
            //get user info
            val email = firebaseUser.email
            binding.emailTv.text = email
        }
    }
}