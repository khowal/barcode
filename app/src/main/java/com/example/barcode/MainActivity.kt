package com.example.barcode

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.barcode.ui.theme.BarcodeTheme

class MainActivity : ComponentActivity() {
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
        val resultText : TextView = findViewById(R.id.result_id)
        val emailText : EditText = findViewById(R.id.email_id)
        val passwordText : EditText = findViewById(R.id.password_id)
        resultText.text = emailText.text.toString() +  passwordText.text.toString()
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