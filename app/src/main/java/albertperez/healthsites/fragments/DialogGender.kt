package albertperez.healthsites.fragments

import albertperez.healthsites.DialogProfile
import albertperez.healthsites.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.textfield.TextInputLayout

class DialogGender : DialogProfile() {

    private var radioGroup: RadioGroup? = null
    private var custom: RadioButton? = null
    private var customText: EditText? = null
    private var customTextLayout: TextInputLayout? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.custom_dialog_gender, null)

        radioGroup = view?.findViewById<View>(R.id.radio_group_gender_profile) as RadioGroup
        customText = view.findViewById<View>(R.id.signup_input_custom_profile) as EditText
        custom = view.findViewById<View>(R.id.radio_btn_custom_profile) as RadioButton
        customTextLayout = view.findViewById<View>(R.id.signup_layout_custom_profile) as TextInputLayout

        builder.setView(view)
            .setTitle(getString(R.string.update_your_male))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                when (radioGroup!!.checkedRadioButtonId) {
                    R.id.radio_btn_male_profile -> listener!!.dialogGenderListener(getString(R.string.male))
                    R.id.radio_btn_female_profile -> listener!!.dialogGenderListener(getString(R.string.female))
                    R.id.radio_btn_custom_profile -> listener!!.dialogGenderListener(customText!!.text.toString())
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        radioGroup!!.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radio_btn_custom_profile) {
                customTextLayout!!.visibility = View.VISIBLE
            } else {
                customTextLayout!!.visibility = View.GONE
            }
        }
    }
}