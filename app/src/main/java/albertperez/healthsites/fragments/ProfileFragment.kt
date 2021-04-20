package albertperez.healthsites.fragments

import albertperez.healthsites.PermissionUtils
import albertperez.healthsites.R
import albertperez.healthsites.fragments.addHealthSite.DialogInfo
import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_profile.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class ProfileFragment() : Fragment() {

    var listener: ProfileFragmentListener? = null
    var followingCounts: Int? = null
    var followingCounter: TextView? = null
    var postCounts: Int? = null
    var postCounter: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.include_toolbar)
        toolbar.title = getString(R.string.profile)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

        val user = FirebaseAuth.getInstance().currentUser
        if(user!!.photoUrl != null) {
            Glide.with(requireActivity()).load(user.photoUrl).into(profile_image)
        }

        followingCounter = view.findViewById(R.id.profile_follow_counter);
        postCounter = view.findViewById(R.id.profile_post_counter);

        setFollowCounterStatus(FirebaseFirestore.getInstance().collection("userFollowing").document(user.uid))
        setPostsCounterStatus(FirebaseFirestore.getInstance().collection("Post").whereEqualTo("userId",FirebaseAuth.getInstance().currentUser!!.uid));

        profile_birth_layout.setEndIconOnClickListener {
            val dpd = DatePickerDialog(requireContext());
            dpd.setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                @Suppress("DEPRECATION") val newDate: Long = Date(year-1900, monthOfYear, dayOfMonth).time
                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser()!!.getUid())
                    .update("dateOfBirth", newDate)
                    .addOnSuccessListener {
                        profile_birth?.setText(SimpleDateFormat("MM/dd/yyyy").format(newDate))
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireActivity().applicationContext, getString(R.string.update_gender_failure), Toast.LENGTH_SHORT).show()
                    }
            }
            dpd.show()
        }

        profile_email_layout.setEndIconOnClickListener {
            DialogEmail().show(requireActivity().supportFragmentManager, "dialogEmail")
        }
        profile_name_layout.setEndIconOnClickListener {
            DialogName().show(requireActivity().supportFragmentManager, "dialogName")
        }
        profile_surname_layout.setEndIconOnClickListener {
            DialogSurname().show(requireActivity().supportFragmentManager, "dialogSurname")
        }

        profile_gender_layout.setEndIconOnClickListener {
            DialogGender().show(requireActivity().supportFragmentManager, "dialogGender")
        }

        change_photo_profile.setOnClickListener {
            if(checkCameraHardware(requireContext())) {
                if(!checkCameraPermission()) {
                    PermissionUtils.requestPermission(requireActivity() as AppCompatActivity?, PermissionUtils.MY_CAMERA_PERMISSION_REQUEST_CODE, Manifest.permission.CAMERA, false);
                } else {
                    listener!!.dispatchTakePictureIntent()
                }
            }
        }

        profile_image.setOnClickListener {
            change_photo_profile.callOnClick()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setListener(context)

        val mPrefs = PreferenceManager.getDefaultSharedPreferences(activity)
        if(!mPrefs!!.getBoolean("profile_introduction_done", false)) {
            val dialog = DialogInfo("Introduction", R.layout.custom_info_dialog_intro_to_profile);
            dialog.show(requireActivity().supportFragmentManager, "dialogIntroProfile")
            val editor = mPrefs.edit()
            editor.putBoolean("profile_introduction_done", true)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()

        val lang = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString("pref_language", "")
        if (!lang!!.isEmpty()) {
            val locale = Locale(lang)
            val config = Configuration()
            val dm = requireContext().resources.displayMetrics
            Locale.setDefault(locale)
            config.locale = locale
            requireContext().resources.updateConfiguration(config, dm)
        }


        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().currentUser!!.uid).get()
            .addOnSuccessListener { it ->
                if (it != null) {
                    profile_username?.setText(it["username"].toString())
                    profile_name?.setText(it["name"].toString())
                    profile_surname?.setText(it["surname"].toString())
                    profile_email?.setText(it["email"].toString())
                    profile_birth?.setText(SimpleDateFormat("MM/dd/yyyy").format(Date(it["dateOfBirth"].toString().toLong())))
                    profile_gender?.setText(it["gender"].toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, getString(R.string.profile_data_load_failure), Toast.LENGTH_SHORT)?.show()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.settings_menu, menu)
    }

    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun setListener(context: Context) {
        if (context is ProfileFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context interface listener not implemented")
        }
    }

    private fun setFollowCounterStatus(reference: DocumentReference) {
        reference.addSnapshotListener { value, _ ->
            if(value != null && value.data != null) {
                followingCounts = value.data!!.size
                followingCounter!!.text = getString(R.string.following) + ": " + followingCounts.toString()
            }
        }
    }

    private fun setPostsCounterStatus(query: Query) {
        query.addSnapshotListener { value, _ ->
            if(value != null) {
                postCounts = value.documents.size
                postCounter!!.text = getString(R.string.posts) + ": " + postCounts.toString()
            }
        }
    }

    interface ProfileFragmentListener {
        fun dispatchTakePictureIntent()
    }

    companion object {
    }
}