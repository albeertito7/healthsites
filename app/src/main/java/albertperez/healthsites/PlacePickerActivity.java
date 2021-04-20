package albertperez.healthsites;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import albertperez.healthsites.fragments.addHealthSite.DialogInfo;

public class PlacePickerActivity extends MainActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final long UPDATE_INTERVAL_IN_MILISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILISECONDS = UPDATE_INTERVAL_IN_MILISECONDS / 2;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private Boolean currentLocationEstablished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        Toolbar toolbar = findViewById(R.id.custom_toolbar_top);
        toolbar.setNavigationIcon(R.drawable.icon_close);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.place_picker);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(this, getResources().getString(R.string.google_maps_key));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        getMenuInflater().inflate(R.menu.placepicker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.autocomplete:
                // Set the fields to specify which types of place data to
                // return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.MY_LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setLocationActions();
                } else {
                    PermissionUtils.PermissionDeniedDialogLocation.newInstance(false).show(getSupportFragmentManager(), "deniedDialogLocation");
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(this, "ID: " + place.getId() + getString(R.string.address) + ":" + place.getAddress() + getString(R.string.name) + ":" + place.getName() + " " + getString(R.string.latlong) + ": " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                super.onActivityResult(requestCode, resultCode, data);
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_map_default));

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.MY_LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, false);
        } else {
            setLocationActions();
        }
        enableMap();
    }

    private void enableMap() {
        DialogInfo dialogPlacePicker = new DialogInfo("Place Picker", R.layout.custom_info_dialog_place_picker);
        dialogPlacePicker.show(getSupportFragmentManager(), "dialogPlacePicker");

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(getAddress(latLng));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mMap.clear();
                mMap.animateCamera(location);
                mMap.addMarker(markerOptions);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void setLocationActions() {
        mMap.setMyLocationEnabled(true);
        createLocationCallback();
        createLocationRequest();
    }

    private String getAddress(LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            String address = addresses.get(0).getAddressLine(0);
            String country = addresses.get(0).getCountryName();
            String countryCode = addresses.get(0).getCountryCode();
            String city = addresses.get(0).getLocality();
            String adminArea = addresses.get(0).getAdminArea();
            String postalCode = addresses.get(0).getPostalCode();
            String street = addresses.get(0).getThoroughfare();
            String number = addresses.get(0).getSubThoroughfare();

            new DialogSelectPlacePicker(latLng.latitude,
                    latLng.longitude, address, countryCode, country, adminArea,
                    city, postalCode, street, number)
                    .show(getSupportFragmentManager(), "dialogSelectPlacePicker");

            return address;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.check_your_connection, Toast.LENGTH_SHORT).show();
            return "No Address Found";
        }
    }

    private void createLocationCallback() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                updateLocationUI();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILISECONDS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                });
    }

    private void updateLocationUI(){
        if(mCurrentLocation != null && !currentLocationEstablished){
            currentLocationEstablished = true;
            LatLng position = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(position, 15);
            mMap.animateCamera(camera);
            mMap.moveCamera(camera);
        } else {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
}