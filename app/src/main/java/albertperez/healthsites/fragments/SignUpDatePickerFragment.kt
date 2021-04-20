package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlinx.android.synthetic.main.datepicker_fragment.*

class SignUpDatePickerFragment : SignUpFragment() {

    private var date: Long? = null
    private val c = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.datepicker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signup_datepicker.maxDate = c.timeInMillis
        signup_datepicker.init(c[Calendar.YEAR], c[Calendar.MONTH], c[Calendar.DAY_OF_MONTH]) { _, year, monthOfYear, dayOfMonth ->
            btn_next_datepicker.alpha = 1f
            btn_next_datepicker.isEnabled = true
            @Suppress("DEPRECATION")
            date = Date(year - 1900, monthOfYear, dayOfMonth + 1).time
        }

        btn_next_datepicker.setOnClickListener {
            fragmentComm!!.setBirth(date)
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpGenderFragment(), "gender")
                .addToBackStack(null)
                .commit()
        }
    }
}