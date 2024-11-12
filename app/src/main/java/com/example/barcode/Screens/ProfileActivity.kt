package com.example.barcode.Screens

import LoginModel
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import com.example.barcode.BaseActivity
import com.example.barcode.R

class ProfileActivity : BaseActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var btnLogOut: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // for each and every id we have to use findviewbyid that is very old approach and time consumin as well
        // there is another mehod called binding
        // using binding we don't need to write a code findviewbyid for any view , instead we can use directly
        txtName = findViewById(R.id.txtName)
        txtEmail = findViewById(R.id.txtEmail)
        txtUserName = findViewById(R.id.txtUserName)
        btnLogOut = findViewById(R.id.btnLogOut)

        intent.getStringExtra("name")?.let {
            txtName.text = it
        }
        intent.getStringExtra("email")?.let {
            txtEmail.text = it
        }

        // to show user name add new key in main login after successfull login and try it by yourself

        btnLogOut.setOnClickListener {
            // go to previous screen
            finish()
        }

    }
}