package albertperez.healthsites

import albertperez.healthsites.fragments.HealthSiteOptionsFragment
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class HealthSiteOptionsActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        custom_toolbar_top.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(custom_toolbar_top)
        supportActionBar!!.title = getString(R.string.options)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        custom_toolbar_top.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
        }

        if (fragment_container != null) {
            if (!(savedInstanceState != null)){
                var bundle = Bundle()
                bundle.putString("id", intent.getStringExtra("id"))
                bundle.putString("name", intent.getStringExtra("name"))
                bundle.putString("slogan", intent.getStringExtra("slogan"))
                var fr:Fragment = HealthSiteOptionsFragment()
                fr.arguments = bundle
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fr).commit()
            }
        }
    }

    override fun onBackPressed() {
        custom_toolbar_top.callOnClick()

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        return true
    }

    companion object {
    }
}