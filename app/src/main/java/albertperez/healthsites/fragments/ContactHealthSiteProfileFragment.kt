package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ContactHealthSiteProfileFragment(
    private val address: String,
    private val webSite: String,
    private val webMail: String,
    private val phoneNumber: String
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.contact_healthsite_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.findViewById<View>(R.id.contact_healthsite_profile_address) as TextView).text = address
        (view.findViewById<View>(R.id.contact_healthsite_profile_webSite) as TextView).text = webSite
        (view.findViewById<View>(R.id.contact_healthsite_profile_webMail) as TextView).text = webMail
        (view.findViewById<View>(R.id.contact_healthsite_profile_phone_number) as TextView).text = phoneNumber
    }
}