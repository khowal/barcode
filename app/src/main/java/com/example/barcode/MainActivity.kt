package com.example.barcode

import LoginController
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.barcode.ui.theme.BarcodeTheme

class MainActivity : BaseActivity() {

    var loginController: LoginController = LoginController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val loginButton: Button = findViewById(R.id.loginBtn_id)
        loginButton.setOnClickListener {
            login()
        }
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
                resultText.text = loginModel.data.name + "\n " + loginModel.data.email + "\n " + loginModel.data.username + "\n " + loginModel.data.token


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