package albertperez.healthsites

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment

open class DialogProfile : DialogFragment() {

    protected var listener: DialogProfileListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as DialogProfileListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + "must implement DialogEmailListener")
        }
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

    override fun onPause() {
        super.onPause()
        hideSoftKeyBoard(context)
    }

    interface DialogProfileListener {
        fun dialogEmailListener(email: String?)
        fun dialogNameListener(name: String?)
        fun dialogSurnameListener(surname: String?)
        fun dialogUsernameListener(username: String?)
        fun dialogGenderListener(gender: String?)
    }
}