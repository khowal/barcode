package com.example.barcode.Screens

import LoginModel
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.barcode.BaseActivity
import com.example.barcode.R

class ProfileActivity2 : BaseActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtUserName: TextView
    private lateinit var btnLogOut: Button


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

        val loginModel =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("user", LoginModel::class.java)
            } else {
                intent.getSerializableExtra("user") as? LoginModel
            }

        loginModel?.let {
            txtName.text = it.data.name;
            txtEmail.text = it.data.email;
            txtUserName.text = it.data.username;
        }


        btnLogOut.setOnClickListener {
            // go to previous screen
            finish()
        }


    }


}