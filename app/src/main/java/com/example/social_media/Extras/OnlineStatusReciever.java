package com.example.social_media.Extras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class OnlineStatusReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Working BroadCast", Toast.LENGTH_SHORT).show();


        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null)
        {
            if (networkInfo.getType()==ConnectivityManager.TYPE_MOBILE)
            {
//                Toast.makeText(context, "interent By Mobile data", Toast.LENGTH_SHORT).show();
            }
            if (networkInfo.getType()==ConnectivityManager.TYPE_WIFI)
            {
//                Toast.makeText(context, "interent By Wifi", Toast.LENGTH_SHORT).show();
            }
            setStatus(context);
        }
        else
        {
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
        }


    }

    void setStatus(Context context)
    {
                if (!String.valueOf(FirebaseAuth.getInstance().getCurrentUser()).equals("null"))
        {
            String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (Extras.isInternetConnected(context)) {


                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("status").setValue("online");
            }
            else
            {
                FirebaseDatabase.getInstance().getReference("Users").child(uid).child("status").setValue("offline");

            }
        }
    }
}
