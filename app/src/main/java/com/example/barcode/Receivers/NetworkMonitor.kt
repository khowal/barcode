import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build

class NetworkMonitor(private val context: Context, private val onConnected: () -> Unit) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun startMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // For API 21 and above, use NetworkCallback
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    onConnected()
                }
            }
            connectivityManager.registerNetworkCallback(request, networkCallback!!)
        } else {
            // For API below 21, use legacy method
            if (isInternetAvailableLegacy(context)) {
                onConnected()
            }
        }
    }

    fun stopMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
        }
    }

    private fun isInternetAvailableLegacy(context: Context): Boolean {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}
