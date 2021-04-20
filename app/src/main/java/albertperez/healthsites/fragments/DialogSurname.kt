package albertperez.healthsites.fragments

import albertperez.healthsites.DialogProfile
import albertperez.healthsites.R
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText

class DialogSurname : DialogProfile() {

    private var surname: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_surname, null)

        surname = view.findViewById(R.id.editText_dialog_surname)

        builder.setView(view)
            .setTitle("Update your surname")
            .setNegativeButton("cancel") { _, _ ->
                hideSoftKeyBoard(context)
            }
            .setPositiveButton("ok") { _, _ ->
                hideSoftKeyBoard(context)
                val newValue = surname!!.text.toString()
                if (newValue != "") {
                    listener?.dialogSurnameListener(newValue)
                }
            }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        surname!!.requestFocus()
        showSoftKeyBoard()
    }
}