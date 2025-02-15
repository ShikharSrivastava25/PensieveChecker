package com.example.pensievechecker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.transition.Visibility
import com.example.pensievechecker.databinding.ActivityAddJournalBinding
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

// Activity to allow user to create a new post in firebase

class AddJournalActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddJournalBinding

    // Create the credentials
    var currentUserId: String = ""
    var currentUserName: String = ""

    // Firebase
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    // Firestore
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    lateinit var storageReference: StorageReference

    var collectionReference: CollectionReference = db.collection("Journal")
    lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)


        storageReference = FirebaseStorage.getInstance().getReference()
        auth = Firebase.auth

        binding.apply {
            postProgressBar.visibility = View.INVISIBLE

            if (JournalUser.instance != null) {
//                currentUserId = JournalUser.instance!!.userId.toString()
//                currentUserName = JournalUser.instance!!.username.toString()

                currentUserId = auth.currentUser?.uid.toString()
                currentUserName = auth.currentUser?.displayName.toString()

                postUsernameTextView.text = currentUserName
            }

            // Getting image from gallery
            binding.postCameraButton.setOnClickListener() {
                var i: Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*") // all types of image - jpg, png etc
                startActivityForResult(i, 1)
            }

            // save all the data into the storage
            postSaveJournalButton.setOnClickListener {
                SaveJournal()
            }
        }

    }

    // saving the post details in the firestore storage
    private fun SaveJournal() {
        val title: String = binding.postTitleEt.text.toString().trim()
        val thoughts: String = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null) {
            val filePath: StorageReference = storageReference.child("journal_images")
                .child("my_image_" + Timestamp.now().seconds)

            filePath.putFile(imageUri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUri: String = uri.toString()
                            val timestamp: Timestamp = Timestamp(Date())

                            val journal = Journal(
                                title,
                                thoughts,
                                imageUri,
                                currentUserId,
                                timestamp,
                                currentUserName
                            )

                            collectionReference.add(journal)
                                .addOnSuccessListener {
                                    Log.d(
                                        "SaveJournal",
                                        "Journal saved successfully, navigating to JournalList"
                                    )
                                    binding.postProgressBar.visibility = View.INVISIBLE

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val intent = Intent(this, JournalList::class.java)
                                        startActivity(intent)
                                        finish()
                                    }, 500)
                                }
                                .addOnFailureListener { e ->
                                    binding.postProgressBar.visibility = View.INVISIBLE
                                    Log.e("SaveJournal", "Failed to add journal: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            binding.postProgressBar.visibility = View.INVISIBLE
                            Log.e("SaveJournal", "Failed to get image URL: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    binding.postProgressBar.visibility = View.INVISIBLE
                    Log.e("SaveJournal", "Image upload failed: ${e.message}")
                }
        } else {
            binding.postProgressBar.visibility = View.INVISIBLE
            Log.e("SaveJournal", "Title, thoughts, or imageUri is empty/null")
        }
    }


    // callback function that is used to handle the result from another activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.data!! // getting the actual image
                binding.postImageView.setImageURI(imageUri) // showing the image
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if (auth != null) {
            auth.signOut()
        }
    }
}