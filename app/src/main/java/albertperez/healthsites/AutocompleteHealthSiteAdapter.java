package albertperez.healthsites;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AutocompleteHealthSiteAdapter extends ArrayAdapter<HealthSite> {

    private List<HealthSite> list;

    public AutocompleteHealthSiteAdapter(@NonNull Context context, @NonNull List<HealthSite> list) {
        super(context, 0, list);
        this.list = new ArrayList<>(list);
    }

    public Filter getFilter() {
        return healthSiteFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_search_health_sites, parent, false);
        }

        TextView text = convertView.findViewById(R.id.searchable_name);
        final ImageView imageView = convertView.findViewById(R.id.search_healthiste_image);
        ImageView imageView1 = convertView.findViewById(R.id.search_verified);
        HealthSite hs = getItem(position);
        if(hs != null) {
            FirebaseStorage.getInstance().getReference("healthSitesImages").child(hs.getId() + ".jpeg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(uri).into(imageView);
                        }
                    });

            text.setText(hs.getName());

            if(hs.isVerified()) {
                imageView1.setVisibility(View.VISIBLE);
            } else {
                imageView1.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private Filter healthSiteFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            List<HealthSite> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                suggestions.addAll(list);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(HealthSite item: list) {
                    if(item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            filterResults.values = suggestions;
            filterResults.count = suggestions.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((HealthSite) resultValue).getName();
        }
    };
}
