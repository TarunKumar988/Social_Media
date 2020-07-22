package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registeration_Activity extends AppCompatActivity {
    EditText email,name;
    Button next;
    CircleImageView profile;
    DatabaseReference ref;


    static String n,e,i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);
        profile=findViewById(R.id.profile_image);
        email=findViewById(R.id.email);
        name=findViewById(R.id.name);
        next=findViewById(R.id.next);
        n=getIntent().getExtras().getString("Name");
        e=getIntent().getExtras().getString("Email");
        i=getIntent().getExtras().getString("Image");



        email.setText(e);
        name.setText(n);
        Glide.with(getApplicationContext()).load(i).into(profile);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity2.class);
                Log.e("Sending Url ", i);
                intent.putExtra("image",i);
                intent.putExtra("name",n);
                startActivity(intent);
                finish();
            }
        });


FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        String userid=user.getUid();
        ref= FirebaseDatabase.getInstance().getReference("Users").child(userid);

        HashMap <String,String>hs=new HashMap<>();
        hs.put("Name",n);
        hs.put("email",e);
        hs.put("imageURL",i);
        hs.put("status","offline");

        ref.setValue(hs).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
//                            Intent i=new Intent(getApplicationContext(),LogIn.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i);
//                            finish();
                            Log.e("RegisterSuceess", "Done ");

                        }
                    }
                });


    }

    }

