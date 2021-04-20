package albertperez.healthsites

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_support_contact_us.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class SupportContactUs : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_contact_us)

        custom_toolbar_top.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(custom_toolbar_top)
        supportActionBar!!.setTitle(resources.getString(R.string.contact_us))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        MultiTextWatcher()
            .registerEditText(support_message)
            .setCallback(object : MultiTextWatcher.TextWatcherWithInstance {
                override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (!TextUtils.isEmpty(support_message!!.text.toString())) {
                        btn_send_support!!.alpha = 1f
                        btn_send_support!!.isEnabled = true
                    } else {
                        btn_send_support!!.alpha = 0.3.toFloat()
                        btn_send_support!!.isEnabled = false
                    }
                }
            })

        btn_send_support.setOnClickListener {
            hideSoftKeyBoard()
            showProgress()
            val message = SupportMessage(FirebaseAuth.getInstance().currentUser!!.uid, support_message.text.toString(), (System.currentTimeMillis()/1000).toString());
            val id = FirebaseFirestore.getInstance().collection("Feedback").document().id
            FirebaseFirestore.getInstance().collection("Feedback").document(id).set(message).addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(applicationContext, "Thank you so much, we appreciate that", Toast.LENGTH_SHORT).show()
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right)
                } else {
                    Toast.makeText(applicationContext, "Sorry there was a problem, try again later", Toast.LENGTH_SHORT).show()
                }
                hideProgress()
            }
        }

        custom_toolbar_top.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

    private fun hideSoftKeyBoard() {
        (applicationContext.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onBackPressed() {
        custom_toolbar_top.callOnClick()
    }

    private fun showProgress() {
        val obj2 = ObjectAnimator.ofFloat(layout_forgotten_email, View.ALPHA, 0.1f).setDuration(200)

        obj2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                btn_send_support.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator) {
                //progress_bar_forgotten.visibility = View.VISIBLE
                textView_sending_email.visibility = View.VISIBLE
            }
        })
        obj2.start()
    }

    private fun hideProgress() {
        val obj2 = ObjectAnimator.ofFloat(layout_forgotten_email, View.ALPHA, 1f).setDuration(200)

        obj2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                //progress_bar_forgotten.visibility = View.GONE
                textView_sending_email.visibility = View.GONE
            }
            override fun onAnimationEnd(animation: Animator) {
                btn_send_support.isEnabled = true
            }
        })
        obj2.start()
    }
}