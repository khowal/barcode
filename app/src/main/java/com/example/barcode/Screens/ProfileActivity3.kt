package com.example.barcode.Screens

import LocalStorage
import LoginModel
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.barcode.BaseActivity
import com.example.barcode.MainActivity
import com.example.barcode.R

class ProfileActivity3 : BaseActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var btnLogOut: Button
    var localStorage = LocalStorage()


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
        btnLogOut = findViewById(R.id.btnLogOut)
        var loginModel = localStorage.getLoginModel(this)

        loginModel?.let {
            txtName.text = it.data.name;
            txtEmail.text = it.data.email;
            txtUserName.text = it.data.username;
        }

        btnLogOut.setOnClickListener {

            // here we are clear data from SharedPreferences and redirecting user to the main screen again
            localStorage.removeFromSharedPreferences(this);
            val intent3 = Intent(this, MainActivity::class.java)
            startActivity(intent3)
        }


    }


}