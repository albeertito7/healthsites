package albertperez.healthsites

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity : MainActivity() {
    var mPrefs: SharedPreferences? = null

    protected var mAuth: FirebaseAuth? = null;
    //protected var mDb: FirebaseDatabase? = null;
    protected var mFs: FirebaseFirestore? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isPlayServicesAvailable()
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        mAuth = FirebaseAuth.getInstance()
        mFs = FirebaseFirestore.getInstance()
    }

    private fun isPlayServicesAvailable() {
        val resultCode = GoogleApiAvailability . getInstance ().isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 1).show()
        }
    }

    public override fun onResume() {
        super.onResume()
        isPlayServicesAvailable()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.settings -> {
                startActivity(
                    Intent(this,
                        SettingsActivity::class.java),
                    ActivityOptions.makeCustomAnimation(
                        applicationContext,
                        R.anim.slide_in_from_right,
                        R.anim.slide_out_to_left)
                    !!.toBundle())
            }
            R.id.map_options -> {
                startActivity(Intent(this, MapOptionsActivity::class.java), ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_in_from_right, R.anim.slide_out_to_left)!!.toBundle())
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

     fun setMapView() {
        val s: String? = mPrefs?.getString("pref_map_view", "normal")
        val editor: SharedPreferences.Editor = mPrefs!!.edit()
        /*val i: Int =  when (s) {
            "satellite" -> GoogleMap.MAP_TYPE_SATELLITE
            "hybrid" -> GoogleMap.MAP_TYPE_HYBRID
            "terrain" -> GoogleMap.MAP_TYPE_TERRAIN
            else -> GoogleMap.MAP_TYPE_NORMAL
        }*/
        editor.putString("pref_map_view", s)
        editor.apply()
        //MapActivity.setMapView(i)
    }

    fun setMapStyle() {
        val s: String? = mPrefs?.getString("pref_map_style", "default")
        /*val i: Int =  when (s) {
            "dark" -> R.raw.style_map_dark
            else -> R.raw.style_map_default
        }*/
        val editor: SharedPreferences.Editor = mPrefs!!.edit()
        editor.putString("pref_map_style", s)
        editor.apply()
        //MapActivity.setMapStyle(applicationContext, i)
    }

    companion object {
    }
}