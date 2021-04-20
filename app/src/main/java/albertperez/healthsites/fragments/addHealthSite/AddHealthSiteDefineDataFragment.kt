package albertperez.healthsites.fragments.addHealthSite

import albertperez.healthsites.MultiCheckBoxWatcher
import albertperez.healthsites.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton

class AddHealthSiteDefineDataFragment : AddFragment() {
    private var restaurant: CheckBox? = null
    private var store: CheckBox? = null
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_healthsite_define_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restaurant = view.findViewById(R.id.check_restaurant)
        store = view.findViewById(R.id.check_store)
        next = view.findViewById(R.id.btn_next_add_healhtsite_define)

        MultiCheckBoxWatcher()
            .registerCheckBox(restaurant!!)
            .registerCheckBox(store!!)
            .setCallback(object : MultiCheckBoxWatcher.CheckBoxWatcherWithInstance {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (atLeastOnePropertyChecked()) {
                        listener!!.setCategories(restaurant!!.isChecked, store!!.isChecked)
                        next!!.alpha = 1f
                        next!!.isEnabled = true
                    } else {
                        next!!.alpha = 0.3.toFloat()
                        next!!.isEnabled = false
                    }
                }
            })

        next!!.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
                .replace(R.id.container_add_fr, AddHealthSitePickDataFragment(), "add_pick_data")
                .addToBackStack(null)
                .commit()
        }
    }

    private fun atLeastOnePropertyChecked(): Boolean {
        return restaurant!!.isChecked || store!!.isChecked
    }
}