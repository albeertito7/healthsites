package albertperez.healthsites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class CustomClusterRenderer extends DefaultClusterRenderer<ClusterItem> {

    private final Context mContext;
    private final IconGenerator mClusterIconGenerator;


    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterItem> clusterManager) {
        super(context, map, clusterManager);
        this.mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());
    }

    //before rendering a conventional marker
    @Override protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions) {

        if(((HealthSite) item).isVerified()) {
            Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker_healthsites);
            BitmapDescriptor markerIcon = getMarkerIconFromBitmap(b);
            markerOptions.icon(markerIcon);
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        markerOptions.snippet(item.getSnippet());
    }

    private BitmapDescriptor getMarkerIconFromBitmap(Bitmap b) {
        Bitmap bitmap = Bitmap.createScaledBitmap(b, 95, 135, false);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onClusterItemRendered(@NonNull ClusterItem clusterItem, @NonNull Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag((HealthSite) clusterItem);
    }

    @Override
    protected void onClusterRendered(@NonNull Cluster<ClusterItem> cluster, @NonNull Marker marker) {
        super.onClusterRendered(cluster, marker);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.background_circle);
        if(drawable != null) {
            //drawable.setAlpha(95);
            mClusterIconGenerator.setBackground(drawable);
            if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(50, 30, 50, 30);
            }
            else {
                mClusterIconGenerator.setContentPadding(70, 50, 70, 50);
            }
            //mClusterIconGenerator.setColor(R.color.colorGreen);
            mClusterIconGenerator.setTextAppearance(R.style.Theme_MyApp_WhiteTextAppearance);
            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    //invoked before rendering a cluster
    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<ClusterItem> cluster, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.background_circle);
        if(drawable != null) {
            //drawable.setAlpha(95);
            mClusterIconGenerator.setBackground(drawable);
            if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(50, 30, 50, 30);
            }
            else {
                mClusterIconGenerator.setContentPadding(70, 50, 70, 50);
            }
            //mClusterIconGenerator.setColor(R.color.colorGreen);
            mClusterIconGenerator.setTextAppearance(R.style.Theme_MyApp_WhiteTextAppearance);
            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
