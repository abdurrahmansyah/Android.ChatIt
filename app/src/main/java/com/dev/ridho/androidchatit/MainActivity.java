package com.dev.ridho.androidchatit;

import android.content.DialogInterface;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.DateTimeKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.ridho.androidchatit.Common.Common;
import com.dev.ridho.androidchatit.Interface.ItemClickListener;
import com.dev.ridho.androidchatit.Model.Messages;
import com.dev.ridho.androidchatit.Model.User;
import com.dev.ridho.androidchatit.ViewHolder.MessageViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnChat, btnEmot;
    TextView txtChat;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Messages, MessageViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference messagesDB;
    DatabaseReference userDB;

    Calendar time = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:SS");
    Date date;

    private long lastBackPressTime = 0;
    private Toast toast;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.chatwall1);
        setContentView(R.layout.activity_main);

        btnChat = (Button)findViewById(R.id.btnChat);
        btnEmot = (Button)findViewById(R.id.btnEmot);
        txtChat = (TextView)findViewById(R.id.txtChat);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        messagesDB = database.getReference("Messages");
        userDB = database.getReference("User");

        // Load Menu
        recyclerView = (RecyclerView)findViewById(R.id.recycleMessage);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!txtChat.getText().toString().isEmpty()){
                    SendMessage();
                    txtChat.setText("");
                }
            }
        });

        loadListMessage();
    }

    private void SendMessage() {
        date = new Date();
        String minute;
        // Create new message
        Messages message = new Messages(
                Common.currentUser.getPhone(),
                "",
                "",
                txtChat.getText().toString(),
                date.getHours() + ":" + (date.getMinutes()<10?"0":"") + date.getMinutes()
        );

        // Submit to Firebase
        // We will using System.CurrentMilli to key
        messagesDB.child(String.valueOf(System.currentTimeMillis()))
                .setValue(message);

        layoutManager.smoothScrollToPosition(recyclerView, null, adapter.getItemCount());
    }

    private void loadListMessage() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Messages");

        FirebaseRecyclerOptions<Messages> options = new FirebaseRecyclerOptions.Builder<Messages>().setQuery(query, Messages.class).build();

        adapter = new FirebaseRecyclerAdapter<Messages, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageViewHolder viewHolder, int position, @NonNull final Messages message) {
                userDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(message.getUserPhoneFrom()).exists()){
                            user = dataSnapshot.child(message.getUserPhoneFrom()).getValue(User.class);
                            user.setPhone(message.getUserPhoneFrom());

                            viewHolder.txtMessageName.setText(user.getName());
                            viewHolder.txtMessageText.setText(message.getText());
                            viewHolder.txtMessageTime.setText(message.getTime());
                            Picasso.with(getBaseContext()).load(user.getImage())
                                    .into(viewHolder.imageMessage);

                            viewHolder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    Toast.makeText(MainActivity.this, "Create new action !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            viewHolder.txtMessageName.setText(message.getUserPhoneFrom());
                            viewHolder.txtMessageText.setText(message.getText());
                            viewHolder.txtMessageTime.setText(message.getTime());

                            viewHolder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    Toast.makeText(MainActivity.this, "Create new action !", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.message_layout, viewGroup, false);
                return new MessageViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast = Toast.makeText(this, "Press back again to close this app", 4000);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        } else {
            if (toast != null) {
                toast.cancel();
            }
            super.onBackPressed();
        }

//        super.onBackPressed();
//        new AlertDialog.Builder(this)
//                .setMessage("Are you sure you want to exit?")
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                })
//                .setNegativeButton("NO", null)
//                .show();
    }
}
