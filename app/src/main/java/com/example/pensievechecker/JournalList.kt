package com.example.pensievechecker


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pensievechecker.databinding.ActivityJournalListBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import java.sql.Time

class JournalList : AppCompatActivity() {

    lateinit var binding: ActivityJournalListBinding

    // Firebase references
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var user : FirebaseUser
    var db = FirebaseFirestore.getInstance()
    lateinit var storageReference : StorageReference
    var collectionReference : CollectionReference = db.collection("Journal")

    lateinit var journalList : MutableList<Journal>
    lateinit var adapter: JournalRecyclerAdapter

    lateinit var noPostsTextView : TextView

    // inflate the layout but still not visible to user
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal_list)
        setSupportActionBar(findViewById(R.id.my_toolbar))


        // Firebase Auth
        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser!!

        // Recycler view

        // recycler view will adapt to its contents size
        binding.recyclerView.setHasFixedSize(true)
        // make the recycler list linearly display views(vertical by default aswell)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Posts Arraylist
        journalList = arrayListOf<Journal>()
    }

    // Inflating the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // adding functionality to the menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            // firebaseAuth != null is not really necessary since it gets initialized often times than not
            R.id.action_add -> if(user != null && firebaseAuth != null) {
                startActivity(Intent(this, AddJournalActivity::class.java))
                // simple transition
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            R.id.action_sign_out -> if(user != null && firebaseAuth != null) {
                firebaseAuth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
                // simple transition
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Getting all posts
    // onStart() is triggered when the Activity becomes visible to the user but still not functional

    // We have used adapter here, because if user comes back from some other activity, onStart is called and not onCreate therefore user shall
    // get access to data at all times
    override fun onStart() {
        // passing userId as param to tell the func to compare the userId of the post to the current user
        collectionReference.whereEqualTo("userId", user.uid).get().addOnSuccessListener {
            // 'it' is a query snapshot which is a list of documents
            if(!it.isEmpty) {

                // every document inside query snapshot will go and create a journal object from it
                for (document in it) {
                    var journal = Journal(
                        document.data.get("title").toString(),
                        document.data.get("thoughts").toString(),
                        document.data.get("imageUrl").toString(),
                        document.data.get("userId").toString(),
                        document.data.get("timeAdded") as Timestamp,
                        document.data.get("username").toString()
                    )

                    journalList.add(journal)
                }

                // Recycler view
                adapter = JournalRecyclerAdapter(this, journalList)
                binding.recyclerView.setAdapter(adapter)
                // refresh the entire list when the dataset changes.
                adapter.notifyDataSetChanged()
            } else {
                binding.listNoPosts.visibility = TextView.VISIBLE
            }
        } .addOnFailureListener {
            // Handle failure
            Toast.makeText(this, "Oops! Something went wrong!", Toast.LENGTH_SHORT).show()
        }
        super.onStart()
    }
}