package albertperez.healthsites.fragments

import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class InfoHealthSiteProfileFragment(
    private val vegan: Boolean,
    private val vegetarian: Boolean,
    private val ecologic: Boolean,
    private val freeGluten: Boolean,
    private val freeLactose: Boolean,
    private val restaurant: Boolean,
    private val store: Boolean
) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.info_healthsite_profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!vegan) {
            view.findViewById<View>(R.id.vegan_layout).visibility = View.GONE
        }
        if (!vegetarian) {
            view.findViewById<View>(R.id.vegetarian_layout).visibility = View.GONE
        }
        if (!ecologic) {
            view.findViewById<View>(R.id.ecologic_layout).visibility = View.GONE
        }
        if (!freeGluten) {
            view.findViewById<View>(R.id.free_gluten_layout).visibility = View.GONE
        }
        if (!freeLactose) {
            view.findViewById<View>(R.id.free_lactose_layout).visibility = View.GONE
        }
        if (!restaurant) {
            view.findViewById<View>(R.id.restaurant_layout).visibility = View.GONE
        }
        if (!store) {
            view.findViewById<View>(R.id.store_layout).visibility = View.GONE
        }
    }
}