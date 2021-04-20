package albertperez.healthsites

import albertperez.healthsites.fragments.SignUpFragment.FragmentComm
import albertperez.healthsites.fragments.SignUpGetStartedFragment
import albertperez.healthsites.fragments.SignUpFinishFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.summary_fragment.*
import java.lang.Exception

class SignUpActivity : MainActivity(), FragmentComm {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_already_account!!.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down)
        }

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.slide_in_from_right,
                R.anim.fade_out)
            .replace(R.id.container_fr,
                SignUpGetStartedFragment(),
                "get_started")
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("get_started")!!.isVisible) {
            btn_already_account!!.callOnClick()
        } else {
            super.onBackPressed()
            btn_already_account.background.alpha = 15
        }
    }

    override fun onResume() {
        super.onResume()
        btn_already_account.background.alpha = 15
    }

    override fun setName(name: String?) {
        user?.name = name
    }

    override fun setSurname(surname: String?) {
        user?.surname = surname
    }

    override fun setUsername(username: String?) {
        user?.username = username
    }

    override fun setEmail(email: String?) {
        user?.email = email
    }

    override fun setPassword(password: String?) {
        user?.password = password
    }

    override fun setBirth(birth: Long?) {
        user?.dateOfBirth = birth
    }

    override fun setGender(gender: String?) {
        user?.gender = gender
    }

    @SuppressLint("ResourceType")
    override fun showSummary() {
        val fr = SignUpFinishFragment()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
            .replace(R.id.container_fr, fr, "summary")
            .addToBackStack(null)
            .commit()
    }

    override fun signUp() {
        showProgress()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user!!.email, user!!.password)
            .addOnSuccessListener {
                user?.setId(FirebaseAuth.getInstance().currentUser!!.uid)
                val map: Map<String, Any> = jacksonObjectMapper().convertValue(user!!)
                FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid).set(map)
                    .addOnSuccessListener {
                        Log.i("Set User", "User Registration OK")
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_register_ok),
                            Toast.LENGTH_SHORT).show()
                        startMapActivity()
                    }.addOnFailureListener {
                        Log.e("Set User", "User Registration KO")
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_register_ko),
                            Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { exception: Exception ->
                Log.e("Fail signup", "createUserWithEmail:failure", exception)
                hideProgress()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.user_register_ko),
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun startMapActivity() {
        val intent = Intent(this, MapActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.slide_down).toBundle())
    }

    private fun showProgress() {
        val obj = ObjectAnimator.ofFloat(layout_signing_up, View.ALPHA, 0.1f).setDuration(200)
        obj.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                btn_summary_signup.isEnabled = false
                btn_already_account.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator) {
                progress_bar_signing_up.visibility = View.VISIBLE
                textView_signing_up.visibility = View.VISIBLE
            }
        })
        obj.start()
    }

    private fun hideProgress() {
        val obj = ObjectAnimator.ofFloat(layout_signing_up, View.ALPHA, 1f).setDuration(200)
        obj.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                progress_bar_signing_up.visibility = View.GONE
                textView_signing_up.visibility = View.GONE
            }
            override fun onAnimationEnd(animation: Animator) {
                btn_summary_signup.isEnabled = true
                btn_already_account.isEnabled = true
            }
        })
        obj.start()
    }

    companion object {
        private var user: User? = User()
    }
}