package albertperez.healthsites.fragments

import albertperez.healthsites.*
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : PreferenceFragmentCompat() {

    private val urlWeb: String = "https://healthsites.herokuapp.com/";

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val username = findPreference<EditTextPreference>("change_username")!!
        username.text = ""
        username.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                FirebaseFirestore.getInstance().collection("Users")
                    .whereEqualTo("username", newValue).get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null && task.result!!.documents.size == 0) {
                            FirebaseFirestore.getInstance().collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                .update("username", newValue.toString())
                        } else {
                            Toast.makeText(context, R.string.username_already_taken, Toast.LENGTH_SHORT).show()
                        }
                    }
                true
            }

        val password = findPreference<EditTextPreference>("change_password")!!
        password.text = ""
        password.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                FirebaseAuth.getInstance().currentUser!!.updatePassword((newValue as String))
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("password", newValue.toString())
                true
            }

        val email = findPreference<EditTextPreference>("change_email")!!
        email.text = ""
        email.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                FirebaseAuth.getInstance().currentUser!!.updateEmail((newValue as String))
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update("email", newValue.toString())
                true
            }

        findPreference<Preference>("pref_log_out")!!.setOnPreferenceClickListener {
                val auth = FirebaseAuth.getInstance()
                if (auth.currentUser != null) {
                    auth.signOut()
                    finishAffinity(requireActivity())
                    val intent = Intent(requireActivity().applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent, ActivityOptions.makeCustomAnimation(requireActivity().applicationContext, R.anim.slide_up, R.anim.fade_out).toBundle())
                }
                false
            }

        findPreference<Preference>("pref_language")!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ -> true }

        findPreference<Preference>("pref_contact")!!.setOnPreferenceClickListener {
            val intent = Intent(requireActivity().applicationContext, SupportContactUs::class.java)
            startActivity(intent, ActivityOptions.makeCustomAnimation(requireActivity().applicationContext, R.anim.slide_in_from_right, R.anim.fade_out).toBundle())
            false
        }

        /*findPreference<Preference>("pref_healthSites")!!.setOnPreferenceClickListener {
            val intent = Intent(requireActivity().applicationContext, AboutHealthSites::class.java)
            startActivity(intent, ActivityOptions.makeCustomAnimation(requireActivity().applicationContext, R.anim.slide_in_from_right, R.anim.fade_out).toBundle())
            false
        }*/

        findPreference<Preference>("pref_website")!!.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this.urlWeb))
            startActivity(browserIntent)
            false
        }

        findPreference<Preference>("pref_privacy_policy")!!.setOnPreferenceClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(browserIntent)
            false
        }
    }
}