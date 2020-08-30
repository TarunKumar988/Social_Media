package com.example.social_media;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_media.Adapters.StatusAdapter;
import com.example.social_media.Extras.OnlineStatusReciever;
import com.example.social_media.Extras.VideoPlayerRecyclerView;
import com.example.social_media.Model.Posts;
import com.example.social_media.Model.Users;
import com.example.social_media.notification.Token;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.items.ListItem;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,SwipeRefreshLayout.OnRefreshListener
{
    VideoPlayerRecyclerView recyclerView;
    StatusAdapter statusAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    CircleImageView profile;
    TextView userName,userEmail;
    OnlineStatusReciever onlineStatusReciever;

   static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> list;
    static String imageUrl,name,email,uid;
    LinearLayout progressBar;
    Toolbar toolbar;
    LinearLayoutManager linearLayoutManager;
    DrawerLayout drawerLayout;
    ProgressBar progressBar1;
    ArrayList<Posts> postsLists;
    ArrayList<Users> usersLists=new ArrayList<>();
    private ItemsPositionGetter mItemsPositionGetter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);



        toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar1=findViewById(R.id.progress_bar);


        swipeRefreshLayout=findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        progressBar=findViewById(R.id.progressParent);

        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView=findViewById(R.id.navigation_view);
        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView=findViewById(R.id.recyclerview);

        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header, null);
      navigationView.addHeaderView(header);

        profile = header.findViewById(R.id.image);
        userName = header.findViewById(R.id.name);
       userEmail=header.findViewById(R.id.email);

        onlineStatusReciever=new OnlineStatusReciever();
        IntentFilter intentFilter=new IntentFilter();

        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");

        registerReceiver(onlineStatusReciever,intentFilter);

if (FirebaseAuth.getInstance().getCurrentUser()!=null) {
    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    FirebaseDatabase.getInstance().getReference("/Users")
            .child(uid).child("status").setValue("online");

//      imageUrl=getIntent().getExtras().getString("image");
//      name=getIntent().getExtras().getString("name");


    list = new ArrayList<>();
    list.add("School");
    list.add("Study");
    list.add("Enjoying");
    list.add("Music");
    list.add("Gaming");
    list.add("M416");
    list.add("AWM");
    linearLayoutManager=new LinearLayoutManager(this);
    linearLayoutManager.setReverseLayout(true);
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);

    getUsersData(uid);
    swipeRefreshLayout.post(new Runnable() {
        @Override
        public void run() {
            swipeRefreshLayout.setRefreshing(true);
            getUsersData(uid);
        }
    });


    updateToken(FirebaseInstanceId.getInstance().getToken());

    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_USER", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("UID", uid);
    editor.apply();


}
    }

    public void updateToken(String token)
    {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token t=new Token(token);
        reference.child(uid).setValue(t);
    }

    public void getUsersData(final String uid)
    {
        progressBar.setVisibility(View.VISIBLE);

        usersLists=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("/Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersLists.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);

                    if (users.getId().equals(uid))
                    {
                        setProgressBarIndeterminateVisibility(false);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        imageUrl=users.getImageURL();
                        name=users.getName();
                        Glide.with(getApplicationContext()).load(imageUrl).into(profile);
                        userName.setText(name);
                        userEmail.setText(users.getEmail());
                        setSharedPreferences(name,imageUrl);
//                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity2.this));
                        //statusAdapter=new StatusAdapter(MainActivity2.this,list,imageUrl);
                        //recyclerView.setAdapter(statusAdapter);
                    }
                    else
                    {
                        usersLists.add(users);
                    }
                }
                getposts();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                swipeRefreshLayout.setRefreshing(false);
            }


        });

    }

    private void setSharedPreferences(String name,String imageUrl)
    {
        sharedPreferences=this.getSharedPreferences("SIGN_IN",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("Name",name);
        editor.putString("Image",imageUrl);
        editor.apply();



    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }


    }


    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.first: {
                Intent intent=new Intent(getApplicationContext(),Profile_Activity.class);
                intent.putExtra("Name",name);
                intent.putExtra("Image",imageUrl);
                startActivity(intent);
                break;
            }
            case  R.id.second:
            {
                Toast.makeText(this, "Second", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(MainActivity2.this,Friend_Activty.class);
                startActivity(intent);
                break;
            }
            case R.id.third:
            {
                Intent intent=new Intent(MainActivity2.this,ChatActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.uploadPost:
            {
                Intent intent=new Intent(MainActivity2.this,PostActivity.class);
                startActivity(intent);

                 break;
            }
            case R.id.myItem:
        {
            Toast.makeText(this, "My Title", Toast.LENGTH_SHORT).show();

            break;
        }
            case R.id.logout:
            {
                Logout();
                  break;
            }





        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout()
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("/Users")
                .child(uid).child("status").setValue(currentTime+" "+currentDate);

        LoginManager.getInstance().logOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();
        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getApplicationContext(),gso);
        googleSignInClient.signOut();

        Intent intent=new Intent(getApplicationContext(),Login_Activity.class);
        startActivity(intent);
        FirebaseAuth.getInstance().signOut();

        sharedPreferences=this.getSharedPreferences("SIGN_IN",MODE_PRIVATE);

        editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        finish();
    }



    @Override
    public void onRefresh() {
        list.add("MyOwn");
        list.add("Virtual");
        list.add("Mobile");
        list.add("Rpbort");

        getUsersData(uid);

    }

    private void getposts()
    {
        postsLists=new ArrayList<>();
        DatabaseReference postsRef=FirebaseDatabase.getInstance().getReference("Posts");
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsLists.clear();
               for (DataSnapshot dataSnapshot:snapshot.getChildren())
               {
                   Posts posts=dataSnapshot.getValue(Posts.class);
                    if (!posts.getId().equals(uid)) {

                            postsLists.add(posts);

                    }

               }
               recyclerView.setMediaObjects(postsLists);
               recyclerView.setAdapter(new StatusAdapter(MainActivity2.this,postsLists,usersLists,initGlide()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }


    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.loginbackground);

        return Glide.with(getApplicationContext())
                .setDefaultRequestOptions(options);
    }
    @Override
    protected void onDestroy() {
        if(recyclerView!=null)
            recyclerView.releasePlayer();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView!=null)
        {
            recyclerView.startPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recyclerView!=null)
        {
            recyclerView.pausePlayer();
        }

    }


}