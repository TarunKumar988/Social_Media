package com.example.social_media.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Fragments.FirendRequestFragment;
import com.example.social_media.Model.Requests;

import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {
        Context context;
        ArrayList<Users> arrayList;
        String uid;
        static DatabaseReference databaseReference;
    HashMap<String ,Boolean> map;
    RecyclerViewOnClickListener listener;
        public FriendRequestAdapter(Context context, ArrayList<Users> list, String uid, RecyclerViewOnClickListener listener)
        {
            this.context=context;
            this.arrayList=list;
            this.uid=uid;
            this.listener=listener;
        }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.friend_request_items,parent,false);


            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.name.setText(arrayList.get(position).getName());
            Glide.with(context).load(arrayList.get(position).getImageURL()).into(holder.profile);
            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    acceptFriendRequests(position);

                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        deleteFriendRequestData(position);

                }
            });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView name;
            Button accept,cancel;
            CircleImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.name);
            accept=itemView.findViewById(R.id.accept);
            cancel=itemView.findViewById(R.id.cancel);
            profile=itemView.findViewById(R.id.profile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onCLick(v,getAdapterPosition());
        }
    }

    private void deleteFriendRequestData(int position)
    {

        databaseReference= FirebaseDatabase.getInstance()
                .getReference("Friend Requests")
                .child(uid).child(arrayList.get(position).getId());
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
    private void acceptFriendRequests(final int position)
    {
        databaseReference=FirebaseDatabase.getInstance().getReference("Friends").
        child(uid).child(arrayList.get(position)
                .getId());
       map=new HashMap<>();
        map.put("friend",true);
        databaseReference.setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            databaseReference=FirebaseDatabase.getInstance().getReference("Friends").
                                    child(arrayList.get(position)
                                    .getId()).child(uid);
                            databaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    deleteFriendRequestData(position);
                                }
                            });

                        }

                    }
                });
    }
}
