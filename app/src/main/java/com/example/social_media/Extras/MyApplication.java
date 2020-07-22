package com.example.social_media.Extras;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class MyApplication extends Application implements LifecycleObserver {

    DatabaseReference databaseReference;
    String uid="";
    @Override
    public void onCreate() {
        super.onCreate();


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppBackgrounded() {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        databaseReference=FirebaseDatabase.getInstance().getReference("/Users");
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (!String.valueOf(firebaseUser).equals("null"))
        {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if (!uid.equals("")) {
            Log.d("MyApp", "App in background");
            Map map=new HashMap();
            map.put("status",currentTime+" "+ currentDate);
            databaseReference.child(uid).updateChildren(map);

        } }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppForegrounded() {
        databaseReference=FirebaseDatabase.getInstance().getReference("/Users");
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (!String.valueOf(firebaseUser).equals("null"))
        {
            uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if (!uid.equals("")) {
            Log.d("MyApp", "App in foreground");
            Map map=new HashMap();
            map.put("status","online");
            databaseReference.child(uid).updateChildren(map);
            uid="";
        }
    }
}
