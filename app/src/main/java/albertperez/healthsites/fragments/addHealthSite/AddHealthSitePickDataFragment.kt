package albertperez.healthsites.fragments.addHealthSite

import albertperez.healthsites.*
import android.Manifest
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class AddHealthSitePickDataFragment : AddFragment() {
    private var placePicked = false
    private var imagePicked = false
    private var next: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_healthsite_pick_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        next = view.findViewById(R.id.btn_next_add_healhtsite_pick)

        view.findViewById<View>(R.id.btn_add_pickplace)
            .setOnClickListener {
                val intent = Intent(context, PlacePickerActivity::class.java)
                startActivityForResult(intent, PLACE_PICKER_REQUEST, ActivityOptions.makeCustomAnimation(context, R.anim.slide_up, R.anim.fade_out).toBundle())
            }

        view.findViewById<View>(R.id.btn_add_image)
            .setOnClickListener {
                if (checkCameraHardware(requireContext())) {
                    if (!checkCameraPermission()) {
                        PermissionUtils.requestPermission(context as MainActivity?, PermissionUtils.MY_CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA, false)
                    } else {
                        startActivityForResult(ImagePicker.getPickImageIntent(context), PICK_IMAGE_REQUEST)
                    }
                }
            }

        next!!.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_from_right, R.anim.fade_out, R.anim.slide_in_from_left, R.anim.fade_out)
                .replace(R.id.container_add_fr, AddCompanyDataFragment(), "add_company_data")
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PLACE_PICKER_REQUEST -> if (resultCode == Activity.RESULT_OK) {
                listener!!.setLocation(
                    data!!.getDoubleExtra("latitude", 0.0),
                    data.getDoubleExtra("longitude", 0.0),
                    data.getStringExtra("address"),
                    data.getStringExtra("countryCode"),
                    data.getStringExtra("country"),
                    data.getStringExtra("adminArea"),
                    data.getStringExtra("city"),
                    data.getStringExtra("postalCode"),
                    data.getStringExtra("street"),
                    data.getStringExtra("number")
                )
                placePicked = true
                placePickedListener()
            }
            PICK_IMAGE_REQUEST -> if (data != null) {
                val extras = data.extras
                if (extras != null) {
                    listener!!.setImage(extras["data"] as Bitmap?)
                    imagePicked = true
                    imagePickedListener()
                }
            }
        }
    }

    private fun placePickedListener() {
        if (imagePicked) {
            next!!.alpha = 1f
            next!!.isEnabled = true
        } else {
            next!!.alpha = 0.3.toFloat()
            next!!.isEnabled = false
        }
    }

    private fun imagePickedListener() {
        if (placePicked) {
            next!!.alpha = 1f
            next!!.isEnabled = true
        } else {
            next!!.alpha = 0.3.toFloat()
            next!!.isEnabled = false
        }
    }

    fun dispatchTakePictureIntent() {
        startActivityForResult(
            ImagePicker.getPickImageIntent(context),
            PICK_IMAGE_REQUEST
        )
    }

    companion object {
        const val PLACE_PICKER_REQUEST = 1
        const val PICK_IMAGE_REQUEST = 2
    }
}