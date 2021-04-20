package albertperez.healthsites

import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*

@Suppress("DEPRECATION")
open class MainActivity : AppCompatActivity() {
    private var networkReceiver: NetworkReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale()

        networkReceiver = NetworkReceiver()
        this.registerReceiver(
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    @Suppress("DEPRECATION")
    protected fun setLocale() {
        val lang = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_language", "")
        if (!lang!!.isEmpty()) {
            val locale = Locale(lang)
            val config = Configuration()
            val dm = baseContext.resources.displayMetrics
            Locale.setDefault(locale)
            config.locale = locale
            baseContext.resources.updateConfiguration(config, dm)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (networkReceiver != null) {
            unregisterReceiver(networkReceiver)
        }
    }
}