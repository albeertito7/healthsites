package albertperez.healthsites.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import albertperez.healthsites.Post;
import albertperez.healthsites.R;

public class PostsHealthSiteProfileFragment extends Fragment {

    private String healthSiteId;
    private String name;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public PostsHealthSiteProfileFragment(String healthSiteId, String name) {
        this.healthSiteId = healthSiteId;
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts_healthsite_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.my_recycler_view_healthsite_profile);
        setPostsList(healthSiteId);
    }

    private void setPostsList(String healthSiteId) {
        Query query = FirebaseFirestore.getInstance().collection("Post").whereEqualTo("healthSiteId", healthSiteId).orderBy("timestamp", Query.Direction.DESCENDING).limit(50);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        mAdapter = new FirestoreRecyclerAdapter<Post, PostsHealthSiteProfileFragment.PostsViewHolder>(options) {
            @NonNull
            @Override
            public PostsHealthSiteProfileFragment.PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_posts, parent, false);
                return new PostsHealthSiteProfileFragment.PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final PostsHealthSiteProfileFragment.PostsViewHolder holder, int position, @NonNull final Post model) {
                FirebaseStorage.getInstance().getReference("profileImages").child(model.getUserId() + ".jpeg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(requireActivity()).load(uri).into(holder.imageView);
                        holder.cardView.setVisibility(View.VISIBLE);
                    }
                });
                FirebaseFirestore.getInstance().collection("Users").document(model.getUserId()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressWarnings("deprecation")
                            @SuppressLint("SimpleDateFormat")
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    holder.textView.setText(task.getResult().get("username").toString());
                                    holder.text.setText(model.getText());
                                    //holder.timeStamp.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date(Long.parseLong(model.getTimestamp()))));
                                }
                            }
                        });

                holder.setLikeButtonStatus(FirebaseFirestore.getInstance().collection("Likes").document(model.getId()));
                holder.btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.likeCheker) {
                            FirebaseFirestore.getInstance().collection("Likes").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                        FirebaseFirestore.getInstance().collection("Likes").document(model.getId())
                                                .update(FirebaseAuth.getInstance().getCurrentUser().getUid(), FieldValue.delete());
                                        holder.likeCheker = false;
                                    }
                                }
                            });
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                            FirebaseFirestore.getInstance().collection("Likes").document(model.getId()).set(map, SetOptions.merge());
                            holder.likeCheker = true;
                        }
                    }
                });
            }
        };

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private static class PostsViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private TextView text;
        private ImageView imageView;
        private CardView cardView;
        private TextView timeStamp;
        private TextView likes;
        private ImageButton btnLike;
        private Integer likesCount;
        private Boolean likeCheker;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_name);
            text = itemView.findViewById(R.id.list_text);
            imageView = itemView.findViewById(R.id.list_image);
            cardView = itemView.findViewById(R.id.list_cardview_image);
            //timeStamp = itemView.findViewById(R.id.list_timestamp);
            likes = itemView.findViewById(R.id.list_likes);
            btnLike = itemView.findViewById(R.id.list_btn_like);
        }

        public void setLikeButtonStatus(DocumentReference reference) {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String userId = user.getUid();

            reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value != null) {
                        if(value.contains(userId)) {
                            likesCount = Objects.requireNonNull(value.getData()).size();
                            btnLike.setImageResource(R.drawable.icon_filled_like);
                            likes.setText(Integer.toString(likesCount) + " likes");
                            likeCheker = true;
                        } else {
                            if(value.getData()!= null) {
                                likesCount = value.getData().size();
                            } else {
                                likesCount = 0;
                            }
                            btnLike.setImageResource(R.drawable.icon_outline_like);
                            likes.setText(Integer.toString(likesCount) + " likes");
                            likeCheker = false;
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}
