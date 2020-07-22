package com.example.social_media.Extras;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.social_media.Model.Chat;
import com.example.social_media.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Extras {
    static  FirebaseAuth firebaseAuth;
    static FirebaseUser firebaseUser;
    static DatabaseReference databaseReference;
    static ArrayList<String> friendsList;
    static ArrayList<Users> usersList;
    public static Users returnUser;
    static ArrayList<Users> friendRequets;
    public static String currentUserName;
    public static boolean isInternetConnected(Context context)
{
    ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
   // Boolean isconnected=connectivityManager.isActiveNetworkMetered();

    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null &&
            activeNetwork.isConnectedOrConnecting();
    if (!isConnected)
    {
        Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
    }
    else
    {
        Toast.makeText(context, "Internet", Toast.LENGTH_SHORT).show();
    }
            return isConnected;
}

public static String calculateOnlineStatus(String onlineStatus)
{
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

    String statusTime=onlineStatus.substring(0,5);
    String statusDate= onlineStatus.substring(6,16);
    int statusTimehours= Integer.parseInt(statusTime.substring(0,2));
    int statusTimeMint= Integer.parseInt(statusTime.substring(3,5));
    int statusDateDay= Integer.parseInt(statusDate.substring(0,2));
    int statusDateMonth= Integer.parseInt(statusDate.substring(3,5));
    int statusDateYear= Integer.parseInt(statusDate.substring(6,10));


    Log.e(" DAtes : ",statusTime+" "+statusDate);

    Log.e("Times ",statusTimehours+" "+statusTimeMint
                    +" "+statusDateDay+" "+statusDateMonth+" "+statusDateYear
            );
    Calendar Day = Calendar.getInstance();
    Day.set(Calendar.MINUTE, statusTimeMint);
    Day.set(Calendar.HOUR, statusTimehours-12);
    Day.set(Calendar.DAY_OF_MONTH,statusDateDay);
    Day.set(Calendar.MONTH,statusDateMonth-1);
    Day.set(Calendar.YEAR, statusDateYear);
    Day.set(Calendar.AM_PM,0);

    Calendar current=Calendar.getInstance();
    current.set(Calendar.AM_PM,0);
    Log.e(" tIMES : ",current+" "+Day);
    long different=current.getTimeInMillis()-Day.getTimeInMillis();
    long secondsInMilli = 1000;
    long minutesInMilli = secondsInMilli * 60;
    long hoursInMilli = minutesInMilli * 60;
    long daysInMilli = hoursInMilli * 24;

    long elapsedDays = different / daysInMilli;
    different = different % daysInMilli;

    long elapsedHours = different / hoursInMilli;
    different = different % hoursInMilli;

    long elapsedMinutes = different / minutesInMilli;
    different = different % minutesInMilli;

    long elapsedSeconds = different / secondsInMilli;

   Log.e("Differnce",
            elapsedDays+" days "+ elapsedHours+" hour "
                    + elapsedMinutes+" Min "+ elapsedSeconds+"sec ");
   if (elapsedDays>0)
   {
       return  "active "+elapsedDays+"d ago";
   }
   else if (elapsedHours>0)
   {
       return  "active "+elapsedHours+"h ago";
   }
   else if (elapsedMinutes>0)
   {
       return  "active "+elapsedMinutes+"m ago";
   }
   else {
       return  "active "+1+"m ago";
   }
}

public static ArrayList<String> getSeperateDates(ArrayList<Chat> list)
{

    String tempDate=list.get(0).getDate();
    ArrayList<String> dateList=new ArrayList<>();

    for (int i=0;i<list.size();i++)
    {
        if (!tempDate.equals(list.get(i).getDate()))
        {
            dateList.add(tempDate);
            tempDate=list.get(i).getDate();
        }

    }
    return dateList;
}




}

