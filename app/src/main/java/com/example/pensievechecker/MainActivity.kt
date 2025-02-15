package com.example.pensievechecker

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import androidx.appcompat.widget.Toolbar
import com.example.pensievechecker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    // Firebase auth
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        val toolbar = binding.appBarLayout.findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        // Go from this activity to the sign up activity
        binding.createAcctBTN.setOnClickListener() {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            // this is solely for transition between the two acitivities
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.emailSignInButton.setOnClickListener() {
            LoginWithEmailPassword(binding.email.text.toString().trim(), binding.password.text.toString().trim())
        }

        // Auth Reference
        auth = Firebase.auth

    }

    private fun LoginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Sign in succeeded
                if(task.isSuccessful) {
                    val journal : JournalUser = JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.username = auth.currentUser?.displayName

                    goToJournalList()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        var intent = Intent(this, JournalList::class.java)
        startActivity(intent)
        // adding transition here
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}