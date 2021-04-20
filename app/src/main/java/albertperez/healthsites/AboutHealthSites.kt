package albertperez.healthsites

import android.os.Bundle
import kotlinx.android.synthetic.main.toolbar_layout.*

class AboutHealthSites : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_healthsites)

        custom_toolbar_top.setNavigationIcon(R.drawable.icon_back)
        setSupportActionBar(custom_toolbar_top)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        custom_toolbar_top.setNavigationOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right)
        }
    }

    override fun onBackPressed() {
        custom_toolbar_top.callOnClick()
    }
}