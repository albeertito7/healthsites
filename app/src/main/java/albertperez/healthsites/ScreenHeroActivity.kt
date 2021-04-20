package albertperez.healthsites

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_screen_hero.*

class ScreenHeroActivity : MainActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_hero)

        layout.setOnClickListener { view ->
            val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
            if(mAuth.currentUser != null) {
                if(PreferenceManager.getDefaultSharedPreferences(this)?.getBoolean("prefs_session", true) != null) {
                    startMapActivity(view)
                } else {
                    mAuth.signOut()
                    startLoginActivity(view)
                }
            } else {
                startLoginActivity(view)
            }
            finish()
        }

        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        anim.startOffset = 10
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE

        textView_tap_here.startAnimation(anim)
    }

    private fun startLoginActivity(view: View) {
        startActivity(Intent(this, LoginActivity::class.java), ActivityOptions.makeCustomAnimation(
            view.context, R.anim.fade_in, R.anim.fade_out)!!.toBundle())
    }

    private fun startMapActivity(view: View) {
        startActivity(Intent(this, MapActivity::class.java), ActivityOptions.makeCustomAnimation(
            view.context, R.anim.fade_in, R.anim.fade_out)!!.toBundle())
    }
}