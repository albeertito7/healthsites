package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*

class HealthSiteOptionsFragment : PreferenceFragmentCompat() {

    private var id: String? = null
    private var name: String? = null
    private var slogan: String? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_healthsite_options, rootKey)

        val bundle = arguments
        id = bundle!!.getString("id")
        name = bundle.getString("name")
        slogan = bundle.getString("slogan")

        /*val details = findPreference<Preference>("pref_deatils")!!
        details.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, HeatlthSiteDetailsActivity::class.java))
            true
        }*/

        val share = findPreference<Preference>("pref_share")!!
        share.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            val subject = "HealthSite"
            i.putExtra(Intent.EXTRA_SUBJECT, name!!)
            i.putExtra(Intent.EXTRA_TITLE, subject)
            i.putExtra(Intent.EXTRA_TEXT, slogan!!)
            startActivity(Intent.createChooser(i, getString(R.string.choose_to_continue)))
            true
        }

        val report = findPreference<Preference>("pref_report")!!
        report.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val map: MutableMap<String, Any> = HashMap()
            map[FirebaseAuth.getInstance().currentUser!!.uid] = true
            FirebaseFirestore.getInstance().collection("HealthSiteReport").document(id!!)[map] = SetOptions.merge()
            Toast.makeText(context, R.string.thank_you_for_your_feedback, Toast.LENGTH_SHORT).show()
            true
        }
    }
}