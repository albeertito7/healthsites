package albertperez.healthsites.fragments.addHealthSite

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DialogInfo(val title: String, val layout: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(layout, null)

        builder.setView(view)
            .setTitle(title)
            .setPositiveButton("ok") { _, _ -> }

        return builder.create()
    }
}