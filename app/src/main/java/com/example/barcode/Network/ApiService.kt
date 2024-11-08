import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST(AppStrings.API_LOGIN)
    fun loginUser(@Field("email") email: String, @Field("password") password: String): Call<LoginModel>
}
