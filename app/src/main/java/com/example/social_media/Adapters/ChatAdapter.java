package com.example.social_media.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.Extras;
import com.example.social_media.Extras.RecyclerViewOnClickListener;
import com.example.social_media.Model.Chat;
import com.example.social_media.Model.Users;
import com.example.social_media.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    Context context;
    ArrayList<Users> list;
    ArrayList<Chat> chatlist;
    ArrayList<Chat> userChatList;

    private RecyclerViewOnClickListener recyclerViewOnClickListener;

    public ChatAdapter(Context context, RecyclerViewOnClickListener listener, ArrayList<Users> list, ArrayList<Chat> chatlist)
    {
        this.context=context;
        this.list=list;
        this.recyclerViewOnClickListener=listener;
        this.chatlist=chatlist;
        userChatList=new ArrayList<>();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.chat_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.username.setText(String.valueOf(list.get(position).getName()));
          //  holder.lastMessage.setText(String.valueOf(list.get(position).getEmail()));
            if (list.get(position).getStatus().equals("online"))
            {
                holder.onlineStatus.setText(String.valueOf(list.get(position).getStatus()));

            }
            else
            {
                holder.onlineStatus.setText(Extras.calculateOnlineStatus( list.get(position).getStatus(),"active"));
            }
            getChats(list.get(position),position,holder);
            Glide.with(context).load(list.get(position).getImageURL()).into(holder.profile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profile;
        TextView username,lastMessage,onlineStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile=itemView.findViewById(R.id.userProfile);
           // onlineStatus=itemView.findViewById(R.id.onlineStatus);
            username=itemView.findViewById(R.id.username);
            lastMessage=itemView.findViewById(R.id.message);
            onlineStatus=itemView.findViewById(R.id.online_status);
            itemView.setOnClickListener(this);
        }
        public void onClick(View view)
        {
                recyclerViewOnClickListener.onCLick(view,getAdapterPosition());
        }
    }
    private  void getChats(Users users,int j,ViewHolder holder)
    {
        int i=0;
        String currentUID= FirebaseAuth.getInstance().getCurrentUser().getUid();
       for (int a=0;a<chatlist.size();a++)
       {
           if (currentUID.equals(chatlist.get(a).getSender())&&
           users.getId().equals(chatlist.get(a).getReceiver())||
                   users.getId().equals(chatlist.get(a).getSender())&&
                           currentUID.equals(chatlist.get(a).getReceiver())
           )
           {
               userChatList.add(chatlist.get(a));
               if (currentUID.equals(chatlist.get(a).getReceiver())&&
                       users.getId().equals(chatlist.get(a).getSender()))
               {
                   if (chatlist.get(a).getStatus().equals("send")) {
                       i++;
                   }
               }
           }
       }
       String name= (String) holder.username.getText();
       holder.username.setText(String.valueOf(list.get(j).getName())+"  ("+i+")");
       if (userChatList.size()>0) {
          holder.lastMessage.setText(userChatList.get((userChatList.size() - 1)).getMessage());
      }

    }


}

