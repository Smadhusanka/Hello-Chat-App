package com.example.hello.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hello.R;
import com.example.hello.model.ChatMessageModel;
import com.example.hello.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel,ChatRecyclerAdapter.ChatModelViewHolder>{

    Context context;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    // send message
    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        //set appears of the chat
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayOut.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(model.getMessage());
        }
        else {
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayOut.setVisibility(View.VISIBLE);
            holder.leftChatTextView.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    //assign id's for variables
    class ChatModelViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftChatLayOut,rightChatLayout;
        TextView leftChatTextView,rightChatTextView;
        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftChatLayOut = itemView.findViewById(R.id.leftChatLayOut);
            rightChatLayout = itemView.findViewById(R.id.rightChatLayOut);
            leftChatTextView = itemView.findViewById(R.id.leftChatTextView);
            rightChatTextView = itemView.findViewById(R.id.rightChatTextView);
        }
    }
}
