package com.example.social_media.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.Model.Chat;
import com.example.social_media.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    int VIEW_TYPE_LEFT=0;
    int VIEW_TYPE_RIGHT=1;
    ArrayList<Chat> chatList;
    Context context;
    String myImage,currentUID,userID,userImage;
    FirebaseUser firebaseUser;

    static String tempdate="date";

    int temp;
    public MessageAdapter(Context context,ArrayList<Chat> chatList,String myImage,String userImage,String currentUID,String userID)
    {
        this.context=context;
        this.chatList=chatList;
        this.myImage=myImage;
        this.userImage=userImage;
        this.currentUID=currentUID;
        this.userID=userID;
       // tempdate=chatList.get(0).getDate();
        setHasStableIds(true);

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType==VIEW_TYPE_RIGHT)
        {
              view= LayoutInflater.from(context).inflate(R.layout.send_message_item,parent,false);
                temp=VIEW_TYPE_RIGHT;
        }
        if (viewType==VIEW_TYPE_LEFT)
        {
            view= LayoutInflater.from(context).inflate(R.layout.receive_message_item,parent,false);
            temp=VIEW_TYPE_LEFT;
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.message.setText(chatList.get(position).getMessage());
            holder.time.setText(chatList.get(position).getTime());
        holder.date.setVisibility(View.GONE);
            if (position!=0) {


                if (chatList.get(position).getDate()
                        .equals
                                (chatList.get(position-1).getDate()))
                {
                    holder.date.setVisibility(View.GONE);
                }
                else
                {
                    holder.date.setVisibility(View.VISIBLE);
                    holder.date.setText(chatList.get(position).getDate());
                }

            }
            else
            {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(chatList.get(position).getDate());

            }


            if (temp==VIEW_TYPE_LEFT)
            {
//                holder.status.setVisibility(View.INVISIBLE);
                Glide.with(context).load(userImage).into(holder.profile);
            }
            if (temp==VIEW_TYPE_RIGHT)
            {
                Glide.with(context).load(myImage).into(holder.profile);
                if (chatList.get(position).getStatus().equals("seen"))
                {
                    holder.status.setBackgroundResource(R.mipmap.seen);
                }
                else
                {
                    holder.status.setBackgroundResource(R.mipmap.send);
                }
            }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView message,time,status,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.time);
            status=itemView.findViewById(R.id.status);
            profile=itemView.findViewById(R.id.profile);
            message=itemView.findViewById(R.id.message);
            date=itemView.findViewById(R.id.date);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return VIEW_TYPE_RIGHT;
        }
        else
        {
            return VIEW_TYPE_LEFT;
        }

    }
}
