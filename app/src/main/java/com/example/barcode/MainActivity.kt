package com.example.barcode

import AppStrings
import KeyboardUtils
import LocalStorage
import LoginController
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.barcode.Screens.ProfileActivity
import com.example.barcode.Screens.ProfileActivity3
import com.example.barcode.ui.theme.BarcodeTheme

class MainActivity : BaseActivity() {

    var loginController: LoginController = LoginController()
    var localStorage = LocalStorage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val loginButton: Button = findViewById(R.id.loginBtn_id)
        loginButton.setOnClickListener {
            login()
        }

        // this is only for case 3 only
        // if you are following case 3 scenario please uncomment below code or you can remove or ignore it
        var loginModel = localStorage.getLoginModel(this)

        // if loginmodel is null means user is not logged in
        loginModel?.let {
            val intent3 = Intent(this, ProfileActivity3::class.java)
            startActivity(intent3)
        }
        // case 3 code over ...
    }

    private fun login() {
        val resultText: TextView = findViewById(R.id.result_id)
        val emailText: EditText = findViewById(R.id.email_id)
        val passwordText: EditText = findViewById(R.id.password_id)

        // Create the data map
        val data = mapOf(
            AppStrings.PARAM_EMAIL to emailText.text.toString().trim(),
            AppStrings.PARAM_PASSWORD to passwordText.text.toString().trim()
        )

        // Call the fetchUser function
        KeyboardUtils.hideKeyboard(this, currentFocus)

        loginController.fetchUser(this, data,
            { loginModel ->
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                // case 1 :
                // here if you don't want send whole object and send only name , email etc
                // you can send them individially
                // code 1
//                val intent2 = Intent(this, ProfileActivity::class.java)
//                intent2.putExtra("name", loginModel.data.name)
//                intent2.putExtra("email", loginModel.data.email)
//                startActivity(intent2)

                // case 2 :
                // here you want to send whole respose object to another screen user this code
                // here to send whole object we have to make our login model Serializable
                // check file [Models/LoginModel.kt]
                // to run this code comment above code and uncomment below code
                // code 2
                // val intent = Intent(this, ProfileActivity2::class.java)
                // intent.putExtra("user", loginModel)
                // startActivity(intent)


                // case 3 :
                // in above 2 approch when user close the app and re open it user will see login screen again
                // this is happening because we are not storing information in device storage
                // to store data in device storage we have to save information sharedPreferences
                // sharedPreferences is android class like cookie and session in website
                // to run this code comment above code and uncomment below code
                // code 3

                // Note : along with this code you have uncomment one more code that is inside on create function
                // please do check as well for this case only

                localStorage.saveLoginModel(this, loginModel)
                val intent3 = Intent(this, ProfileActivity3::class.java)
                startActivity(intent3)


            },
            { errorMessage ->
                resultText.text = errorMessage
            }
        )


//        resultText.text = emailText.text.toString() +  passwordText.text.toString()
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BarcodeTheme {
        Greeting("Android")
    }
}