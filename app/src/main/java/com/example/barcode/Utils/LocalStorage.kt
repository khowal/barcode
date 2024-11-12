import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class LocalStorage {


    var prefLoginModel = "loginModel";
    var prefMain = "MyPrefs";

    fun saveLoginModel(context: Context, loginModel: LoginModel) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefMain, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convert the LoginModel object to JSON string using Gson
        val json = Gson().toJson(loginModel)

        // Store the JSON string in SharedPreferences
        editor.putString(prefLoginModel, json)
        editor.apply()
    }

    fun getLoginModel(context: Context): LoginModel? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefMain, Context.MODE_PRIVATE)

        // Retrieve the JSON string from SharedPreferences
        val json = sharedPreferences.getString(prefLoginModel, null)

        // If the JSON string is not null, deserialize it into LoginModel object
        return if (json != null) {
            Gson().fromJson(json, LoginModel::class.java)
        } else {
            null
        }
    }

    fun removeFromSharedPreferences(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(prefMain, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove specific key-value pair
        editor.remove(prefLoginModel)

        // Apply the changes
        editor.apply()
    }


}