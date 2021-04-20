package albertperez.healthsites;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

public class DialogSelectPlacePicker extends DialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private String address;
    private String countryCode;
    private String country;
    private String adminArea;
    private String city;
    private String postalCode;
    private String street;
    private String number;
    private SupportMapFragment mapFragment;

    public DialogSelectPlacePicker(Double latitude, Double longitude, String address,
                                   String countryCode, String country, String adminArea, String city, String postalCode,
                                   String street, String number) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.countryCode = countryCode;
        this.country = country;
        this.adminArea = adminArea;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.number = number;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_place_picker, null);

        mapFragment = (SupportMapFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.mapp);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        view.findViewById(R.id.Select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), address,Toast.LENGTH_LONG).show();
                Intent data = new Intent();
                data.putExtra("latitude", latitude);
                data.putExtra("longitude", longitude);
                data.putExtra("address", address);
                data.putExtra("countryCode", countryCode);
                data.putExtra("country", country);
                data.putExtra("adminArea", adminArea);
                data.putExtra("city", city);
                data.putExtra("postalCode", postalCode);
                data.putExtra("street", street);
                data.putExtra("number", number);
                requireActivity().setResult(Activity.RESULT_OK, data);
                requireActivity().finish();
            }
        });

        view.findViewById(R.id.Change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
                dismiss();
            }
        });

        builder.setView(view)
                .setTitle(R.string.use_this_location)
                .setMessage(address)
                .setIcon(R.drawable.logo_healthsites);

        return builder.create();
    }

    @Override
    public void onCancel(@NotNull DialogInterface dialog) {
        super.onCancel(dialog);
        requireActivity().getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
        dismiss();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_map_default));

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerOptions.position(new LatLng(latitude, longitude));

        markerOptions.title(address);
        mMap.clear();
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 16f);
        mMap.animateCamera(location);
        mMap.addMarker(markerOptions);
    }
}