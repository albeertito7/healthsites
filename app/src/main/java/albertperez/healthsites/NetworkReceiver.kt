package albertperez.healthsites

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import 	android.net.NetworkCapabilities

class NetworkReceiver : BroadcastReceiver() {
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork

        if(activeNetwork != null) {
            val networkInfo: NetworkCapabilities =  connectivityManager.getNetworkCapabilities(activeNetwork)
            when {
                networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    //Toast.makeText(context, "Wifi connnected",Toast.LENGTH_SHORT).show()
                }
                networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    //Toast.makeText(context, "Celular internet connected",Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, R.string.connection_lost, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, R.string.connection_lost, Toast.LENGTH_SHORT).show()
        }
    }
}