package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.social_media.Adapters.MessageAdapter;
import com.example.social_media.Extras.Extras;
import com.example.social_media.Model.Chat;
import com.example.social_media.Model.Users;
import com.example.social_media.notification.APIService;
import com.example.social_media.notification.Client;
import com.example.social_media.notification.Data;
import com.example.social_media.notification.Response;
import com.example.social_media.notification.Sender;
import com.example.social_media.notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String image,name,userID,chatUser,userImage,currentUID;
    static String online_status;
    CircleImageView userProfile;
    TextView userName,onlineStatus;
    RecyclerView recyclerView;
    TextInputEditText type_Message;
    MaterialButton send;
    ArrayList<Chat> chatList;
    ArrayList<String> dateList;
    String TAG="MESSAGE ACTIVITY";

    DatabaseReference chatListref,chatListref1,chatRef,messsageRef;
    APIService apiService;
    boolean notify=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        userProfile=findViewById(R.id.profile);
        userName=findViewById(R.id.name);
        onlineStatus=findViewById(R.id.onlineStatus);
        onlineStatus.setVisibility(View.VISIBLE);
        recyclerView=findViewById(R.id.recyclerview);
        type_Message=findViewById(R.id.typeMessage);
        send=findViewById(R.id.send);
        chatList=new ArrayList();
        dateList=new ArrayList<>();

        userID=getIntent().getExtras().getString("userID");
//        chatUser=getIntent().getExtras().getString("userName");
//        userImage=getIntent().getExtras().getString("userImage");

        sharedPreferences=getSharedPreferences("SIGN_IN",MODE_PRIVATE);
        image=sharedPreferences.getString("Image",null);
        name=sharedPreferences.getString("Name",null);


        getUsersData();

        apiService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);


        currentUID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getchats(currentUID,userID);
            startChat();
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessage(chatUser,currentUID,userID);
                }
            });



    }
    private void startChat()
    {

        chatListref= FirebaseDatabase.getInstance()
                .getReference()
                .child("ChatsList")
                .child(currentUID).child(userID);

        chatListref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    chatListref.child("id").setValue(userID);
                    chatListref1= FirebaseDatabase.getInstance()
                            .getReference()
                            .child("ChatsList")
                            .child(userID).child(currentUID);
                    chatListref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            chatListref1.child("id").setValue(currentUID);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG," "+error);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG," "+error);
            }
        });




    }
    private void sendMessage(String userName, final String sender, final String receiver)
    {

        notify =true;
       final String msg=type_Message.getText().toString();
       type_Message.setText("");
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        if (!msg.equals("")) {
           chatRef = FirebaseDatabase.getInstance().getReference();
           Map<String, Object> map = new HashMap<>();
           map.put("message", msg);
           map.put("sender", sender);
           map.put("receiver", receiver);

           map.put("status", "send");
           map.put("time", currentTime);
            map.put("date",currentDate);
           chatRef.child("Chats").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   Toast.makeText(MessageActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                   getchats(sender,receiver);

                    type_Message.setHint(R.string.type_message);
                   sendNotification(receiver,msg);
               }
           });
       }
       else
       {
           Toast.makeText(this, "You can't send Empty message", Toast.LENGTH_SHORT).show();
       }

    }

    private void getchats(final String sender, final String receiver)
    {
            chatRef=FirebaseDatabase.getInstance().getReference("/Chats");
            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   chatList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                   {
                       if (dataSnapshot.exists()) {

                        String key=dataSnapshot.getKey();
                           Chat chat = dataSnapshot.getValue(Chat.class);
                           String chatSender,chatReceiver;
                           chatSender=chat.getSender();
                           chatReceiver=chat.getReceiver();
                           if (sender.equals(chatSender) && receiver.equals(chatReceiver) ||
                                   sender.equals( chatReceiver) && receiver.equals(chatSender)) {
                               chatList.add(chat);
                               if (receiver.equals(chatSender)&&sender.equals(chatReceiver))
                               {

                                   chatRef=FirebaseDatabase
                                           .getInstance().getReference("/Chats")
                                           .child(key).child("status");
                                   chatRef.setValue("seen")
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           Log.e("Message...","seen");
                                       }
                                   });
                               }


                           }
                       }
                         }
                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setStackFromEnd(true);
//                    dateList=Extras.getSeperateDates(chatList);

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(new MessageAdapter(MessageActivity.this,chatList,image,userImage,currentUID,userID));
                   // setSeenStatus(sender,receiver);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG," "+error);
                }
            });



    }
    private void setSeenStatus(String sender, final String receiver)
    {
            messsageRef=FirebaseDatabase.getInstance().getReference("/Chats");
        for(int i=0;i<chatList.size();i++)
        {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
    void getUsersData()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance()
                .getReference("/Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    if (users.getId().equals(userID))
                    {
                        online_status=users.getStatus();
                        Log.e("Online Status", "  "+users.getStatus());
                        chatUser=users.getName();
                        userImage=users.getImageURL();
                        Glide.with(getApplicationContext()).load(userImage).into(userProfile);
                        userName.setText(chatUser);
                        if (online_status.equals("online"))
                        {
                            onlineStatus.setText(online_status);
                        }
                        else {
                            onlineStatus.setText(Extras.calculateOnlineStatus(online_status,"active"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG," "+error);
            }
        });
    }
    private void sendNotification(final String reciever, final String message)
    {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(currentUID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                if (notify)
                {
                   // sendNotification(reciever,user.getName(),message);

                    DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
                    Query query=allTokens.orderByKey().equalTo(reciever);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                Token token=dataSnapshot.getValue(Token.class);
                                Data data=new Data(currentUID,name+" "+message,"New Message",reciever,R.drawable.com_facebook_button_icon);

                                Sender sender=new Sender(data,token.getToken());
                                apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                                    @Override
                                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                        Toast.makeText(MessageActivity.this, ""+response.message(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Response> call, Throwable t) {
                                        Toast.makeText(MessageActivity.this, "Error ion retrofit", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG," "+error);
                        }
                    });

                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG," "+error);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
