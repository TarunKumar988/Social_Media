package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.social_media.Adapters.ChatAdapter;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Model.Chat;
import com.example.social_media.Model.ChatLists;
import com.example.social_media.Model.Users;
import com.example.social_media.notification.APIService;
import com.example.social_media.notification.Client;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity
implements SwipeRefreshLayout.OnRefreshListener
{
    CircleImageView profile, userProfile;
    TextView username;
    RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String name, image, currentUID;
    ArrayList<ChatLists> chatLists;
    ArrayList<Users> usersList,chatUsers;
    ArrayList<Chat> messagesList;
    RecyclerViewOnClickListener listener;
    TextView noChats;
    DatabaseReference usersRef,chatListsRef;

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout progressbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sharedPreferences = getSharedPreferences("SIGN_IN", MODE_PRIVATE);
        name = sharedPreferences.getString("Name", null);
        image = sharedPreferences.getString("Image", null);
        noChats=findViewById(R.id.noChat);
        profile = findViewById(R.id.profile);
        // userProfile=findViewById(R.id.usersProfile);
        username = findViewById(R.id.name);
        recyclerView = findViewById(R.id.recyclerview);
        progressbar=findViewById(R.id.progress_bar);
        swipeRefreshLayout=findViewById(R.id.swiper);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        chatLists = new ArrayList<>();
        usersList=new ArrayList<>();
        messagesList=new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUsers(currentUID);


        Glide.with(this).load(image).into(profile);
        username.setText(name);
         setOnCLickListner();
         swipeRefreshLayout.post(new Runnable() {
             @Override
             public void run() {
                 swipeRefreshLayout.setRefreshing(true);
                 getUsers(currentUID);
             }
         });


    }

    private void setOnCLickListner() {
        listener = new RecyclerViewOnClickListener() {
            @Override
            public void onCLick(View view, int position) {
//                Toast.makeText(ChatActivity.this, "" + chatLists.get(position) + "  " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                intent.putExtra("userID",chatUsers.get(position).getId());
//                intent.putExtra("userName",chatUsers.get(position).getName());
//                intent.putExtra("userImage",chatUsers.get(position).getImageURL());
//                intent.putExtra("OnlineStatus",chatUsers.get(position).getStatus());

                startActivity(intent);
            }
        };
    }

    private void getChatList(String currentUID) {


        chatListsRef = FirebaseDatabase.getInstance().getReference("/ChatsList").child(currentUID);
        chatListsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              chatLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatLists chats = dataSnapshot.getValue(ChatLists.class);
                    chatLists.add(chats);
                }
                if (chatLists.size()==0)
                {
                    noChats.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressbar.setVisibility(View.GONE);
                }
                else {
                    getChats();
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
            }
        });
    }

    private void getUsers(final String currentUID)
    {
        progressbar.setVisibility(View.VISIBLE);
        usersRef=FirebaseDatabase.getInstance().getReference("/Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               usersList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    if (users.getId().equals(currentUID)) {

                    }else{
                        usersList.add(users);
                    }

                }
                getChatList(currentUID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
            }
        });

    }
    private void getChats()
    {
        chatUsers=new ArrayList<>();
        chatUsers.clear();
        for (int i=0;i<usersList.size();i++)
        {
            for (int j=0;j<chatLists.size();j++)
            {
                if (usersList.get(i).getId().equals(chatLists.get(j).getId()))
                {
                    chatUsers.add(usersList.get(i));
                }
            }
        }
        getChatMessages();



    }
    private void getChatMessages()
    {
        chatListsRef=FirebaseDatabase.getInstance().getReference("/Chats");
        chatListsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Chat chat=dataSnapshot.getValue(Chat.class);
                        messagesList.add(chat);
                    }
                recyclerView.setAdapter(new ChatAdapter(ChatActivity.this, listener, chatUsers,messagesList));

                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        getUsers(currentUID);
    }
}
