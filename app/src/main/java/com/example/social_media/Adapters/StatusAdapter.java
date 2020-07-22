package com.example.social_media.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder>
{
    Context con;
    String image;
    ArrayList<String> list;
    public StatusAdapter(Context con, ArrayList<String> list, String image)
    {
        this.con=con;
        this.list=list;
        this.image=image;
    }
    @NonNull
    @Override
    public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(con).inflate(R.layout.main_recycler_item,parent,false);
        return new StatusAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, int position) {
            holder.title.setText(list.get(position));
            Glide.with(con).load(image).into(holder.profile);
            holder.imageView.setImageDrawable(con.getResources().getDrawable(R.drawable.loginbackground));
//        Glide.with(con).load(image).into(holder.profile);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
      ImageView imageView;
      CircleImageView profile;
      TextView title,discription;
      TextView views,likes,comments,time;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageview);
            title=itemView.findViewById(R.id.title);
            discription=itemView.findViewById(R.id.discription);
            profile=itemView.findViewById(R.id.profile);
            views=itemView.findViewById(R.id.views);
            likes=itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            time=itemView.findViewById(R.id.time);

        }
    }
}
