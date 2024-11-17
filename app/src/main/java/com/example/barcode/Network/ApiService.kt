import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST(AppStrings.API_LOGIN)
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginModel>

    @FormUrlEncoded
    @POST(AppStrings.API_BARCODE_INFO)
    fun barcodeInfo(@Field(AppStrings.PARAM_ROLL_NUMBER) email: String): Call<BarcodeInfoModel>

    @Multipart
    @POST(AppStrings.API_SAVE_IMAGE)
    fun uploadBarcodeInfo(
        @Part photo: MultipartBody.Part,
        @Part(AppStrings.PARAM_ROLL_NUMBER) rollnumber: RequestBody
    ): Call<ResponseBody>


}
