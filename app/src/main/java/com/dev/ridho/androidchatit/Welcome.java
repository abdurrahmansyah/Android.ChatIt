package com.dev.ridho.androidchatit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.ridho.androidchatit.Common.Common;
import com.dev.ridho.androidchatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Welcome extends AppCompatActivity {

    Button btnSignIn, btnSignUp;
    TextView txtPhone, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Init Component
        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        txtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        txtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tableUser = database.getReference("User");

        User user = Common.currentUser;

        if (Common.currentUser != null){
            txtPhone.setText(Common.currentUser.getPhone());
            txtPassword.setText(Common.currentUser.getPassword());
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(Welcome.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (txtPhone.getText().toString().isEmpty()){
                            mDialog.dismiss();
                            Toast.makeText(Welcome.this, "Fill phone number !.", Toast.LENGTH_SHORT).show();
                        }
                        // Check if User Already Registered
                        else if (dataSnapshot.child(txtPhone.getText().toString()).exists()){
                            mDialog.dismiss();
                            User user = dataSnapshot.child(txtPhone.getText().toString()).getValue(User.class);
                            user.setPhone(txtPhone.getText().toString()); // set Phone
                            if (user.getPassword().equals(txtPassword.getText().toString()))
                            {
                                Intent mainActivityIntent = new Intent(Welcome.this, MainActivity.class);
                                Common.currentUser = user;
                                startActivity(mainActivityIntent);
                                finish();
                            } else {
                                Toast.makeText(Welcome.this, "Wrong Password !!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(Welcome.this, "User not registered !", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.currentUser != null){
                    User user = new User(Common.currentUser.getName(), txtPassword.getText().toString(), txtPhone.getText().toString());
                    Common.currentUser = user;
                }
                Intent signUpIntent = new Intent(Welcome.this, SignUp.class);
                startActivity(signUpIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Are you sure you want to exit?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Welcome.super.onBackPressed();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
