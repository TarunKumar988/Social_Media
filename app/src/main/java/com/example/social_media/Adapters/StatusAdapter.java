package com.example.social_media.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.social_media.DisplayPostsActivity;
import com.example.social_media.Extras.Extras;
import com.example.social_media.Extras.ListItem;
import com.example.social_media.Model.PostInfo;
import com.example.social_media.Model.Posts;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.google.android.material.button.MaterialButton;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter
        extends RecyclerView.Adapter<StatusViewHolder>



{
    Context con;
    ArrayList<Users> userlist;
    ArrayList<Posts> list;
    RequestManager requestManager;
    public StatusAdapter(Context con, ArrayList<Posts> list, ArrayList<Users> userlist,RequestManager requestManager)
    {
        this.con=con;
        this.list=list;
        this.userlist=userlist;
        this.requestManager=requestManager;
        setHasStableIds(true);


    }
    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(con).inflate(R.layout.main_recycler_item,parent,false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            String userName="";
            String imageUrl="";
            String fileType="";
                for(int j=0;j<userlist.size();j++)
                {
                    if(list.get(position).getId().equals(userlist.get(j).getId()))
                    {
                       userName= userlist.get(j).getName();
                        imageUrl=userlist.get(j).getImageURL();
                    }
                }


        holder.title.setText(userName);
        Glide.with(con).load(imageUrl).into(holder.profile);


        boolean png,jpg,jpeg,mp4;
            png=list.get(position).getPostUrl().contains(".png");
            jpg=list.get(position).getPostUrl().contains(".jpg");
            jpeg=list.get(position).getPostUrl().contains(".jpeg");


            mp4= list.get(position).getPostUrl().contains(".mp4");
        holder.onBind(list.get(position),requestManager);


        if (mp4)
           {
               fileType=".mp4";
               holder.frameLayout.setVisibility(View.VISIBLE);
              // holder.videoView.setVideoURI(Uri.parse(list.get(position).getPostUrl()));
                holder.imageView.setVisibility(View.GONE);
                holder.frameLayout.setVisibility(View.VISIBLE);


           }
           else if (png || jpg || jpeg)
           {

               fileType=".png";
               RequestOptions options = new RequestOptions()
                       .centerCrop()
                       .placeholder(R.mipmap.ic_launcher_round)
                       .error(R.mipmap.ic_launcher_round);

               holder.imageView.setVisibility(View.VISIBLE);

//               Glide.with(con).load(list.get(position).getPostUrl())
//
//                   .apply(options).into(holder.imageView);

               Glide.with(con).asBitmap().load(list.get(position).getPostUrl())
                       .apply(options).into(new SimpleTarget<Bitmap>() {
                           @Override
                           public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {


                               int w = resource.getWidth();
                               int h = resource.getHeight();
                               Log.e("Height width","   "+h+"  "+w);
                               holder.imageView.setImageBitmap(resource);


                           }
                       });

               holder.frameLayout.setVisibility(View.GONE);

           }
           if (list.get(position).getCaption().equals(""))
           {
               holder.discription.setVisibility(View.GONE);
           }
           else
           {
               holder.discription.setVisibility(View.VISIBLE);
           }

           holder.discription.setText(list.get(position).getCaption());
           holder.likes.setText(String.valueOf(list.get(position).getPostInfo().getLikes()));
        holder.comments.setText(String.valueOf(list.get(position).getPostInfo().getComments()));
        String postsTime=Extras.calculateOnlineStatus(list.get(position).getTime()+" "+list.get(position).getDate(),"posted");
        holder.date.setText(postsTime);



//        String finalUserName = userName;
//        String finalImageUrl = imageUrl;
//        String finalFileType = fileType;
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goNext(finalUserName,finalImageUrl,position,postsTime,finalFileType);
//            }
//        });
//        holder.comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goNext(finalUserName,finalImageUrl,position,postsTime,finalFileType);
//
//            }
//        });
//       holder.likes.setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View v) {
//               int l=Integer.parseInt( holder.likes.getText().toString());
//               int tl=l+1;
//               holder.likes.setText(String.valueOf(tl));
//           }
//       });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    void goNext(String finalUserName,String finalImageUrl,int position,String postsTime,String finalFileType)
    {
    Intent i=new Intent(con, DisplayPostsActivity.class);
                i.putExtra("UsersName", finalUserName);
                i.putExtra("UserProfile",finalImageUrl);
                i.putExtra("Url", list.get(position).getPostUrl());
                i.putExtra("FileType", finalFileType);
                i.putExtra("Caption",list.get(position).getCaption());
                i.putExtra("PostsTime",postsTime);
                i.putExtra("Likes",list.get(position).getPostInfo().getLikes());
                i.putExtra("Comments",list.get(position).getPostInfo().getComments());

                con.startActivity(i);
    }





}




