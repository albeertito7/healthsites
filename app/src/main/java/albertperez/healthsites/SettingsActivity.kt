package albertperez.healthsites

import albertperez.healthsites.fragments.SettingsFragment
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        custom_toolbar_top.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(custom_toolbar_top)
        supportActionBar!!.title = resources.getString(R.string.settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        custom_toolbar_top.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
        }

        if (fragment_container != null && savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).commit()
        }

        listener = OnSharedPreferenceChangeListener { prefs, key ->
            if(key == "pref_language") {
                prefs.getString(key, "en")?.let { setLocale(it) }
            }
        }

        mPrefs!!.registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onBackPressed() {
        custom_toolbar_top.callOnClick()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mPrefs!!.unregisterOnSharedPreferenceChangeListener(listener)
    }

    @Suppress("DEPRECATION")

    fun setLocale(lang: String) {
        val locale = Locale(lang)
        val config = Configuration()
        val dm: DisplayMetrics = baseContext.resources.displayMetrics

        Locale.setDefault(locale)
        config.locale = locale
        baseContext.resources.updateConfiguration(config, dm)

        recreate()
    }

    companion object {
        private var listener: OnSharedPreferenceChangeListener? = null
    }
}