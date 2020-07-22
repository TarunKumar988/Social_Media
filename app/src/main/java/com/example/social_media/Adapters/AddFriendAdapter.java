package com.example.social_media.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {
    Context context;
    ArrayList<Users> list;
    String currentUserName;
    RecyclerViewOnClickListener listener;
    public AddFriendAdapter(Context context, ArrayList<Users> list,String currentUserName,  RecyclerViewOnClickListener listener)
    {
        this.context=context;
        this.list=list;
        this.currentUserName=currentUserName;
        this.listener=listener;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AddFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.add_friend_item,parent,false);

        return new AddFriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AddFriendAdapter.ViewHolder holder, final int position) {
            holder.name.setText(list.get(position).getName());
        Glide.with(context).load(list.get(position).getImageURL()).into(holder.profile);
            holder.addFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!holder.addFriend.getText().equals("SENT")) {
                        //Toast.makeText(context, "Request send", Toast.LENGTH_SHORT).show();
                        holder.addFriend.setText("SENT");
                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                                .getReference("Friend Requests")
                                .child(list.get(position).getId()).child(uid);
                        Map<String, Object> req=new HashMap<>();
                        req.put("Request",true);
                        req.put("id",uid);
                        req.put("name",currentUserName);
                        databaseReference.setValue(req).addOnCompleteListener( new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Log.e("Sent Successful", "Done ");

                                }
                            }
                        });

                    }

                }
            });
        holder.setIsRecyclable(false);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {

        TextView name;
        Button addFriend;
        CircleImageView profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            addFriend=itemView.findViewById(R.id.addFriend);
            profile=itemView.findViewById(R.id.profile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onCLick(v,getAdapterPosition());
        }
    }

}
