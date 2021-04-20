package albertperez.healthsites;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import albertperez.healthsites.fragments.ContactHealthSiteProfileFragment;
import albertperez.healthsites.fragments.DialogPost;
import albertperez.healthsites.fragments.InfoHealthSiteProfileFragment;
import albertperez.healthsites.fragments.PostsHealthSiteProfileFragment;

public class HealthSiteProfileActivity extends MainActivity {

    private RatingBar ratingBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private InfoHealthSiteProfileFragment infoFragment;
    private ContactHealthSiteProfileFragment contactFragment;
    private PostsHealthSiteProfileFragment postsFragment;

    private DocumentReference reference;
    private Boolean followChecker;
    private Integer followCounts;
    private Button btnFollow;
    private TextView followCounter;

    private Query query;
    private Integer postCounts;
    private TextView postCounter;

    private HealthSite healthSite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthsite_profile);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        healthSite = (HealthSite) bundle.get("healthsite");

        assert healthSite != null;
        Toolbar toolbar;
        if(healthSite.isVerified()) {
            toolbar = findViewById(R.id.profile_healthsite_toolbar_verified);
        } else {
            toolbar = findViewById(R.id.profile_healthsite_toolbar);
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(healthSite.getTitle());
        toolbar.setVisibility(View.VISIBLE);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
            }
        });

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        FirebaseStorage.getInstance().getReference("healthSitesImages").child(healthSite.getId() + ".jpeg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).into((ImageView) findViewById(R.id.profile_healthiste_image));
                    }
                });

        //((TextView) findViewById(R.id.healthsite_profile_name)).setText(healthSite.getName());
        ((TextView) findViewById(R.id.profile_healthsite_description)).setText(healthSite.getDescription());
        if(healthSite.isVerified()) {
            //findViewById(R.id.healthsite_profile_verified).setVisibility(View.VISIBLE);
        }

        reference = FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(healthSite.getId());
        setFollowButtonStatus(reference);

        query = FirebaseFirestore.getInstance().collection("Post").whereEqualTo("healthSiteId", healthSite.getId());
        setPostsNumberStatus(query);

        infoFragment = new InfoHealthSiteProfileFragment(healthSite.isVegan(), healthSite.isVegetarian(), healthSite.isEcologic(), healthSite.isFreeGluten(), healthSite.isFreeLactose(), healthSite.isRestaurant(), healthSite.isStore());
        contactFragment = new ContactHealthSiteProfileFragment(healthSite.getAddress(),healthSite.getWebsite(), healthSite.getWebMail(), healthSite.getPhoneNumber());
        postsFragment = new PostsHealthSiteProfileFragment(healthSite.getId(), healthSite.getName());

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(infoFragment, getString(R.string.properties));
        viewPagerAdapter.addFragment(contactFragment, getString(R.string.contact));
        viewPagerAdapter.addFragment(postsFragment, getString(R.string.posts));
        viewPager.setAdapter(viewPagerAdapter);

        /*ratingBar = findViewById(R.id.healthsite_profile_rating_bar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float rating, boolean fromUser) {
                FirebaseFirestore.getInstance().collection("HealthSites").whereEqualTo("companyCIF", healthSite.getCompanyCIF()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            FirebaseFirestore.getInstance().collection("HealthSites").document(Objects.requireNonNull(task.getResult()).getDocuments().get(0).getId()).update("rating", rating);
                        }
                    }
                });
            }
        });*/

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(followChecker) {
                    FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(healthSite.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.contains(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(healthSite.getId())
                                        .update(FirebaseAuth.getInstance().getCurrentUser().getUid(), FieldValue.delete());
                                FirebaseFirestore.getInstance().collection("userFollowing").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .update(healthSite.getId(), FieldValue.delete());
                                followChecker = false;
                            }
                        }
                    });
                } else {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> map2 = new HashMap<>();
                    map.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), true);
                    map2.put(healthSite.getId(), true);
                    FirebaseFirestore.getInstance().collection("healthSiteFollowers").document(healthSite.getId()).set(map, SetOptions.merge());
                    FirebaseFirestore.getInstance().collection("userFollowing").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map2, SetOptions.merge());
                    followChecker = true;
                }
            }
        });

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("id", healthSite.getId());
                DialogPost dialogPost = new DialogPost();
                dialogPost.setArguments(args);
                dialogPost.show(getSupportFragmentManager(), "dialogPost");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.profile_healthsite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.options:
                Bundle bundle = new Bundle();
                bundle.putString("id", healthSite.getId());
                bundle.putString("name", healthSite.getName());
                bundle.putString("slogan", healthSite.getDescription());
                Intent intent = new Intent(this, HealthSiteOptionsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent, ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_from_right, R.anim.slide_out_to_left).toBundle());
                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }
        return true;
    }

    private void setFollowButtonStatus(DocumentReference reference) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();
        btnFollow = findViewById(R.id.btn_follow);
        followCounter = findViewById(R.id.healthsite_profile_follow_counter);

        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    if(value.contains(userId)) {
                        followCounts = Objects.requireNonNull(value.getData()).size();
                        btnFollow.setBackground(getDrawable(R.drawable.btn_outline_rounded));
                        btnFollow.setText(getString(R.string.following));
                        btnFollow.setTextColor(getResources().getColor(R.color.colorGreen));
                        followCounter.setText(Integer.toString(followCounts));
                        followChecker = true;
                    } else {
                        if(value.getData()!= null) {
                            followCounts = value.getData().size();
                        } else {
                            followCounts = 0;
                        }
                        btnFollow.setBackground(getDrawable(R.drawable.btn_rounded));
                        btnFollow.setText(getString(R.string.follow));
                        btnFollow.setTextColor(getResources().getColor(android.R.color.white));
                        followCounter.setText(Integer.toString(followCounts));
                        followChecker = false;
                    }
                }
            }
        });
    }

    private void setPostsNumberStatus(Query query) {

        postCounter = findViewById(R.id.healthsite_profile_post_counter);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    if(value.getDocuments().size() > 0) {
                        postCounts = value.getDocuments().size();
                        postCounter.setText(Integer.toString(postCounts));
                    } else {
                        postCounts = 0;
                        postCounter.setText(Integer.toString(postCounts));
                    }
                }
            }
        });
    }

    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentsTitles = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentsTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitles.get(position);
        }
    }
}
