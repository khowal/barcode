package network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

interface AuthService {
    @POST("login")  // Replace with the correct endpoint
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}



