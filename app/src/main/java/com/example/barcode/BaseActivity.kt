package com.example.barcode

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity

open class BaseActivity : ComponentActivity() {

    companion object {
        private var currentActivity: ComponentActivity? = null

        // Get the current activity context
        fun getCurrentActivity(): ComponentActivity? {
            return currentActivity
        }
    }

    override fun onResume() {
        super.onResume()
        currentActivity = this  // Set the current activity when it resumes
    }

    override fun onPause() {
        super.onPause()
        currentActivity = null  // Clear the reference when the activity is paused
    }
}
