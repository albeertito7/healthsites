package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class MapOptionsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_map_options, rootKey)
    }
}