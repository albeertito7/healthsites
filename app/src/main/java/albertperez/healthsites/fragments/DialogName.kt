package albertperez.healthsites.fragments

import albertperez.healthsites.DialogProfile
import albertperez.healthsites.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText

class DialogName : DialogProfile() {

    private var name: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_name, null)

        name = view.findViewById(R.id.editText_dialog_name)

        builder.setView(view)
            .setTitle(getString(R.string.update_your_name))
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                hideSoftKeyBoard(context)
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                hideSoftKeyBoard(context)
                val newValue = name!!.text.toString()
                if (newValue != "") {
                    listener?.dialogNameListener(newValue)
                }
            }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()

        name!!.requestFocus()
        showSoftKeyBoard()
    }
}