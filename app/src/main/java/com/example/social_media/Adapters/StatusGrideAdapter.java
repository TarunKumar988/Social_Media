package com.example.social_media.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.social_media.Model.Posts;
import com.example.social_media.Model.Users;
import com.example.social_media.R;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatusGrideAdapter extends RecyclerView.Adapter<StatusGrideAdapter.ViewHolder> {
        Context context;
        ArrayList<Posts> postspList;
        Users users;
    public StatusGrideAdapter(Context context, ArrayList<Posts> postsList, Users users) {
        this.context=context;
        this.postspList=postsList;
        this.users=users;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.main_recycler_gride_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.setIsRecyclable(false);
        String imageUrl=postspList.get(position).getPostUrl();
        boolean png,jpg,jpeg,mp4;
        png=imageUrl.contains(".png");
        jpg=imageUrl.contains(".jpg");
        jpeg=imageUrl.contains(".jpeg");
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        mp4= imageUrl.contains(".mp4");
        if (mp4)
        {
            holder.videoView.setVisibility(View.VISIBLE);
            holder.videoView.setVideoURI(Uri.parse(imageUrl));
                holder.imageView.setVisibility(View.GONE);
        }
        else if (jpeg||png||jpg)
        {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(imageUrl).apply(options).into(holder.imageView);
            holder.videoView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return postspList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        VideoView videoView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            imageView=itemView.findViewById(R.id.imageview);
            videoView=itemView.findViewById(R.id.videoview);
            
            
        }
    }
}
