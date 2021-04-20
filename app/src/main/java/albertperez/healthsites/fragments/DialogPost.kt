package albertperez.healthsites.fragments

import albertperez.healthsites.Post
import albertperez.healthsites.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DialogPost : DialogFragment() {

    private var healthSiteId: String? = null
    private var editText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_post, null)

        healthSiteId = arguments?.get("id").toString()
        editText = view.findViewById(R.id.editText_dialog_post)


        builder.setView(view)
            .setTitle(getString(R.string.how_was_your_experience))
            .setNegativeButton(getString(R.string.cancel)) { _,_ ->
                hideSoftKeyBoard(context)
            }
            .setPositiveButton(getString(R.string.post)) { _, _ ->
                hideSoftKeyBoard(context)
                val id = FirebaseFirestore.getInstance().collection("Post").document().id
                val post = Post(healthSiteId, FirebaseAuth.getInstance().currentUser!!.uid, editText!!.text.toString(), (System.currentTimeMillis()/1000).toString())
                post.id = id
                FirebaseFirestore.getInstance().collection("Post").document(id).set(post)
            }

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        editText!!.requestFocus()
        showSoftKeyBoard()
    }

    protected fun showSoftKeyBoard() {
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    protected fun hideSoftKeyBoard(context: Context?) {
        try {
            (context as Activity?)!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (context?.currentFocus != null && context?.currentFocus!!.windowToken != null) {
                (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow((context as Activity?)!!.currentFocus!!.windowToken, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}