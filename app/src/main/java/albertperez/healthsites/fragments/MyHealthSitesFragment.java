package albertperez.healthsites.fragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import albertperez.healthsites.HealthSite;
import albertperez.healthsites.HealthSiteProfileActivity;
import albertperez.healthsites.R;
import albertperez.healthsites.fragments.addHealthSite.DialogInfo;

public class MyHealthSitesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> arrayHealthSites;
    private List<HealthSite> healthSites = new ArrayList<>();
    private Boolean init = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_posts, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if(!mPrefs.getBoolean("myhealthsites_introduction_done", false)) {
            DialogInfo dialog = new DialogInfo(getString(R.string.myhealthsites), R.layout.custom_info_dialog_intro_to_myhealthsites);
            dialog.show(requireActivity().getSupportFragmentManager(), "dialogIntroMyHealthSites");
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean("myhealthsites_introduction_done", true);
            editor.apply();
        }

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.include_toolbar);
        toolbar.setTitle(getString(R.string.myhealthsites));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.my_recycler_view);

        FirebaseFirestore.getInstance().collection("userFollowing").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value != null && value.getData() != null) {
                            arrayHealthSites = new ArrayList<>();
                            arrayHealthSites.addAll(value.getData().keySet());
                            setMyHealthSitesList(arrayHealthSites);
                        }
                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.filters_menu, menu);
        MenuItem item = menu.findItem(R.id.searchable);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(healthSites != null && healthSites.size() > 0) {
                    List<String> array = new ArrayList<>();
                    if(!newText.equals("")) {
                        for (HealthSite hs : healthSites) {
                            if (hs.getName().toLowerCase().contains(newText.toLowerCase()) && !array.contains(hs.getId())) {
                                array.add(hs.getId());
                            }
                        }
                    } else {
                        array = arrayHealthSites;
                    }

                    if(array.size() > 0 && mAdapter != null) {
                        Query query = FirebaseFirestore.getInstance().collection("HealthSites").whereIn("id", array);
                        FirestoreRecyclerOptions<HealthSite> options = new FirestoreRecyclerOptions.Builder<HealthSite>()
                                .setQuery(query, HealthSite.class)
                                .build();

                        mAdapter.stopListening();
                        recyclerView.removeAllViewsInLayout();
                        mAdapter = new HealthSiteAdapter(options);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.startListening();
                    }
                }
                return true;
            }
        });
    }

    private void setMyHealthSitesList(List<String> array) {
        Query query = FirebaseFirestore.getInstance().collection("HealthSites").whereIn("id", array);
        FirestoreRecyclerOptions<HealthSite> options = new FirestoreRecyclerOptions.Builder<HealthSite>()
                .setQuery(query, HealthSite.class)
                .build();

        mAdapter = new HealthSiteAdapter(options);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }


    private class HealthSiteAdapter extends FirestoreRecyclerAdapter<HealthSite, HealthSiteViewHolder> {

        public HealthSiteAdapter(@NonNull FirestoreRecyclerOptions<HealthSite> options) {
            super(options);
        }

        @Override
        public void updateOptions(@NonNull FirestoreRecyclerOptions<HealthSite> options) {
            super.updateOptions(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull final HealthSiteViewHolder holder, int position, @NonNull final HealthSite model) {
            FirebaseStorage.getInstance().getReference().child("healthSitesImages").child(model.getId() + ".jpeg").getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(requireContext()).load(uri).into(holder.imageView);
                            holder.cardView.setVisibility(View.VISIBLE);
                        }
                    });

            holder.textView.setText(Objects.requireNonNull(model.getName()));
            if(model.isVerified()) holder.verified.setVisibility(View.VISIBLE);
            //holder.text.setText(model.getDescription());

            FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot != null && documentSnapshot.getData() != null) {
                        holder.followers.setText("Follorwers: " + documentSnapshot.getData().size());
                    }
                }
            });

            FirebaseFirestore.getInstance().collection("Post").whereEqualTo("healthSiteId", model.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value != null && value.getDocuments() != null) {
                        holder.posts.setText("Posts: " + value.getDocuments().size());
                    }
                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), HealthSiteProfileActivity.class);
                    intent.putExtra("healthsite", model);
                    startActivity(intent, ActivityOptions.makeCustomAnimation(getContext(), R.anim.slide_up, R.anim.fade_out).toBundle());
                }
            });

            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(model.getId())
                            .update(FirebaseAuth.getInstance().getCurrentUser().getUid(), FieldValue.delete());
                    FirebaseFirestore.getInstance().collection("userFollowing").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update(model.getId(), FieldValue.delete());
                }
            });

            holder.btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putString("id", model.getId());
                    DialogPost dialogPost = new DialogPost();
                    dialogPost.setArguments(args);
                    dialogPost.show(requireActivity().getSupportFragmentManager(), "dialogPost");
                }
            });

            if(healthSites.size() > 0) {
                Boolean bb = true;
                for(HealthSite hs: healthSites) {
                    if(hs.getId().equals(model.getId())) {
                        bb = false;
                        break;
                    }
                }

                if(bb) {
                    healthSites.add(model);
                }
            } else {
                healthSites.add(model);
            }
        }

        @NonNull
        @Override
        public HealthSiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_health_sites, parent, false);
            return new HealthSiteViewHolder(view);
        }
    }

    private static class HealthSiteViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView text;
        private TextView followers;
        private TextView posts;
        private ImageView imageView;
        private CardView cardView;
        private ImageView verified;
        private Button btnFollow;
        private Button btnPost;

        public HealthSiteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_name);
            text = itemView.findViewById(R.id.list_descripcio);
            followers = itemView.findViewById(R.id.list_followers);
            posts = itemView.findViewById(R.id.list_posts);
            imageView = itemView.findViewById(R.id.list_image);
            cardView = itemView.findViewById(R.id.list_material_card_view);
            verified = itemView.findViewById(R.id.list_verified);
            btnFollow = itemView.findViewById(R.id.list_btn_follow);
            btnPost = itemView.findViewById(R.id.list_btn_post);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if(mAdapter != null) {
            mAdapter.startListening();
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        /*if(mAdapter != null) {
            mAdapter.stopListening();
        }*/
    }
}
