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
import android.widget.Toast;

import com.example.social_media.Adapters.FriendRequestAdapter;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Model.Requests;

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

public class FirendRequestFragment extends Fragment
implements  SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    TextView noRequests;
FriendRequestAdapter friendRequestAdapter;
ArrayList<Requests> arrayList;
ArrayList<Users> usersList;
    ArrayList<Users> templist;
DatabaseReference databaseReference;
RecyclerViewOnClickListener listener;
SwipeRefreshLayout swipeRefreshLayout;
LinearLayout progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view=inflater.inflate(R.layout.fragment_firend_request, container, false);
        noRequests=view.findViewById(R.id.noRequest);
        recyclerView=view.findViewById(R.id.recyclerview);
        progressBar=view.findViewById(R.id.progressParent);
        swipeRefreshLayout =view.findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersList=new ArrayList();
        templist=new ArrayList<>();
        setOnClickListener();
        getUsers();
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getUsers();
            }
        });

        return view;
    }
   public void data()
    {

        arrayList=new ArrayList();
        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend Requests").child(uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    arrayList.clear();

                    for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {


                        Requests requests=dataSnapshot.getValue(Requests.class);
                        arrayList.add(requests);
                    }
                    templist.clear();
                    for (int i=0;i<usersList.size();i++)
                    {
                        for (int j=0;j<arrayList.size();j++)
                        {
//                        Log.e("AddFriend ID",""+tempadd.get(i).getId());
//                        Log.e("Friend Request",""+friendRequests.get(j));
                            String addfrd=usersList.get(i).getId();
                            String frdReq=arrayList.get(j).getId();
                            if (addfrd.equals(frdReq))
                            {
                                templist.add(usersList.get(i));
                            }

                        }

                    }

                        friendRequestAdapter = new FriendRequestAdapter(getContext(), templist, uid,listener);
                        recyclerView.setAdapter(friendRequestAdapter);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                }
                else
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noRequests.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    void getUsers()
    {

        progressBar.setVisibility(View.VISIBLE);
        final String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference= FirebaseDatabase.getInstance().getReference("/Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    usersList.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()) {

                        if (dataSnapshot.exists()) {
                            Users users = dataSnapshot.getValue(Users.class);
                            if (!uid.equals(users.getId())) {
                                usersList.add(users);
                            }
                        } else {
                            Toast.makeText(getContext(), "No users", Toast.LENGTH_SHORT).show();
                        }
                    }
                data();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e("ONCancell",""+error);
            }


        });

    }
    private void setOnClickListener()
    {
        listener=new RecyclerViewOnClickListener() {
            @Override
            public void onCLick(View view, int position) {
                Intent intent=new Intent(getActivity(), UsersProfile.class);
                intent.putExtra("userID",templist.get(position).getId());
                intent.putExtra("userName",templist.get(position).getName());
                intent.putExtra("userImage",templist.get(position).getImageURL());
                intent.putExtra("OnlineStatus",templist.get(position).getStatus());
                intent.putExtra("ID","2");
                intent.putExtra("Discription",templist.get(position).getDiscription());

                startActivity(intent);
            }
        };
    }

    @Override
    public void onRefresh() {
        getUsers();

    }
}
