package com.example.pensievechecker
import android.app.Application

// Since this is a subclass of Application, it will run first in the program before any activity/fragment
// This will run in the background as long as app is alive
class JournalUser : Application() {
    var username : String? = null
    var userId : String? = null

    companion object {
        var instance : JournalUser? = null
            get() {
                if (field == null) {
                    // create a new instance from journal user
                    field = JournalUser()
                }
                return field;
            }
            private set
    }
}