package albertperez.healthsites.fragments

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.util.*

abstract class SignUpFragment : Fragment() {

    protected var fragmentComm: FragmentComm? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentComm = if (context is FragmentComm) {
            context
        } else {
            throw RuntimeException("$context interface FragmentComm not implemented")
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentComm = null
    }

    protected fun showSoftKeyBoard(view: View?) {
        (Objects.requireNonNull(requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)) as InputMethodManager).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    interface FragmentComm {
        fun setName(name: String?)
        fun setSurname(surname: String?)
        fun setUsername(username: String?)
        fun setEmail(email: String?)
        fun setPassword(password: String?)
        fun setBirth(birth: Long?)
        fun setGender(gender: String?)
        fun showSummary()
        fun signUp()
    }
}