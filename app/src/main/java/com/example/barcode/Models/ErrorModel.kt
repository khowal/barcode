import com.google.gson.Gson

// Define the data classes based on the JSON structure
data class ErrorModel(
    val success: Boolean,
    val message: String,
    val errordata: Data
)


