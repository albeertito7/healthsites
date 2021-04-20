package albertperez.healthsites.fragments

import albertperez.healthsites.MultiTextWatcher
import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.name_surname_fragment.*

class SignUpNameSurnameFragment : SignUpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.name_surname_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_name.requestFocus()
        showSoftKeyBoard(signup_name)

        MultiTextWatcher()
            .registerEditText(signup_name)
            .registerEditText(signup_surname)
            .setCallback(object : MultiTextWatcher.TextWatcherWithInstance {
                override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) { }

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (!TextUtils.isEmpty(signup_name.text.toString()) && !TextUtils.isEmpty(signup_surname.text.toString())) {
                        btn_next_name_surname.alpha = 1f
                        btn_next_name_surname.isEnabled = true
                    } else {
                        btn_next_name_surname.alpha = 0.3.toFloat()
                        btn_next_name_surname.isEnabled = false
                    }
                }
            })

        btn_next_name_surname.setOnClickListener {
            fragmentComm!!.setName(signup_name.text.toString())
            fragmentComm!!.setSurname(signup_surname.text.toString())
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpUsernameFragment(), "username")
                .addToBackStack(null)
                .commit()
        }
    }
}