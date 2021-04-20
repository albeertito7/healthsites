package albertperez.healthsites.fragments

import albertperez.healthsites.R
import albertperez.healthsites.fragments.addHealthSite.DialogInfo
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_filters.*

class FiltersFragment: Fragment() {
    private var mPrefs: SharedPreferences? = null
    private var vegan: CardView? = null
    private var vegetarian: CardView? = null
    private var ecologic: CardView? = null
    private var freeGluten: CardView? = null
    private var freeLactose: CardView? = null
    private var restaurant: CardView? = null
    private var store: CardView? = null
    private var verified: CardView? = null

    private var veganCheck = false
    private var vegetarianCheck = false
    private var ecologicCheck = false
    private var freeGlutenCheck = false
    private var freeLactoseCheck = false
    private var restaurantCheck = false
    private var storeCheck = false
    private var verifiedCheck = false
    private var veganCheck2 = false
    private var vegetarianCheck2 = false
    private var ecologicCheck2 = false
    private var freeGlutenCheck2 = false
    private var freeLactoseCheck2 = false
    private var restaurantCheck2 = false
    private var storeCheck2 = false
    private var verifiedCheck2 = false
    private var changedApplied = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPrefs = PreferenceManager.getDefaultSharedPreferences(activity)

        if(!mPrefs!!.getBoolean("filters_introduction_done", false)) {
            val dialog = DialogInfo("Introduction", R.layout.custom_info_dialog_intro_to_filters)
            dialog.show(requireActivity().supportFragmentManager, "dialogIntroFilters")
            val editor = mPrefs!!.edit()
            editor.putBoolean("filters_introduction_done", true)
            editor.apply()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.include_toolbar)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setTitle(R.string.filters)

        vegan = view.findViewById(R.id.vegan_filter)
        vegetarian = view.findViewById(R.id.vegetarian_filter)
        ecologic = view.findViewById(R.id.ecologic_filter)
        freeGluten = view.findViewById(R.id.free_gluten_filter)
        freeLactose = view.findViewById(R.id.free_lactose_filter)
        restaurant = view.findViewById(R.id.restaurant_filter)
        store = view.findViewById(R.id.store_filter)
        verified = view.findViewById(R.id.verified_filter)

        setFilters()
        setListeners()
    }

    private fun setFilters() {
        if (mPrefs!!.getBoolean("vegan_filter", false)) {
            vegan!!.elevation = 1f
            vegan_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            veganCheck = true
        }
        if (mPrefs!!.getBoolean("vegetarian_filter", false)) {
            vegetarian_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            vegetarian!!.elevation = 1f
            vegetarianCheck = true
        }
        if (mPrefs!!.getBoolean("ecologic_filter", false)) {
            ecologic_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            ecologic!!.elevation = 1f
            ecologicCheck = true
        }
        if (mPrefs!!.getBoolean("free_gluten_filter", false)) {
            free_gluten_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            freeGluten!!.elevation = 1f
            freeGlutenCheck = true
        }
        if (mPrefs!!.getBoolean("free_lactose_filter", false)) {
            free_lactose_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            freeLactose!!.elevation = 1f
            freeLactoseCheck = true
        }
        if (mPrefs!!.getBoolean("restaurant_filter", false)) {
            restaurant_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            restaurant!!.elevation = 1f
            restaurantCheck = true
        }
        if (mPrefs!!.getBoolean("store_filter", false)) {
            store_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            store!!.elevation = 1f
            storeCheck = true
        }
        if (mPrefs!!.getBoolean("verified_filter", false)) {
            verified_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
            verified!!.elevation = 1f
            verifiedCheck = true
        }
    }

    private fun setListeners() {
        vegan!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("vegan_filter", false)) {
                editor.putBoolean("vegan_filter", true)
                vegan_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                veganCheck2 = true
            } else {
                editor.putBoolean("vegan_filter", false)
                vegan_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                veganCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }
        vegetarian!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("vegetarian_filter", false)) {
                editor.putBoolean("vegetarian_filter", true)
                vegetarian_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                vegetarianCheck2 = true
            } else {
                editor.putBoolean("vegetarian_filter", false)
                vegetarian_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                vegetarianCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        ecologic!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("ecologic_filter", false)) {
                editor.putBoolean("ecologic_filter", true)
                ecologic_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                ecologicCheck2 = true
            } else {
                editor.putBoolean("ecologic_filter", false)
                ecologic_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                ecologicCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        freeGluten!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("free_gluten_filter", false)) {
                editor.putBoolean("free_gluten_filter", true)
                free_gluten_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                freeGlutenCheck2 = true
            } else {
                editor.putBoolean("free_gluten_filter", false)
                free_gluten_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                freeGlutenCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        freeLactose!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("free_lactose_filter", false)) {
                editor.putBoolean("free_lactose_filter", true)
                free_lactose_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                freeLactoseCheck2 = true
            } else {
                editor.putBoolean("free_lactose_filter", false)
                free_lactose_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                freeLactoseCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        restaurant!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("restaurant_filter", false)) {
                editor.putBoolean("restaurant_filter", true)
                restaurant_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                restaurantCheck2 = true
            } else {
                editor.putBoolean("restaurant_filter", false)
                restaurant_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                restaurantCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        store!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("store_filter", false)) {
                editor.putBoolean("store_filter", true)
                store_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                storeCheck2 = true
            } else {
                editor.putBoolean("store_filter", false)
                store_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                storeCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }

        verified!!.setOnClickListener { v ->
            val editor = mPrefs!!.edit()
            if (!mPrefs!!.getBoolean("verified_filter", false)) {
                editor.putBoolean("verified_filter", true)
                verified_filter_layout.setBackgroundResource(R.drawable.bg_autocomplete)
                v.elevation = 1f
                verifiedCheck2 = true
            } else {
                editor.putBoolean("verified_filter", false)
                verified_filter_layout.setBackgroundResource(0)
                v.elevation = 22f
                verifiedCheck2 = false
            }
            editor.apply()
            setPrefFiltersChanged()
        }
    }

    private fun setPrefFiltersChanged() {
        if (veganCheck != veganCheck2 || vegetarianCheck != vegetarianCheck2 || ecologicCheck != ecologicCheck2 || freeGlutenCheck != freeGlutenCheck2 || freeLactoseCheck != freeLactoseCheck2 || restaurantCheck != restaurantCheck2 || storeCheck != storeCheck2 || verifiedCheck != verifiedCheck2) {
            val editor = mPrefs!!.edit()
            editor.putBoolean("filters_changed", true)
            editor.apply()
            changedApplied = true
        } else {
            if (changedApplied) {
                val editor = mPrefs!!.edit()
                editor.putBoolean("filters_changed", false)
                editor.apply()
            }
        }
    }
}