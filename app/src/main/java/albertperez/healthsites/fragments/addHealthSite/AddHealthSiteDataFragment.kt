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

class AddHealthSiteDataFragment : AddFragment() {
    private var name: EditText? = null
    private var description: EditText? = null
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_healthsite_data_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.add_input_name)
        description = view.findViewById(R.id.add_input_description)
        next = view.findViewById(R.id.btn_next_add_healhtsite_data)

        next!!.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(
                    R.id.container_add_fr,
                    AddHealthSitePropertiesDataFragment(), "add_Properties_data"
                )
                .addToBackStack(null)
                .commit()
        }

        MultiTextWatcher()
            .registerEditText(name!!)
            .registerEditText(description!!)
            .setCallback(object : TextWatcherWithInstance {
                override fun beforeTextChanged(
                    editText: EditText?,
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    editText: EditText?,
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(editText: EditText?, editable: Editable?) {
                    if (!TextUtils.isEmpty(name!!.text.toString()) && !TextUtils.isEmpty(description!!.text.toString())) {
                        listener!!.setName(name!!.text.toString())
                        listener?.setDescription(description!!.text.toString())
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