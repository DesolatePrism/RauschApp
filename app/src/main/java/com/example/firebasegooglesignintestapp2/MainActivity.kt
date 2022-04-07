package com.example.firebasegooglesignintestapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasegooglesignintestapp2.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firebaseAuth: FirebaseAuth

    //constants

    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure GoogleSignIn
        val googleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString( R.string.default_web_client_id))
                .requestEmail() //only need email
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()


        //signin button
        binding.googleSignInBtn.setOnClickListener {
            Log.d(TAG, "onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    private fun checkUser() {
        //check is user logged in or not
        var firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user is logged in.
            //start profile activity
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (super.onActivityResult(requestCode, resultCode, data))

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google SignIn succes, now FireBase
                val account =  accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)

            } catch (e: Exception) {
                Log.d(TAG, "onActivityResult: ${e.message}")
            }


        }

    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")
        var credential = GoogleAuthProvider.getCredential(account!!.idToken,null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser!!.email
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                if(authResult.additionalUserInfo!!.isNewUser){
                    //user is new, account created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created... \n$email")
                    Toast.makeText(this@MainActivity, "Account created...\n$email", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    //user is existing
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing user... \n$email")
                    Toast.makeText(this@MainActivity, "Logged in...\n$email", Toast.LENGTH_SHORT).show()
                }

                //start profile activity
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                finish()
            }
                //login failed
            .addOnFailureListener{ e-> Log.d(TAG, "firebaseAuthWithGoogleAccount: Logging in failed due to ${e.message}")
                Toast.makeText(this@MainActivity, "Logging in failed due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

}