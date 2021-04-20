package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class SignUpGetStartedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.get_started_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.findViewById<View>(R.id.btn_get_started) as Button).setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_from_right,
                    R.anim.fade_out,
                    R.anim.slide_in_from_left,
                    R.anim.fade_out
                )
                .replace(R.id.container_fr, SignUpNameSurnameFragment(), "name_surname")
                .addToBackStack(null)
                .commit()
        }
    }
}