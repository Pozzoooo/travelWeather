package pozzo.apps.travelweather.common

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi

class NetworkHelper {

    fun isConnected(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isConnectedM(context)
        } else {
            isConnectedPreM(context)
        }
    }

    @Deprecated("For pre Android M usage only")
    private fun isConnectedPreM(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo?.isConnected == true
    }

    @RequiresApi(Build.VERSION_CODES.M) private fun isConnectedM(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetwork != null
    }
}
