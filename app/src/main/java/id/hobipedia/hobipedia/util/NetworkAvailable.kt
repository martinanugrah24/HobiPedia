package id.hobipedia.hobipedia.util

import android.content.Context
import android.net.ConnectivityManager

class NetworkAvailable {
    companion object {
        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean {
            try {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = cm.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected) {
                    return true
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return false
        }
    }
}