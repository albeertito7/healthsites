package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.email_fragment.*

class SignUpEmailFragment : SignUpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.email_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_input_email.requestFocus()
        showSoftKeyBoard(signup_input_email)

        signup_input_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable) {
                if (Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches() /*&& checkEmail(s.toString())*/) {
                    btn_next_email.alpha = 1.toFloat()
                    btn_next_email.isEnabled = true
                } else {
                    btn_next_email.alpha = 0.4.toFloat()
                    btn_next_email.isEnabled = false
                }
            }
        })

        btn_next_email.setOnClickListener {
            fragmentComm!!.setEmail(signup_input_email.text.toString())
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpPasswordFragment(), "password")
                .addToBackStack(null)
                .commit()
        }
    }

    /*private fun checkEmail(email: String): Boolean {
        var value = false
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if(task.result!!.signInMethods!!.isEmpty()) {
                    value = true
                } else {
                    Toast.makeText(context, "Email already exists", Toast.LENGTH_SHORT)?.show()
                }
            }
        return value
    }*/
}