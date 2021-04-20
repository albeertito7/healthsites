package albertperez.healthsites

import albertperez.healthsites.fragments.MapOptionsFragment
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class MapOptionsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        custom_toolbar_top.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(custom_toolbar_top)
        supportActionBar!!.title = getString(R.string.map_options)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        custom_toolbar_top.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
        }

        if (fragment_container != null) {
            if (!(savedInstanceState != null))
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, MapOptionsFragment()).commit()
        }

        listener = OnSharedPreferenceChangeListener { _, key ->
            if (key == "pref_map_view") {
                setMapView()
            } else if (key == "pref_map_style") {
                setMapStyle()
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

    companion object {
        private var listener: OnSharedPreferenceChangeListener? = null
    }
}