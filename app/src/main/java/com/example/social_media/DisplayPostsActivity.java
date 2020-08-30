package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class DisplayPostsActivity extends AppCompatActivity {
    MaterialTextView userName,postsTime,caption;
    ImageView imageView;
    VideoView videoView;
    CircleImageView profile;
    MaterialButton likes,comments;
    String uName;
    String uprofile;
    String uUrl;
    String uCaption;
    String pTime;
    int uLikes;
    int uComments;
    String filetytpe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_posts);

        Bundle bundle=getIntent().getExtras();
        uName=bundle.getString("UsersName");
        uprofile=bundle.getString("UserProfile");
        uUrl=bundle.getString("Url");
        uCaption=bundle.getString("Caption");
        pTime=bundle.getString("PostsTime");
        uLikes=bundle.getInt("Likes");
        uComments=bundle.getInt("Comments");
        filetytpe=bundle.getString("FileType");

        userName=findViewById(R.id.username);
        postsTime=findViewById(R.id.postedTime);
        caption=findViewById(R.id.caption);
        profile=findViewById(R.id.profile);

        imageView=findViewById(R.id.imageview);
        videoView=findViewById(R.id.videoview);

        likes=findViewById(R.id.likes);
        comments=findViewById(R.id.comments);

       setData();

    }
    private void setData()
    {
        userName.setText(uName);
        postsTime.setText(pTime);
        caption.setText(uCaption);


        if (filetytpe.equals(".mp4"))
        {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoURI(Uri.parse(uUrl));

        }
        else if (filetytpe.equals(".png")){

            videoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);

            Glide.with(this).asBitmap().load(uUrl).apply(options).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    imageView.setImageBitmap(resource);

                }
            });


        }

        likes.setText(String.valueOf(uLikes));
        comments.setText(String.valueOf(uComments));
        Glide.with(this).load(uprofile).into(profile);



    }
}