package com.example.barcode
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {

    companion object {
        private var currentActivity: ComponentActivity? = null

        // Get the current activity context
        fun getCurrentActivity(): ComponentActivity? {
            return currentActivity
        }
    }

    override fun onStart() {
        super.onStart()
        currentActivity = this  // Set the current activity when it starts
    }

    override fun onStop() {
        super.onStop()
        currentActivity = null  // Clear the reference when the activity stops
    }
}


