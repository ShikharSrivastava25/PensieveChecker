package com.example.pensievechecker
import com.google.firebase.Timestamp
data class Journal(
    var title: String,
    var thoughts: String,
    var imageUrl: String,
    var userId: String,
    var timeAdded: Timestamp,
    var userName: String
)
