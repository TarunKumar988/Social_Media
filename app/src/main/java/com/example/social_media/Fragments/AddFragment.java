package com.example.social_media.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.social_media.Adapters.AddFriendAdapter;
import com.example.social_media.Extras.Extras;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.MessageActivity;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.example.social_media.UsersProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddFragment extends Fragment
implements SwipeRefreshLayout.OnRefreshListener
{
   RecyclerView recyclerView;
   TextView noUsers;

    ArrayList<Users> usersList;
     ArrayList<String> friends_list;
    ArrayList<String> friendRequests=new ArrayList<>();
     ArrayList<Users> addFriend=new ArrayList<>();

     FirebaseUser firebaseUser;
    String currentUserName;
    String uid;
    ArrayList<Users> tempadd;
   DatabaseReference databaseReference;
   RecyclerViewOnClickListener listener;
   SwipeRefreshLayout swipeRefreshLayout;
   LinearLayout progressbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_add, container, false);

        noUsers=view.findViewById(R.id.noUsers);
        swipeRefreshLayout=view.findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        progressbar=view.findViewById(R.id.progressParent);

        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        setOnClickListener();
        getUsers();

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getUsers();
            }
        });





        return view ;

    }
  void getUsers() {
            progressbar.setVisibility(View.VISIBLE);
        usersList=new ArrayList<>();

      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("onDataChange", "Called" + snapshot.toString());
                    usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Users users = dataSnapshot.getValue(Users.class);
                    if (users.getId().equals(firebaseUser.getUid())) {
                        currentUserName = users.getName();
                    } else {
                        usersList.add(users);
                          }
                }
                getFriends();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("onCancelled", "Called");
                progressbar.setVisibility(View.GONE);
            }
        });
//        Log.e("USERS LIsT", ""+usersList);

    }


    void getFriends()
    {
        friends_list=new ArrayList<>();

        uid=  FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("Friends")
                .child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friends_list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    if (dataSnapshot.exists())
                    {
                        String a= dataSnapshot.getKey();
                        friends_list.add(a);
                    }
                }
                getFriendRequests(usersList,friends_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressbar.setVisibility(View.GONE);
            }
        });

    }
   void getFriendRequests(final ArrayList<Users> users, ArrayList<String> friends)
    {
        addFriend=users;
        Log.e("Friend users size", users.size()+"  "+friends.size());
        for (int i=0;i<friends.size();i++)
        {
            for (int j=0;j<users.size();j++)
            {
                if (users.get(j).getId().equals(friends.get(i)))
                {
                    addFriend.remove(users.get(j));
                }
            }
        }

        Log.e("Add Friend",""+addFriend.size());
        databaseReference=FirebaseDatabase.getInstance().getReference("Friend Requests").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              friendRequests.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        friendRequests.add(dataSnapshot.getKey());
                        Log.e("Friend Requests ",""+friendRequests);
                    }

                }
           tempadd =addFriend;

                for (int i=0;i<tempadd.size();i++)
                {
                    for (int j=0;j<friendRequests.size();j++)
                    {
//                        Log.e("AddFriend ID",""+tempadd.get(i).getId());
//                        Log.e("Friend Request",""+friendRequests.get(j));
                        String addfrd=tempadd.get(i).getId();
                        String frdReq=friendRequests.get(j);
                        if (addfrd.equals(frdReq))
                        {
                                addFriend.remove(tempadd.get(i));
                        }

                    }

                }
                if (addFriend.size()==0)
                {
                    noUsers.setVisibility(View.VISIBLE);
                }
                else {
                    noUsers.setVisibility(View.GONE);

                    AddFriendAdapter addFriendAdapter = new AddFriendAdapter(getContext(), addFriend, currentUserName,listener);
                    recyclerView.setAdapter(addFriendAdapter);
                    swipeRefreshLayout.setRefreshing(false);

                }
                progressbar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
            }
        });

    }
    private void setOnClickListener()
    {
        listener=new RecyclerViewOnClickListener() {
            @Override
            public void onCLick(View view, int position) {
                Intent intent=new Intent(getActivity(), UsersProfile.class);
                intent.putExtra("userID",addFriend.get(position).getId());
                intent.putExtra("userName",addFriend.get(position).getName());
                intent.putExtra("userImage",addFriend.get(position).getImageURL());
                intent.putExtra("OnlineStatus",addFriend.get(position).getStatus());
                intent.putExtra("ID","1");
                intent.putExtra("Discription",addFriend.get(position).getDiscription());
                startActivity(intent);
            }
        };
    }

    @Override
    public void onRefresh() {
        getUsers();
    }
}
