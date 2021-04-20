package albertperez.healthsites.fragments.addHealthSite

import albertperez.healthsites.MultiTextWatcher
import albertperez.healthsites.MultiTextWatcher.TextWatcherWithInstance
import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class AddCompanyDataFragment : AddFragment() {
    private var companyName: EditText? = null
    private var companyCIF: EditText? = null
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_company_data_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        companyName = view.findViewById(R.id.add_input_company_name)
        companyCIF = view.findViewById(R.id.add_input_cif)
        next = view.findViewById(R.id.btn_next_add_company_data)

        next!!.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
                .replace(R.id.container_add_fr, AddCompanyContactFragment(), "add_company_contact")
                .addToBackStack(null)
                .commit()
        }

        MultiTextWatcher()
            .registerEditText(companyName!!)
            .registerEditText(companyCIF!!)
            .setCallback(object : TextWatcherWithInstance {
                override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (!TextUtils.isEmpty(companyName!!.text.toString()) && !TextUtils.isEmpty(companyCIF!!.text.toString())) {
                        listener!!.setCompanyName(companyName!!.text.toString())
                        listener?.setCompanyCIF(companyCIF!!.text.toString())
                        next!!.alpha = 1f
                        next!!.isEnabled = true
                    } else {
                        next!!.alpha = 0.3.toFloat()
                        next!!.isEnabled = false
                    }
                }
            })
    }
}