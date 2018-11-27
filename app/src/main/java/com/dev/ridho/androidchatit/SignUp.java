package com.dev.ridho.androidchatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.ridho.androidchatit.Common.Common;
import com.dev.ridho.androidchatit.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    MaterialEditText txtPhone, txtName, txtPassword;
    Button btnSignUp, btnUploadImage;

    ImageView imageView;
    CircleImageView circleImageView;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;

    String imageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        txtName = (MaterialEditText)findViewById(R.id.edtName);
        txtPassword = (MaterialEditText)findViewById(R.id.edtPassword);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnUploadImage = (Button)findViewById(R.id.btnUploadImage);

        imageView = (ImageView)findViewById(R.id.imageViewBackground);
        circleImageView = (CircleImageView)findViewById(R.id.circleImageView);

        // Init Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (Common.currentUser != null){
            txtPhone.setText(Common.currentUser.getPhone());
            txtName.setText(Common.currentUser.getName());
            txtPassword.setText(Common.currentUser.getPassword());
        }

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                if(txtPhone.getText().toString().isEmpty()
                        || txtName.getText().toString().isEmpty()
                        || txtPassword.getText().toString().isEmpty()){
                    mDialog.dismiss();
                    Toast.makeText(SignUp.this, "Data must be fill !!", Toast.LENGTH_SHORT).show();
                }
                else{
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Check if user Phone already exist
                            if(dataSnapshot.child(txtPhone.getText().toString()).exists())
                            {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Phone Number already registered", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                mDialog.dismiss();
                                User user = new User(txtName.getText().toString(), txtPassword.getText().toString(), imageUri);
                                table_user.child(txtPhone.getText().toString()).setValue(user);
                                user.setPhone(txtPhone.getText().toString());
                                Common.currentUser = user;
                                Toast.makeText(SignUp.this, "Sign Up successfully", Toast.LENGTH_SHORT).show();
                                Intent welcomeIntent = new Intent(SignUp.this, Welcome.class);
                                startActivity(welcomeIntent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseImage();
            }
        });
    }

    private void ChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                circleImageView.setImageBitmap(bitmap);

                UploadToFirebase();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void UploadToFirebase() {
        // Process Upload to Firebase
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUri = uri.toString();
                                    Log.d("GETURI", ""+ uri.toString());
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SignUp.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
}
