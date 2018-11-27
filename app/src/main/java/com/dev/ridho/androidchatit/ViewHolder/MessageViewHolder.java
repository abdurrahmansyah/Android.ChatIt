package com.dev.ridho.androidchatit.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dev.ridho.androidchatit.Interface.ItemClickListener;
import com.dev.ridho.androidchatit.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMessageName, txtMessageText, txtMessageTime;
    public CircleImageView imageMessage;

    private ItemClickListener itemClickListener;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        txtMessageName = (TextView)itemView.findViewById(R.id.message_name);
        txtMessageText = (TextView)itemView.findViewById(R.id.message_text);
        txtMessageTime = (TextView)itemView.findViewById(R.id.message_time);
        imageMessage = (CircleImageView)itemView.findViewById(R.id.message_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
    }
}
