package com.example.social_media.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.social_media.Adapters.FriendsAdapter;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.MessageActivity;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.example.social_media.UsersProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendsFragment extends Fragment
implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    TextView noFriend;
   static DatabaseReference databaseReference;
    ArrayList<Users> users_list,friend_users;
    ArrayList<String> friends_list;
    RecyclerViewOnClickListener listener;
    LinearLayout progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_firend, container, false);
        users_list=new ArrayList<>();
        friends_list=new ArrayList<>();
        friend_users=new ArrayList<>();
        noFriend=view.findViewById(R.id.noFriend);
        swipeRefreshLayout=view.findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        progressBar=view.findViewById(R.id.progressParent);
            recyclerView=view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setOnClickListener();
        getFriends();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getFriends();
            }
        });

        return view;
    }
    void  getUsers(final ArrayList<String> friends_list)
    {
         databaseReference=FirebaseDatabase.getInstance().getReference("/Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users_list.clear();
                friend_users.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                        users_list.add(users);

                }

                for (int i=0;i<friends_list.size();i++)
                {
                    for (int j=0;j<users_list.size();j++)
                    {
                        Boolean isFriend=friends_list.get(i).equals(users_list.get(j).getId());
                        if (isFriend)
                        {
                            friend_users.add(users_list.get(j));
                        }
                    }
                }




                    noFriend.setVisibility(View.INVISIBLE);
                    recyclerView.setAdapter(new FriendsAdapter(getContext(),listener, friend_users));
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void getFriends()
    {

        progressBar.setVisibility(View.VISIBLE);
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("/Friends")
                .child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends_list.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                        String a = dataSnapshot.getKey();
                        friends_list.add(a);

                    }
                    // Log.e("Ids ",firends_list.toString());
                    getUsers(friends_list);
                }
                else
                {
                    noFriend.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setOnClickListener()
    {
        listener=new RecyclerViewOnClickListener() {
            @Override
            public void onCLick(View view, int position) {
                Intent intent=new Intent(getActivity(), UsersProfile.class);
                intent.putExtra("userID",friend_users.get(position).getId());
                intent.putExtra("userName",friend_users.get(position).getName());
                intent.putExtra("userImage",friend_users.get(position).getImageURL());
                intent.putExtra("OnlineStatus",friend_users.get(position).getStatus());
                intent.putExtra("ID","3");
                intent.putExtra("Discription",friend_users.get(position).getDiscription());

                startActivity(intent);
            }
        };
    }

    @Override
    public void onRefresh() {
        getFriends();
    }
}
