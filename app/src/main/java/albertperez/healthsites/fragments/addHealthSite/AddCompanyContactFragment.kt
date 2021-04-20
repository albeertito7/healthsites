package albertperez.healthsites.fragments.addHealthSite

import albertperez.healthsites.MultiTextWatcher
import albertperez.healthsites.MultiTextWatcher.TextWatcherWithInstance
import albertperez.healthsites.R
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class AddCompanyContactFragment : AddFragment() {
    private var webSite: EditText? = null
    private var webMail: EditText? = null
    private var phoneNumber: EditText? = null
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_healthsite_company_contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webSite = view.findViewById(R.id.add_input_website)
        webMail = view.findViewById(R.id.add_input_webmail)
        phoneNumber = view.findViewById(R.id.add_input_phone)
        next = view.findViewById(R.id.btn_next_add_company_contact)

        MultiTextWatcher()
            .registerEditText(webSite!!)
            .registerEditText(webMail!!)
            .registerEditText(phoneNumber!!)
            .setCallback(object : TextWatcherWithInstance {
                override fun beforeTextChanged(editText: EditText?, s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(editText: EditText?, s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (!TextUtils.isEmpty(webSite!!.text.toString()) &&
                            !TextUtils.isEmpty(webMail!!.text.toString()) &&
                        Patterns.EMAIL_ADDRESS.matcher(webMail?.text.toString()).matches() &&
                        Patterns.WEB_URL.matcher(webSite?.text.toString()).matches() &&
                        Patterns.PHONE.matcher(phoneNumber!!.text.toString()).matches() &&
                        !TextUtils.isEmpty(phoneNumber?.text.toString())) {

                        listener!!.setWebSite(webSite?.text.toString())
                        listener?.setWebMail(webMail?.text.toString())
                        listener?.setPhoneNumber(phoneNumber?.text.toString())
                        next!!.alpha = 1f
                        next?.isEnabled = true
                    } else {
                        next!!.alpha = 0.3.toFloat()
                        next?.isEnabled = false
                    }
                }
            })

        next!!.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
                .replace(R.id.container_add_fr, AddRegisterFragment(), "add_register")
                .addToBackStack(null)
                .commit()
        }
    }
}