package albertperez.healthsites

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgotten_password.*

class ForgottenPasswordActivity : MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)

        btn_back_forgotten!!.background.alpha = 15

        email_recover!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email_recover!!.text.toString()).matches()) {
                    btn_send_recover.alpha = 0.4.toFloat()
                    btn_send_recover.isEnabled = false
                    email_recover!!.requestFocus()
                } else {
                    btn_send_recover.alpha = 1f
                    btn_send_recover.isEnabled = true
                }
            }
        })
        btn_send_recover.setOnClickListener { sendRecoveryEmail() }
        btn_back_forgotten!!.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down)
        }
    }

    override fun onBackPressed() {
        btn_back_forgotten!!.callOnClick()
    }

    private fun sendRecoveryEmail() {
        hideSoftKeyBoard()
        showProgress()
        FirebaseAuth.getInstance().sendPasswordResetEmail(email_recover!!.text.toString())
            .addOnSuccessListener {
                val toast = Toast.makeText(applicationContext, getString(R.string.recovery_email_send_succesfully), Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 40)
                toast.show()
            }
            .addOnFailureListener {
                val toast = Toast.makeText(applicationContext, getString(R.string.recovery_email_send_failed), Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP, 0, 40)
                toast.show()
            }
            .addOnCompleteListener {
                hideProgress()
            }
    }

    private fun hideSoftKeyBoard() {
        (applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun showProgress() {
        val obj2 = ObjectAnimator.ofFloat(layout_forgotten_email, View.ALPHA, 0.1f).setDuration(200)

        obj2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                btn_send_recover.isEnabled = false
                btn_back_forgotten.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator) {
                progress_bar_forgotten.visibility = View.VISIBLE
                textView_sending_email.visibility = View.VISIBLE
            }
        })
        obj2.start()
    }

    private fun hideProgress() {
        val obj2 = ObjectAnimator.ofFloat(layout_forgotten_email, View.ALPHA, 1f).setDuration(200)

        obj2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                progress_bar_forgotten.visibility = View.GONE
                textView_sending_email.visibility = View.GONE
            }
            override fun onAnimationEnd(animation: Animator) {
                btn_send_recover.isEnabled = true
                btn_back_forgotten.isEnabled = true
            }
        })
        obj2.start()
    }
}