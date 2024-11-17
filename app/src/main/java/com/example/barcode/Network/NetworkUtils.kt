import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.activity.ComponentActivity

class NetworkUtils {

    companion object {
        /**
         * Checks if the device is connected to the internet.
         * @param context The application or activity context.
         * @return true if the device is connected to the internet, false otherwise.
         */
        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // For devices running Android 10 (API 29) and above, use NetworkCapabilities
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

                networkCapabilities?.let {
                    return it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                }
            } else {
                // For older devices (pre-Android 6), we use the deprecated method
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo?.isConnected == true
            }

            return false
        }
    }
}
