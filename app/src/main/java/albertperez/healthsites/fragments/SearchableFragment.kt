package albertperez.healthsites.fragments

import albertperez.healthsites.HealthSite
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.util.*

abstract class SearchableFragment : Fragment() {

    protected var listener: Searchable? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is Searchable) {
            context
        } else {
            throw RuntimeException("$context interface FragmentComm not implemented")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    protected fun showSoftKeyBoard(view: View?) {
        (Objects.requireNonNull(requireContext().getSystemService(Context.INPUT_METHOD_SERVICE)) as InputMethodManager).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    interface Searchable {
        fun initSearchable()
        fun add(hs: HealthSite?)
        fun get(): List<HealthSite>
    }
}