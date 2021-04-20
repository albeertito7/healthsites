package albertperez.healthsites.fragments.addHealthSite

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class AddFragment : Fragment() {
    protected var listener: addFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = if (context is addFragmentListener) {
            context
        } else {
            throw RuntimeException("$context interface addFragmentListener not implemented")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    protected fun showSoftKeyBoard(view: View?) {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            view,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    protected fun hideSoftKeyBoard() {
        (requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            requireActivity().currentFocus!!.windowToken,
            0
        )
    }

    interface addFragmentListener {
        fun setName(name: String?)
        fun setDescription(description: String?)
        fun setProperties(vegan: Boolean, vegetarian: Boolean, ecologic: Boolean, freeGluten: Boolean, freeLactose: Boolean)
        fun setLocation(latitude: Double?, longitude: Double?, address: String?, countryCode: String?, country: String?, adminArea: String?, city: String?, postalCode: String?, street: String?, number: String?)
        fun setImage(image: Bitmap?)
        fun setCompanyName(companyName: String?)
        fun setCompanyCIF(companyCIF: String?)
        fun setWebSite(webSite: String?)
        fun setWebMail(webMail: String?)
        fun setPhoneNumber(phoneNumber: String?)
        fun setCategories(restaurant: Boolean, store: Boolean)
        fun register()
    }

    protected fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    protected fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }
}