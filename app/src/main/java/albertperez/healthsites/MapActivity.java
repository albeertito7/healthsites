package albertperez.healthsites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import albertperez.healthsites.fragments.FiltersFragment;
import albertperez.healthsites.fragments.MapFragment;
import albertperez.healthsites.fragments.MyHealthSitesFragment;
import albertperez.healthsites.fragments.ProfileFragment;
import albertperez.healthsites.fragments.SearchableFragment;


public class MapActivity extends BaseActivity implements ProfileFragment.ProfileFragmentListener, DialogProfile.DialogProfileListener, SearchableFragment.Searchable {

    private static final String TAG = MapActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 3;

    private MapFragment mapFragment;
    private Fragment profile;
    private Fragment list;
    private Fragment filters;

    protected List<HealthSite> mClusterMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setOnNavigationItemSelectedListener();

        mapFragment = new MapFragment();
        profile = new ProfileFragment();
        list = new MyHealthSitesFragment();
        filters = new FiltersFragment();
        makeMapCurrentFragment(mapFragment);
    }

    @Override
    protected void onPause(){
        super.onPause();
        removeOnNavigationItemSelectedListener();
    }

    @Override
    protected void onRestart(){
        super.onRestart();

        if(getSupportFragmentManager().findFragmentByTag("map_fragment") != null && getSupportFragmentManager().findFragmentByTag("map_fragment").isVisible()) {
            ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.item_map);
        }
        else if(getSupportFragmentManager().findFragmentByTag("profile_fragment") != null && getSupportFragmentManager().findFragmentByTag("profile_fragment").isVisible()) {
            ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.item_profile);
        }
        else if(getSupportFragmentManager().findFragmentByTag("list_fragment") != null && getSupportFragmentManager().findFragmentByTag("list_fragment").isVisible()) {
            ((BottomNavigationView) findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.item_post);
        }
        setOnNavigationItemSelectedListener();
    }

    @Override
    public void onBackPressed() {
        /*if(getSupportFragmentManager().findFragmentByTag("map_fragment") != null && getSupportFragmentManager().findFragmentByTag("map_fragment").isVisible() &&
                !((SearchView) findViewById(R.id.searchable)).isIconified()) {
            ((SearchView) findViewById(R.id.searchable)).setIconified(true);
        } else {
            finish();
        }*/
        finish();
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResultMap");
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if(data != null){
                    Bundle extras = data.getExtras();
                    if(extras != null) {
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        assert bitmap != null;
                        handleUpload(bitmap);
                    }
                }
                break;

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtils.MY_CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Toast.makeText(this, R.string.user_interaction_was_cancelled, Toast.LENGTH_SHORT).show();
            } else if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.CAMERA)) {
                dispatchTakePictureIntent();
            } else {
                PermissionUtils.PermissionDeniedDialogCamera.newInstance(false).show(getSupportFragmentManager(), "deniedDialogCamera");
            }
        }
    }

    private void removeOnNavigationItemSelectedListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        bottomNavigationView.setOnLongClickListener(null);
    }

    private void setOnNavigationItemSelectedListener() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_post:
                        makeListPostCurrentFragment(list);
                        break;
                    case R.id.item_explore:
                        makeFiltersCurrentFragment(filters);
                        break;
                    case R.id.item_map:
                        makeMapCurrentFragment(mapFragment);
                        break;
                    case R.id.item_add:
                        startActivity(new Intent(getApplicationContext(), AddHealthSiteActivity.class), ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_up, R.anim.fade_out).toBundle());
                        break;
                    case R.id.item_profile:
                        makeProfileCurrentFragment(profile);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    private void makeProfileCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fl_wrapper, fragment, "profile_fragment")
                .addToBackStack(null)
                .commit();
    }

    private void makeMapCurrentFragment(MapFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fl_wrapper, fragment, "map_fragment")
                .addToBackStack(null)
                .commit();
    }

    private void makeListPostCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fl_wrapper, fragment, "list_fragment")
                .addToBackStack(null)
                .commit();
    }

    private void makeFiltersCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.fade_out, R.anim.slide_up, R.anim.fade_out)
                .replace(R.id.fl_wrapper, fragment, "filters_fragment")
                .addToBackStack(null)
                .commit();
    }

    public void dialogUsernameListener(final String v) {
        Objects.requireNonNull(getMFs()).collection("Users").document(Objects.requireNonNull(Objects.requireNonNull(getMAuth()).getCurrentUser()).getUid()).update("username", v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        EditText editText = findViewById(R.id.profile_username);
                        if(editText != null) editText.setText(v);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.update_username_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void dialogNameListener(final String v) {
        Objects.requireNonNull(getMFs()).collection("Users").document(Objects.requireNonNull(Objects.requireNonNull(getMAuth()).getCurrentUser()).getUid()).update("name", v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EditText editText = findViewById(R.id.profile_name);
                        if(editText != null) editText.setText(v);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.update_name_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void dialogSurnameListener(final String v) {
        Objects.requireNonNull(getMFs()).collection("Users").document(Objects.requireNonNull(Objects.requireNonNull(getMAuth()).getCurrentUser()).getUid()).update("surname", v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        EditText editText = findViewById(R.id.profile_surname);
                        if(editText != null) editText.setText(v);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.update_surname_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void dialogEmailListener(final String v) {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updateEmail(v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("email", v);
                        EditText editText = findViewById(R.id.profile_email);
                        if(editText != null) editText.setText(v);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.update_email_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void dialogGenderListener(final String v) {
        Objects.requireNonNull(getMFs()).collection("Users").document(Objects.requireNonNull(Objects.requireNonNull(getMAuth()).getCurrentUser()).getUid()).update("gender", v)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        EditText editText = findViewById(R.id.profile_gender);
                        if(editText != null) editText.setText(v);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.update_gender_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUpload(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("profileImages").child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageView imageView = findViewById(R.id.profile_image);
                        if((imageView) != null) imageView.setImageBitmap(bitmap);
                        getDownloadUrl(reference);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                setUserProfile(uri);
            }
        });
    }

    private void setUserProfile(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
        assert user != null;
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), R.string.uploaded_photo_successfully, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.uploaded_photo_failure, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void dispatchTakePictureIntent() {
        startActivityForResult(ImagePicker.getPickImageIntent(this), CAMERA_REQUEST_CODE);
    }

    @Override
    public void initSearchable() {
        mClusterMarkers = new ArrayList<>();
    }

    @Override
    public void add(@Nullable HealthSite hs) {
        mClusterMarkers.add(hs);
    }

    @NotNull
    @Override
    public List<HealthSite> get() {
        return mClusterMarkers;
    }
}