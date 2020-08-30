package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.Extras;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class UsersProfile extends AppCompatActivity {
    CircleImageView profile;
    TextView name,discription,onlineStatus;
    Button response,message;
    RecyclerView recyclerView;
    Dialog mydailog;
    String userID,username,userImage,online_status,id,userDiscription;
    DatabaseReference databaseReference;
    Map map;
    String uid;
    Button accept,reject;
    TextView tittle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);
        profile=findViewById(R.id.profile);
        name=findViewById(R.id.name);
        discription=findViewById(R.id.discription);
        response=findViewById(R.id.response);
        message=findViewById(R.id.message);
        recyclerView=findViewById(R.id.posts);
        onlineStatus=findViewById(R.id.onlineStatus);
        mydailog=new Dialog(this);
        mydailog.setCancelable(false);
        userID=getIntent().getExtras().getString("userID");
        username=getIntent().getExtras().getString("userName");
        userImage=getIntent().getExtras().getString("userImage");
        online_status=getIntent().getExtras().getString("OnlineStatus");
        id=getIntent().getExtras().getString("ID");
        userDiscription=getIntent().getExtras().getString("Discription");
        setData();
        response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (response.getText().equals("Add Friend"))
                {
                    sentRequest();

                }
                else if (response.getText().equals("Response"))
                {
                    responseRequest();

                }
                else
                if (response.getText().equals("Friends"))
                {
                   unFriend();

                }
                Toast.makeText(UsersProfile.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UsersProfile.this, MessageActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("userName",username);
                intent.putExtra("userImage",userImage);
                intent.putExtra("OnlineStatus",online_status);
                startActivity(intent);
            }
        });

    }

    private void unFriend() {


        mydailog.setContentView(R.layout.custom_popup_friendrequest);

        accept=mydailog.findViewById(R.id.accept);
        reject=mydailog.findViewById(R.id.reject);
        tittle=mydailog.findViewById(R.id.tittle);
        tittle.setText("Want to UnFriend");
        accept.setText("Unfriend");
        reject.setText("Cancel");
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydailog.dismiss();
                removeFriend();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydailog.dismiss();
                Toast.makeText(UsersProfile.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        mydailog.show();
    }

    private void responseRequest() {

        mydailog.setContentView(R.layout.custom_popup_friendrequest);
        accept=mydailog.findViewById(R.id.accept);
        reject=mydailog.findViewById(R.id.reject);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptFriendRequests();
                mydailog.dismiss();

            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriendRequestData();
                mydailog.dismiss();
            }
        });
        mydailog.show();

    }

    private void setData()
    {
        Glide.with(this).load(userImage).into(profile);
        discription.setText(userDiscription);
        name.setText(username);
        if (online_status.equals("online")) {
            onlineStatus.setText(online_status);

        }
        else {
            onlineStatus.setText( Extras.calculateOnlineStatus(online_status,"active"));
        }

        if (id.equals("1"))
        {

            response.setText("Add Friend");
        }
        else if (id.equals("2"))
        {
            response.setText("Response");
        }
        else if (id.equals("3"))
        {
            response.setText("Friends");
        }

    }
    private void sentRequest()
    {
        uid= FirebaseAuth.getInstance().getUid();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                .getReference("Friend Requests")
                .child(userID).child(uid);
        Map<String, Object> req=new HashMap<>();
        req.put("Request",true);
        req.put("id",uid);
        req.put("name","...");
        databaseReference.setValue(req).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    response.setText("sent");
                    Log.e("Sent Successful", "Done ");

                }
            }
        });
    }
    private void deleteFriendRequestData()
    {
        uid= FirebaseAuth.getInstance().getUid();
        databaseReference= FirebaseDatabase.getInstance()
                .getReference("Friend Requests")
                .child(uid).child(userID);
        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Log.d("FriendRequestsAdapter ","Delete Friend Requests");

                }
            }
        });
    }
    private void acceptFriendRequests()
    {
        uid= FirebaseAuth.getInstance().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference("Friends").
                child(uid).child(userID);
        map=new HashMap<>();
        map.put("friend",true);
        databaseReference.setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            databaseReference=FirebaseDatabase.getInstance().getReference("Friends").
                                    child(userID).child(uid);
                            databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    deleteFriendRequestData();
                                }
                            });

                        }

                    }
                });
    }
    private  void removeFriend()
    {

        uid= FirebaseAuth.getInstance().getUid();
            databaseReference= FirebaseDatabase.getInstance()
                    .getReference("Friends")
                    .child(uid).child(userID);
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        databaseReference= FirebaseDatabase.getInstance()
                                .getReference("Friends")
                                .child(userID).child(userID);
                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(UsersProfile.this, "UnFriend", Toast.LENGTH_SHORT).show();
                                    accept.setText("Add Friend");
                                }
                            }
                        });

                    }
                }
            });

    }
}
