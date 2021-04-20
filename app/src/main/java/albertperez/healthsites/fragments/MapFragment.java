package albertperez.healthsites.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;

import java.util.ArrayList;
import java.util.Objects;

import albertperez.healthsites.AutocompleteHealthSiteAdapter;
import albertperez.healthsites.CustomClusterRenderer;
import albertperez.healthsites.CustomInfoWindowAdapter;
import albertperez.healthsites.HealthSite;
import albertperez.healthsites.HealthSiteProfileActivity;
import albertperez.healthsites.MapActivity;
import albertperez.healthsites.PermissionUtils;
import albertperez.healthsites.R;

public class MapFragment extends SearchableFragment implements OnMapReadyCallback {

    private static final String TAG = MapActivity.class.getSimpleName();
    private SupportMapFragment map;

    private static GoogleMap mMap;
    private UiSettings mUiSettings;
    private SharedPreferences mPrefs;

    private static final long UPDATE_INTERVAL_IN_MILISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILISECONDS = UPDATE_INTERVAL_IN_MILISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private ArrayList<HealthSite> mHealthSites;
    private ClusterManager<ClusterItem> mClusterManager;
    private ClusterRenderer mClusterRenderer;

    private Boolean initAll = false;
    private boolean initMapDone = true;
    private boolean firstLocation = true;

    private SearchView searchable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.include_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.map_options_menu, menu);
        searchable = (SearchView) menu.findItem(R.id.searchable).getActionView();
        searchable.setQueryHint("Search");

        if(mMap != null && getListener().get() != null) {
            setSearchable();
        }
    }

    private void setSearchable() {
        final AutocompleteHealthSiteAdapter adapter = new AutocompleteHealthSiteAdapter(requireContext(), getListener().get());
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchable.findViewById(R.id.search_src_text);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        requireActivity().getCurrentFocus().getWindowToken(), 0);

                Intent intent = new Intent(requireContext(), HealthSiteProfileActivity.class);
                intent.putExtra("healthsite", adapter.getItem(position));
                startActivity(intent, ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_up, R.anim.fade_out).toBundle());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mRequestingLocationUpdates) {
            Log.i(TAG, "onPause stopLocationUpdates");
            stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mMap != null) {
            if(initAll) {
                setMapStyle();
                setMapView();

                if(checkPermissions()) {
                    if(!mRequestingLocationUpdates) {
                        startLocationUpdates();
                        if (mUiSettings != null) {
                            mUiSettings.setMyLocationButtonEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_myLocationButton", false));
                            mUiSettings.setZoomControlsEnabled(Objects.requireNonNull(mPrefs).getBoolean("pref_zoomControls", false));
                            mUiSettings.setCompassEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_compass", false));
                            mUiSettings.setAllGesturesEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_gestures", false));
                        }
                    }

                    if(mPrefs.getBoolean("filters_changed", false)) {
                        SharedPreferences.Editor editor = mPrefs.edit();
                        editor.putBoolean("filters_changed", false);
                        editor.apply();
                        addMapMarkers();
                    }
                } else {
                    setPermissions();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRequestingLocationUpdates){
            Log.i(TAG,"onDestroy stopLocationUpdates");
            stopLocationUpdates();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        if(mMap == null) {
            mMap = googleMap;

            setMapStyle();
            setMapView();

            mUiSettings = mMap.getUiSettings();
            mUiSettings.setMapToolbarEnabled(true);
            mUiSettings.setCompassEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_compass", false));
            mUiSettings.setAllGesturesEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_gestures", false));
            mUiSettings.setZoomControlsEnabled(Objects.requireNonNull(mPrefs).getBoolean("pref_zoomControls", false));

            setPermissions();

            addMapMarkers();
        }
    }

    private void setPermissions() {
        if(!checkPermissions()) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionUtils.MY_LOCATION_PERMISSION_REQUEST_CODE);
            //PermissionUtils.requestPermission(getContext(), PermissionUtils.MY_LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            setLocationFunctionsEnabled();
        }
    }

    private boolean checkPermissions() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void setLocationFunctionsEnabled(){
        setMyLocationButtonEnabled();
        mRequestingLocationUpdates = true;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();
        startLocationUpdates();
        initAll = true;
    }

    @SuppressLint("MissingPermission")
    private void setMyLocationButtonEnabled(){
        mMap.setMyLocationEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(!Objects.requireNonNull(mPrefs).getBoolean("pref_myLocationButton", false));
    }

    private void createLocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates(){
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "startLocationUpdates All location settings are satisfied");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode){
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "startLocaionUpdates Location Settings are not satisfied.");
                                try{
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e1) {
                                    Log.i(TAG,"startLocationUpdates Pending Intent unabled");
                                    e1.printStackTrace();
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.e(TAG, "startLocationUpdates "+ R.string.settings_inadequate);
                                Toast.makeText(getContext(), R.string.settings_inadequate, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }
                        updateLocationUI();
                    }
                });
    }

    private void stopLocationUpdates(){
        if(!mRequestingLocationUpdates) return;

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void updateLocationUI(){
        if(mCurrentLocation != null && Objects.requireNonNull(mPrefs).getBoolean("pref_locationUpdates", false) || mCurrentLocation != null && firstLocation){
            CameraUpdate camera = CameraUpdateFactory.newLatLng(
                    new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())
            );
            mMap.animateCamera(camera);
            mMap.moveCamera(camera);
            firstLocation = false;
        }
    }

    private void addMapMarkers() {
        mHealthSites = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("HealthSites").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        mHealthSites.add(document.toObject(HealthSite.class));
                    }
                    addMarkersToClusterManager();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void addMarkersToClusterManager() {
        if(mMap != null) {
            mMap.clear();
            mClusterManager = new ClusterManager<>(getActivity(), mMap);

            mClusterRenderer = new CustomClusterRenderer(getActivity(), mMap, mClusterManager);
            mClusterManager.setRenderer(mClusterRenderer);

            setUpClusterListeners();

            getListener().initSearchable();
            for(HealthSite healthSite: mHealthSites) {
                try{
                    if(isValid(healthSite)) {
                        //ClusterMarker newClusterMarker = new ClusterMarker(healthSite.getPosition(), healthSite.getTitle(), healthSite.getSnippet(), R.drawable.icon_marker, healthSite.getName());
                        mClusterManager.addItem(healthSite);//adding to the map
                        getListener().add(healthSite);//making an easy access array list
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                }
            }
            mClusterManager.cluster();
            setSearchable();
        }
    }

    private void setUpClusterListeners() {
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<ClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<ClusterItem> cluster) {
                return false;
            }
        });

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterItem>() {
            @Override
            public boolean onClusterItemClick(ClusterItem item) {
                return false;
            }
        });

        mClusterManager.setOnClusterItemInfoWindowClickListener(
                new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                    @Override public void onClusterItemInfoWindowClick(ClusterItem item) {
                        Intent intent = new Intent(getContext(), HealthSiteProfileActivity.class);
                        intent.putExtra("healthsite", (HealthSite) item);
                        startActivity(intent, ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_up, R.anim.fade_out).toBundle());
                    }
                });

        mClusterManager.getMarkerCollection().setInfoWindowAdapter(new CustomInfoWindowAdapter(LayoutInflater.from(getContext())));
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.setOnInfoWindowClickListener(mClusterManager);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG,"onRequestPermissionResult Method");
        if (requestCode == PermissionUtils.MY_LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length <= 0){
                Log.i(TAG, "User interaction was cancelled. Ops!");
                //Toast.makeText(this, R.string.user_interaction_cancelled, Toast.LENGTH_SHORT).show();

            }
            else if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i(TAG, "Permission Location Granted. Enabling location functionalites...");
                setLocationFunctionsEnabled();
            }
            else {
                Log.i(TAG, "Permission Location denied. Starting Denied Dialog.");
                PermissionUtils.PermissionDeniedDialogLocation.newInstance(false).show(getActivity().getSupportFragmentManager(), "deniedDialogLocation");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResultMap");
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "onActivityResultMap User agreed to make required location settings changes");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i(TAG, "onActivityResultMap User chose not to make required location setting changes");
                    mRequestingLocationUpdates = false;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void setMapView() {
        String s = mPrefs.getString("pref_map_view", "normal");

        int i;
        switch (s) {
            case "satellite":
                i = GoogleMap.MAP_TYPE_SATELLITE;
                break;
            case "hybrid":
                i = GoogleMap.MAP_TYPE_HYBRID;
                break;
            case "terrain":
                i = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            default:
                i = GoogleMap.MAP_TYPE_NORMAL;
                break;
        }

        mMap.setMapType(i);
    }

    public void setMapStyle() {
        String s = mPrefs.getString("pref_map_style", "default");

        int i;
        switch (s){
            case "dark":
                i = R.raw.style_map_dark;
                break;
            default:
                i = R.raw.style_map_default;
                break;
        }

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), i));
    }

    private boolean isValid(HealthSite hs) {
            if(((!mPrefs.getBoolean("vegan_filter", false) || hs.isVegan() == mPrefs.getBoolean("vegan_filter", false)) &&
                    (!mPrefs.getBoolean("vegetarian_filter", false) || hs.isVegetarian() == mPrefs.getBoolean("vegetarian_filter", false)) &&
                    (!mPrefs.getBoolean("ecologic_filter", false) || hs.isEcologic() == mPrefs.getBoolean("ecologic_filter", false)) &&
                    (!mPrefs.getBoolean("free_gluten_filter", false) || hs.isFreeGluten() == mPrefs.getBoolean("free_gluten_filter", false)) &&
                    (!mPrefs.getBoolean("free_lactose_filter", false) || hs.isFreeLactose() == mPrefs.getBoolean("free_lactose_filter", false)) &&
                    (!mPrefs.getBoolean("restaurant_filter", false) || hs.isRestaurant() == mPrefs.getBoolean("restaurant_filter", false)) &&
                    (!mPrefs.getBoolean("store_filter", false) || hs.isStore() == mPrefs.getBoolean("store_filter", false)) &&
                    (!mPrefs.getBoolean("verified_filter", false) || hs.isVerified() == mPrefs.getBoolean("verified_filter", false))) ||
                    (allCategoryFalse() && allPropertyFalse())) {
                return true;
        } else {
                return false;
        }
    }

    private boolean allPropertyFalse() {
        return !mPrefs.getBoolean("vegan_filter", false) &&
                !mPrefs.getBoolean("vegetarian_filter", false) &&
                !mPrefs.getBoolean("ecologic_filter", false) &&
                !mPrefs.getBoolean("free_gluten_filter", false) &&
                !mPrefs.getBoolean("free_lactose_filter", false) &&
                !mPrefs.getBoolean("verified_filter", false);

    }

    private boolean allCategoryFalse() {
        return !mPrefs.getBoolean("restaurant_filter", false) &&
                !mPrefs.getBoolean("store_filter", false);
    }
}
