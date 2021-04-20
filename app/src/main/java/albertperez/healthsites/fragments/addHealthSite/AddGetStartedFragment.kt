package albertperez.healthsites.fragments.addHealthSite

import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class AddGetStartedFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_get_started_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_add_get_started)
            .setOnClickListener {
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
                    .replace(R.id.container_add_fr, AddHealthSiteDataFragment(), "add_healthsite_data")
                    .addToBackStack(null)
                    .commit()
            }
    }
}