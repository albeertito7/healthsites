package albertperez.healthsites

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : MainActivity() {

    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance();

        if(mAuth!!.currentUser != null) {
            mAuth?.signOut()
        }

        btn_login!!.setOnClickListener { attemptLogin() }

        btn_signup.background.alpha = 15
        btn_signup!!.setOnClickListener { view ->
            startActivity(Intent(this, SignUpActivity::class.java), ActivityOptions.makeCustomAnimation(view!!.context, R.anim.slide_up, R.anim.fade_out)!!.toBundle())
        }

        textView_forgotten!!.setOnClickListener { view ->
            startActivity(Intent(this, ForgottenPasswordActivity::class.java), ActivityOptions.makeCustomAnimation(view!!.context, R.anim.slide_up, R.anim.fade_out)!!.toBundle())
        }
    }

    private fun attemptLogin() {
        (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if(validLogInForm()) {
            showProgress()
            mAuth!!.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnSuccessListener {
                    startMapActivity()
                }
                .addOnFailureListener {
                    val toast = Toast.makeText(applicationContext, getString(R.string.authentication_failed), Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.TOP, 0, 40)
                    toast.show()
                    hideProgress()
                }
        }
    }

    private fun validLogInForm(): Boolean {

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = getString(R.string.error_invalid_email)
            email.requestFocus()
            return false
        }
        else if (TextUtils.isEmpty(password.text.toString()) && !isPasswordValid(password.text.toString())) {
            password.error = getString(R.string.error_invalid_password)
            password.requestFocus()
            return false
        }
        return true
    }

    private fun showProgress() {
        layout_logging_in!!.isEnabled = false;
        val obj1 = ObjectAnimator.ofFloat(progress_bar, View.ALPHA, 1f).setDuration(200)
        val obj2 = ObjectAnimator.ofFloat(layout_logging_in, View.ALPHA, 0.1f).setDuration(200)
        val obj3 = ObjectAnimator.ofFloat(layout_logging_in_bottom, View.ALPHA, 0.1f).setDuration(200)
        val collection = Arrays.asList(obj1, obj2, obj3)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(collection as Collection<Animator>?)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress_bar.visibility = View.VISIBLE
                textView_logging_in.visibility = View.VISIBLE
            }
        })
        animatorSet.start()
    }

    private fun hideProgress() {
        /*val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        textView_logging_in.visibility = View.GONE;
        progress_bar.animate().setDuration(shortAnimTime).alpha(0.toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress_bar.visibility = View.GONE
            }
        })
        ObjectAnimator.ofFloat(layout_logging_in, View.ALPHA, 0.1f, 1f).apply {
            duration = 200
            start()
        }*/

        val obj1 = ObjectAnimator.ofFloat(progress_bar, View.ALPHA, 0f).setDuration(200)
        val obj2 = ObjectAnimator.ofFloat(layout_logging_in, View.ALPHA, 1f).setDuration(200)
        val obj3 = ObjectAnimator.ofFloat(layout_logging_in_bottom, View.ALPHA, 1f).setDuration(200)
        val collection = Arrays.asList(obj1, obj2, obj3)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(collection as Collection<Animator>?)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                progress_bar.visibility = View.GONE
            }
        })
        textView_logging_in.visibility = View.GONE;
        animatorSet.start()
    }

    private fun startMapActivity() {
        startActivity(Intent(this, MapActivity::class.java))
        finish()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}