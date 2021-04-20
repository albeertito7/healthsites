package albertperez.healthsites

import albertperez.healthsites.fragments.addHealthSite.AddFragment.addFragmentListener
import albertperez.healthsites.fragments.addHealthSite.AddGetStartedFragment
import albertperez.healthsites.fragments.addHealthSite.AddHealthSitePickDataFragment
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_healthsite.*
import kotlinx.android.synthetic.main.add_register_fragment.*
import java.io.ByteArrayOutputStream

class AddHealthSiteActivity : MainActivity(), addFragmentListener {

    private val healthSite = HealthSite()
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_healthsite)

        btn_add_go_back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.slide_down)
        }

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.slide_in_from_right, R.anim.fade_out)
            .replace(R.id.container_add_fr, AddGetStartedFragment(), "add_get_started")
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()
        btn_add_go_back.background.alpha = 15
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("add_get_started")!!.isVisible) {
            findViewById<View>(R.id.btn_add_go_back).callOnClick()
        } else {
            super.onBackPressed()
            btn_add_go_back.background.alpha = 15
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionUtils.MY_CAMERA_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                (supportFragmentManager.findFragmentByTag("add_pick_data") as AddHealthSitePickDataFragment).dispatchTakePictureIntent()
            } else {
                PermissionUtils.PermissionDeniedDialogLocation.newInstance(false)
                    .show(supportFragmentManager, "deniedDialogLocation")
            }
        }
    }

    override fun setName(name: String?) {
        healthSite.name = name
    }

    override fun setDescription(description: String?) {
        healthSite.description = description
    }

    override fun setProperties(vegan: Boolean, vegetarian: Boolean, ecologic: Boolean, freeGluten: Boolean, freeLactose: Boolean) {
        healthSite.isVegan = vegan
        healthSite.isVegetarian = vegetarian
        healthSite.isEcologic = ecologic
        healthSite.isFreeGluten = freeGluten
        healthSite.isFreeLactose = freeLactose
    }

    override fun setLocation(latitude: Double?, longitude: Double?, address: String?, countryCode: String?, country: String?,
                             adminArea: String?, city: String?, postalCode: String?, street: String?, number: String?) {
        healthSite.latitude = latitude
        healthSite.longitude = longitude
        healthSite.address = address
        healthSite.countryCode = countryCode
        healthSite.country = country
        healthSite.adminArea = adminArea
        healthSite.city = city
        healthSite.postalCode = postalCode
        healthSite.street = street
        healthSite.number = number
    }

    override fun setImage(image: Bitmap?) {
        this.image = image
    }

    override fun setCompanyName(companyName: String?) {
        healthSite.companyName = companyName
    }

    override fun setCompanyCIF(companyCIF: String?) {
        healthSite.companyCIF = companyCIF
    }

    override fun setWebSite(webSite: String?) {
        healthSite.website = webSite
    }

    override fun setWebMail(webMail: String?) {
        healthSite.webMail = webMail
    }

    override fun setPhoneNumber(phoneNumber: String?) {
        healthSite.phoneNumber = phoneNumber
    }

    override fun setCategories(restaurant: Boolean, store: Boolean) {
        healthSite.isRestaurant = restaurant
        healthSite.isStore = store
    }

    @Suppress("NAME_SHADOWING")

    override fun register() {
        showProgress()
        FirebaseFirestore.getInstance()
            .collection("HealthSites")
            .whereEqualTo("companyCIF", healthSite.companyCIF).get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful && task.result!!.documents.size == 0) {
                    val id = FirebaseFirestore.getInstance()
                        .collection("HealthSites")
                        .document().id
                    this.healthSite.setId(id)
                    val map: Map<String, Any> = jacksonObjectMapper().convertValue(this.healthSite)
                    FirebaseFirestore.getInstance()
                        .collection("HealthSites")
                        .document(id).set(map)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                handleUpload(image!!, id)
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.healthsite_register_ok),
                                    Toast.LENGTH_SHORT).show()
                            } else if (task.isCanceled) {
                                hideProgress()
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.healthsite_register_ko),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    hideProgress()
                    Toast.makeText(this,
                        "This company CIF is already taken",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun handleUpload(bitmap: Bitmap, id: String) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val reference = FirebaseStorage.getInstance().reference.child("healthSitesImages").child("$id.jpeg")
        reference.putBytes(baos.toByteArray())
            .addOnSuccessListener {
                btn_add_go_back.callOnClick()
                Toast.makeText(applicationContext, getString(R.string.healthsite_image_upload_ok), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                hideProgress()
                Toast.makeText(applicationContext, getString(R.string.healthsite_image_upload_ko), Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgress() {
        val obj = ObjectAnimator.ofFloat(layout_add_healthsite, View.ALPHA, 0.1f).setDuration(200)
        obj.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                btn_register.isEnabled = false
                btn_add_go_back.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator) {
                progress_bar_add_healthsite.visibility = View.VISIBLE
                textView_add_healthsite.visibility = View.VISIBLE
            }
        })
        obj.start()
    }

    private fun hideProgress() {
        val obj = ObjectAnimator.ofFloat(layout_add_healthsite, View.ALPHA, 1f).setDuration(200)
        obj.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                progress_bar_add_healthsite.visibility = View.GONE
                textView_add_healthsite.visibility = View.GONE
            }
            override fun onAnimationEnd(animation: Animator) {
                btn_register.isEnabled = true
                btn_add_go_back.isEnabled = true
            }
        })
        obj.start()
    }

    companion object {
        //private val TAG = AddHealthSiteActivity::class.java.simpleName
    }
}