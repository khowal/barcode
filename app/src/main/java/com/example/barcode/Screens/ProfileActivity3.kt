package com.example.barcode.Screens

import AppStrings
import Commons.Companion.uploadData
import MyDatabaseHelper
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.barcode.BaseActivity
import com.example.barcode.MainActivity
import com.example.barcode.R
import com.example.barcode.Storage.LocalStorage

class ProfileActivity3 : BaseActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var txtRemainingUploads: TextView
    private lateinit var btnLogOut: Button
    private lateinit var btnScanBarcode: Button
    var localStorage = LocalStorage()

    var remainingUploads: String = ""

    //
    private lateinit var broadcastReceiver: BroadcastReceiver


    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

    }


    fun updateRemainingCount() {
        val dbHelper = MyDatabaseHelper(this)
        remainingUploads = dbHelper.fetchRemainingUploads(false).size.toString()
        txtRemainingUploads.text = remainingUploads
    }


    override fun onResume() {
        super.onResume()
        updateRemainingCount()
        val intentFilter = IntentFilter(AppStrings.BROADCAST_UPDATE_UI)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // here i am using same layout for this acitivty as well
        setContentView(R.layout.activity_profile)

        // for each and every id we have to use findviewbyid that is very old approach and time consumin as well
        // there is another mehod called binding
        // using binding we don't need to write a code findviewbyid for any view , instead we can use directly
        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        txtUserName = findViewById(R.id.txtUserName)
        txtRemainingUploads = findViewById(R.id.txtRemainingUploads)
        txtRemainingUploads.text = "0";
        btnLogOut = findViewById(R.id.btnLogOut)
        btnScanBarcode = findViewById(R.id.btnScanBarcode)

        var loginModel = localStorage.getLoginModel(this)


        loginModel?.let {
            txtName.text = it.data.name;
            txtEmail.text = it.data.email;
            txtUserName.text = it.data.username;
        }

        btnLogOut.setOnClickListener {
            // here we are clear data from SharedPreferences , local database and redirecting user to the main screen again
            val dbHelper = MyDatabaseHelper(this)
            dbHelper.clearTable()
            localStorage.removeFromSharedPreferences(this);
            val intent3 = Intent(this, MainActivity::class.java)
            startActivity(intent3)
        }

        btnScanBarcode.setOnClickListener {

            val intent = Intent(this, ScannerScreen::class.java)
            startActivity(intent)
        }

        //

        uploadData(this)

        // Initialize the BroadcastReceiver
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                updateRemainingCount()
            }
        }

    }


}