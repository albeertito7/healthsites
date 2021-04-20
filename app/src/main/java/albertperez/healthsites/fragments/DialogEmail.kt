package albertperez.healthsites.fragments

import albertperez.healthsites.DialogProfile
import albertperez.healthsites.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast

class DialogEmail : DialogProfile() {
    private var email: EditText? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_email, null)

        email = view.findViewById(R.id.editText_dialog_email)

        builder.setView(view)
            .setTitle(getString(R.string.update_your_email))
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                hideSoftKeyBoard(context)
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                hideSoftKeyBoard(context)
                val newValue = email!!.text.toString()
                if (Patterns.EMAIL_ADDRESS.matcher(newValue).matches()) {
                    listener?.dialogEmailListener(newValue)
                } else {
                    Toast.makeText(context, getString(R.string.email_invalid), Toast.LENGTH_LONG).show()
                }
            }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        email!!.requestFocus()
        showSoftKeyBoard()
    }
}