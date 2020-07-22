package com.example.social_media.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    Context con;
    ArrayList<Users> friends_list;
    ArrayList<Users> users_List;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerViewOnClickListener listener;
    public FriendsAdapter(Context con,RecyclerViewOnClickListener listener, ArrayList<Users> friends_list)
    {
        this.con=con;
        this.friends_list=friends_list;
        this.listener=listener;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(con)
                .inflate(R.layout.friends_items,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {

           //databaseReference=FirebaseDatabase.getInstance().getReference("Users");
           holder.name.setText(friends_list.get(position).getName());
        Glide.with(con).load(friends_list.get(position).getImageURL()).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return friends_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        CircleImageView profile;
    public ViewHolder(@NonNull View view)
    {
        super(view);
            name=view.findViewById(R.id.name);
            profile=view.findViewById(R.id.profile);
            view.setOnClickListener(this);
    }



        @Override
        public void onClick(View v) {
            listener.onCLick(v,getAdapterPosition());
        }
    }
}
