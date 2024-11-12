import android.app.Activity
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.ProgressDialog
import com.example.barcode.BaseActivity
import com.google.gson.Gson


class LoginController {

    fun fetchUser(
        context: Activity,
        data: Map<String, String>,
        callbackSuccess: (LoginModel) -> Unit,
        callbackFailure: (String?) -> Unit
    ) {

        // Get email and password from the map, with error handling
        val email = data[AppStrings.PARAM_EMAIL]
        val password = data[AppStrings.PARAM_PASSWORD]

        if (email == null || password == null) {
            Log.e("LoginController", "Email or password missing in data map.")
            callbackFailure(AppStrings.SOMETHING_WENT_WRONG + "1")
            return
        }

        if (!Validator.isValidEmail(email)) {
            callbackFailure(AppStrings.INVALID_EMAIL)
            return
        }

        if (!Validator.isValidPassword(password)) {
            callbackFailure(AppStrings.INVALID_PASSWORD)
            return
        }

        val currentActivity = BaseActivity.getCurrentActivity()


        currentActivity?.let {
            if (NetworkUtils.isInternetAvailable(it)) {
                val progressDialog = ProgressDialog(currentActivity)
                progressDialog.setMessage(AppStrings.LOADING)
                progressDialog.setCancelable(false)
                progressDialog.show()

                val call = RetrofitClient.instance.loginUser(email, password)
                call.enqueue(object : Callback<LoginModel> {
                    override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                        progressDialog.dismiss()



                        if (response.code() == 200) {
                            response.body()?.let {
                                if (it.success == "success") {
                                    callbackSuccess(it)
                                } else {
                                    callbackFailure(it.message)
                                }
                            }
                        } else {
                            response.errorBody()?.string().let {
                                val gson = Gson()
                                val response = gson.fromJson(it, ErrorModel::class.java)
                                callbackFailure(response.message)
                            }

                        }
                    }

                    override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                        progressDialog.dismiss()
                        Log.e("API_ERROR", "Failed: ${t.message}")
                        callbackFailure(t.message)
                    }
                })
            }else{
                callbackFailure(AppStrings.NO_INTERNET)
            }
        }?:run {
            callbackFailure(AppStrings.SOMETHING_WENT_WRONG + "2")
        }

    }

}