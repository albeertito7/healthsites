package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.username_fragment.*

class SignUpUsernameFragment : SignUpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.username_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_input_nickname.requestFocus()
        showSoftKeyBoard(signup_input_nickname)

        signup_input_nickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable) {
                if (s.length > 6) {
                    btn_next_username.alpha = 1f
                    btn_next_username.isEnabled = true
                } else {
                    btn_next_username.alpha = 0.4.toFloat()
                    btn_next_username.isEnabled = false
                }
            }
        })

        btn_next_username.setOnClickListener {
            fragmentComm!!.setUsername(signup_input_nickname.text.toString())
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpEmailFragment(), "email")
                .addToBackStack(null)
                .commit()
        }
    }
}