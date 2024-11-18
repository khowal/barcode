import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLConnection


class ApiController {

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
            callbackFailure(AppStrings.SOMETHING_WENT_WRONG)
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


        if (NetworkUtils.isInternetAvailable(context)) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(AppStrings.LOADING)
            progressDialog.setCancelable(false)
            progressDialog.show()

            val call = RetrofitClient.instance.loginUser(email, password)
            call.enqueue(object : Callback<LoginModel> {
                override fun onResponse(
                    call: Call<LoginModel>, response: Response<LoginModel>
                ) {
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
        } else {
            callbackFailure(AppStrings.NO_INTERNET)
        }


    }


    fun fetchBarcodeInfo(
        context: Activity,
        data: Map<String, String>,
        callbackSuccess: (BarcodeInfoModel) -> Unit,
        callbackFailure: (String?) -> Unit,
        noIntenet: () -> Unit,
    ) {

        if (NetworkUtils.isInternetAvailable(context)) {

            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(AppStrings.LOADING)
            progressDialog.setCancelable(false)
            progressDialog.show()

            var rollNumber: String? = data[AppStrings.PARAM_ROLL_NUMBER]


            if (rollNumber == null) {
                callbackFailure(AppStrings.SOMETHING_WENT_WRONG)
                return
            }


            val call = RetrofitClient.instance.barcodeInfo(rollNumber)

            call.enqueue(object : Callback<BarcodeInfoModel> {
                override fun onResponse(
                    call: Call<BarcodeInfoModel>, response: Response<BarcodeInfoModel>
                ) {
                    progressDialog.dismiss()

                    var code = response.code();
                    var body = response.body();
                    var message = response.message();
                    Log.e(">>>yaddd", "" + code)
                    Log.e(">>>yaddd", "" + body)
                    Log.e(">>>yaddd", "" + message)

                    if (code == 200) {
                        body?.let {
                            callbackSuccess(it)
                        }
                    } else {
                        callbackFailure(AppStrings.SOMETHING_WENT_WRONG)
                    }
                }

                override fun onFailure(call: Call<BarcodeInfoModel>, t: Throwable) {
                    progressDialog.dismiss()
                    callbackFailure(t.message)
                }
            })
        } else {
            noIntenet()
        }


    }

    fun getMimeType(file: File): String {
        // Try to get the MIME type based on file extension
        val mimeType = URLConnection.guessContentTypeFromName(file.name)
        return mimeType ?: "application/octet-stream"  // Fallback to a default MIME type
    }


    fun uploadBarcodeInfo(
        context: Context,
        data: Map<String, Any>,
        callbackSuccess: (String) -> Unit,
        callbackFailure: (String?) -> Unit,
        noIntent: () -> Unit,
        showDialog: Boolean,
    ) {

        if (NetworkUtils.isInternetAvailable(context)) {
            var progressDialog: ProgressDialog? = null
            if (showDialog == true) {
                progressDialog = ProgressDialog(context)
                progressDialog.setMessage(AppStrings.LOADING)
                progressDialog.setCancelable(false)
                progressDialog.show()
            }

            //
            var rollNumber: String = data[AppStrings.PARAM_ROLL_NUMBER] as String
            var name: String = data[AppStrings.PARAM_NAME] as String
            val rollNumberPart = RequestBody.create("text/plain".toMediaType(), rollNumber)


            var photFile: File = data[AppStrings.PARAM_PHOTO] as File
            val mimeType = getMimeType(photFile)
            val requestFile = RequestBody.create((mimeType.toMediaType()), photFile)
            val photoMultipart = MultipartBody.Part.createFormData(
                AppStrings.PARAM_PHOTO, photFile.name, requestFile
            )


            val call = RetrofitClient.instance.uploadBarcodeInfo(photoMultipart, rollNumberPart)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>, response: Response<ResponseBody>
                ) {
                    if (showDialog) {
                        progressDialog?.dismiss()
                    }


                    var code = response.code();
                    var url = call.request().url;
                    var body = response.body();
                    var message = response.message();
                    var err = response.errorBody()?.string();
                    Log.e(">>>yaddd111", "" + code)
                    Log.e(">>>yaddd111", "" + body)
                    Log.e(">>>yaddd111", "" + message)
                    Log.e(">>>yaddd111", "" + err)
                    Log.e(">>>yaddd111", "" + url)


                    if (code == 200) {
                        body?.let {
                            callbackSuccess(it.string())
                            context?.let {
                                val dbHelper = MyDatabaseHelper(it)
                                dbHelper.insertData(name, photFile.path, rollNumber, 1)
                            }


                        }
                    } else {
                        callbackFailure(AppStrings.SOMETHING_WENT_WRONG)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    if (showDialog) {
                        progressDialog?.dismiss()
                    }
                    callbackFailure(t.message)
                }
            })
        } else {
            noIntent()
        }


    }

}