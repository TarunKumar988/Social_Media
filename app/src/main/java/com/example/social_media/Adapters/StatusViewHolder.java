package com.example.social_media.Adapters;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.social_media.Extras.VideoPlayerRecyclerView;
import com.example.social_media.Model.Posts;
import com.example.social_media.R;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusViewHolder extends RecyclerView.ViewHolder  {
   public ImageView imageView,thumbnail,volume_control;
    public CircleImageView profile;
    public TextView title,discription;
    public TextView date;

    public MaterialButton views,likes,comments;
    public FrameLayout frameLayout;
    public RequestManager requestManager;
    public  ProgressBar progress_Bar;
    public View  parent;
//    VideoView frameLayout;


    public StatusViewHolder(@NonNull View itemView) {
        super(itemView);
        parent=itemView;
        imageView=itemView.findViewById(R.id.imageview);

        title=itemView.findViewById(R.id.title);
        discription=itemView.findViewById(R.id.discription);
        profile=itemView.findViewById(R.id.profile);

        likes=itemView.findViewById(R.id.likes);
        comments=itemView.findViewById(R.id.comments);
        date=itemView.findViewById(R.id.date);
        frameLayout=itemView.findViewById(R.id.media_container);

        thumbnail=itemView.findViewById(R.id.thumbnail);
        volume_control=itemView.findViewById(R.id.volume_control);
        progress_Bar=itemView.findViewById(R.id.progress_bar);
    }


    public void onBind(Posts mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;

        this.parent.setTag(this);
        this.requestManager.asBitmap()
                .load(mediaObject.getPostUrl())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        frameLayout.setMinimumWidth(resource.getWidth());
                        frameLayout.setMinimumHeight(resource.getHeight());
                        thumbnail.setImageBitmap(resource);

                    }
                });
    }
}
