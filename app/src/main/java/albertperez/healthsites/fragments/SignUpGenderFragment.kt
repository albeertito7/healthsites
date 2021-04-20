package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.gender_fragment.*
import java.util.*

class SignUpGenderFragment : SignUpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.gender_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_group_gender.setOnCheckedChangeListener { _, checkedId ->
            btn_next_gender.alpha = 1.toFloat()
            btn_next_gender.isEnabled = true

            if (checkedId == R.id.radio_btn_custom) {
                signup_layout_custom.visibility = View.VISIBLE
                radio_btn_custom.requestFocus()
                showSoftKeyBoard(radio_btn_custom)
            } else {
                signup_layout_custom.visibility = View.GONE
                radio_btn_custom.clearFocus()
                (Objects.requireNonNull(requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE)) as InputMethodManager).hideSoftInputFromWindow(
                    requireView().windowToken,
                    0
                )
            }
        }

        btn_next_gender.setOnClickListener {
            when (radio_group_gender.checkedRadioButtonId) {
                R.id.radio_btn_male -> fragmentComm!!.setGender(getString(R.string.male))
                R.id.radio_btn_female -> fragmentComm!!.setGender(getString(R.string.female))
                R.id.radio_btn_custom -> fragmentComm!!.setGender(signup_input_custom.text.toString())
            }
            fragmentComm!!.showSummary()
        }
    }
}