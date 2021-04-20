package albertperez.healthsites;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final LayoutInflater mInflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
        final View popup = mInflater.inflate(R.layout.custom_info_window, null);

        //HealthSite hs = (HealthSite) marker.getTag();

        /*if(hs != null) {
            FirebaseFirestore.getInstance().collection("HealthSites").whereEqualTo("companyCIF", hs.getCompanyCIF()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                String uid = Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId();
                                FirebaseStorage.getInstance().getReference("healthSitesImages").child(uid + ".jpeg").getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(context).load(uri).into((ImageView) popup.findViewById(R.id.info_window_healthiste_image));
                                            }
                                        });
                            }
                        }
                    });
        }*/

        HealthSite hs = (HealthSite) marker.getTag();
        if(hs != null) {

            FirebaseStorage.getInstance().getReference("healthSitesImages").child(hs.getId() + ".jpeg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(popup.getContext()).load(uri).into((ImageView) popup.findViewById(R.id.info_image));
                        }
                    });

            /*FirebaseStorage.getInstance().getReference("healthSitesImages").child(hs.getId() + ".jpeg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(popup.getContext()).load(uri).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    popup.findViewById(R.id.info_cardview_image).setVisibility(View.VISIBLE);
                                    //((ImageView) popup.findViewById(R.id.info_image)).setImageDrawable(resource);
                                    return false;
                                }
                            }).into((ImageView) popup.findViewById(R.id.info_image));
                        }
                    });*/

            if(hs.isVerified()) {
                popup.findViewById(R.id.info_verified).setVisibility(View.VISIBLE);
            }

            if(hs.isRestaurant() && hs.isStore()){
                ((TextView)popup.findViewById(R.id.type_info_window)).setText(R.string.restaurant_and_store);
            } else {
                if(hs.isRestaurant()) {
                    ((TextView)popup.findViewById(R.id.type_info_window)).setText(R.string.restaurant);
                }

                if(hs.isStore()) {
                    ((TextView)popup.findViewById(R.id.type_info_window)).setText(R.string.store);
                }
            }

            ((TextView) popup.findViewById(R.id.title_info_window)).setText(hs.getName());
            //((TextView) popup.findViewById(R.id.address_info_window)).setText(hs.getAddress());
        }

        ((TextView) popup.findViewById(R.id.snippet_info_window)).setText(marker.getSnippet());

        return popup; //return null;
    }

    @Override public View getInfoContents(Marker marker) {
        final View popup = mInflater.inflate(R.layout.custom_info_window, null);

        //HealthSite hs = (HealthSite) marker.getTag();

        /*assert hs != null;
        FirebaseFirestore.getInstance().collection("HealthSites").whereEqualTo("companyCIF", hs.getCompanyCIF()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            String uid = Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId();
                            Glide.with(popup.findViewById(R.id.info_window_healthiste_image)).load(uid + ".jpeg").into((ImageView) popup.findViewById(R.id.info_window_healthiste_image));
                        }
                    }
                });*/
        ((TextView) popup.findViewById(R.id.title_info_window)).setText(marker.getTitle());
        ((TextView) popup.findViewById(R.id.snippet_info_window)).setText(marker.getTitle());

        return popup;
    }
}
