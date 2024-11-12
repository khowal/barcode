import java.io.Serializable

data class LoginModel(
    val success: String,
    val code: String,
    val message: String,
    val appversion: String,
    val apiname: String,
    val error: String,
    val data: Data
) : Serializable

data class Data(
    val error: String,
    val token: String,
    val name: String,
    val email: String,
    val username: String
) : Serializable
