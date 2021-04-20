package albertperez.healthsites.fragments

import albertperez.healthsites.MultiTextWatcher
import albertperez.healthsites.MultiTextWatcher.TextWatcherWithInstance
import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.password_fragment.*

class SignUpPasswordFragment : SignUpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.password_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_input_password.requestFocus()
        showSoftKeyBoard(signup_input_password)

        MultiTextWatcher()
            .registerEditText(signup_input_password)
            .registerEditText(signup_input_password_confirm)
            .setCallback(object : TextWatcherWithInstance {
                override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) { }

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (signup_input_password.length() < 6 || signup_input_password.length() != signup_input_password_confirm.length() || signup_input_password.text.toString() != signup_input_password_confirm.text.toString()) {
                        btn_next_password.alpha = 0.3.toFloat()
                        btn_next_password.isEnabled = false
                    } else {
                        btn_next_password.alpha = 1f
                        btn_next_password.isEnabled = true
                    }
                }
            })

        btn_next_password.setOnClickListener {
            fragmentComm!!.setPassword(signup_input_password.text.toString())
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpDatePickerFragment(), "datepicker")
                .addToBackStack(null)
                .commit()
        }
    }
}