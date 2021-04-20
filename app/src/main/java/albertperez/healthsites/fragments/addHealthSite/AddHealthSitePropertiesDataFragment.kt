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

class AddHealthSitePropertiesDataFragment : AddFragment() {
    private var vegan: CheckBox? = null
    private var vegetarian: CheckBox? = null
    private var ecologic: CheckBox? = null
    private var freeGluten: CheckBox? = null
    private var freeLactose: CheckBox? = null
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_healthsite_properties_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vegan = view.findViewById(R.id.check_vegan)
        vegetarian = view.findViewById(R.id.check_vegetarian)
        ecologic = view.findViewById(R.id.check_ecologic)
        freeGluten = view.findViewById(R.id.check_free_gluten)
        freeLactose = view.findViewById(R.id.check_free_lactose)
        next = view.findViewById(R.id.btn_next_add_healhtsite_properties)

        MultiCheckBoxWatcher()
            .registerCheckBox(vegan!!)
            .registerCheckBox(vegetarian!!)
            .registerCheckBox(ecologic!!)
            .registerCheckBox(freeGluten!!)
            .registerCheckBox(freeLactose!!)
            .setCallback(object : MultiCheckBoxWatcher.CheckBoxWatcherWithInstance {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (atLeastOnePropertyChecked()) {
                        listener!!.setProperties(
                            vegan!!.isChecked,
                            vegetarian!!.isChecked,
                            ecologic!!.isChecked,
                            freeGluten!!.isChecked,
                            freeLactose!!.isChecked
                        )
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
                .replace(R.id.container_add_fr, AddHealthSiteDefineDataFragment(), "add_define_data")
                .addToBackStack(null)
                .commit()
        }
    }

    private fun atLeastOnePropertyChecked(): Boolean {
        return vegan!!.isChecked || vegetarian!!.isChecked || ecologic!!.isChecked || freeGluten!!.isChecked || freeLactose!!.isChecked
    }
}