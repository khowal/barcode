package com.example.barcode.Receivers

import Commons.Companion.initBgProcess
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Check for pending uploads and schedule the tasks
            initBgProcess(context)
        }
    }
}
