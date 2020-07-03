package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.auth.UserProfileChangeRequest.Builder;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.Object;

public class dashboard extends AppCompatActivity {
    private static final int CHOOSE_IMAGE = 101;
    ImageView imageView;
    EditText editText;
    Uri uriprofileimage;
    ProgressBar progressBar;
    String profileimageurl;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mAuth = FirebaseAuth.getInstance();

        editText = (EditText) findViewById(R.id.edittext_dash);
        imageView = (ImageView) findViewById(R.id.image_d);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_dash);

        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showimagechooser();
            }
        });

        loaduserInformation();


        findViewById(R.id.button_dash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveuserinfo();
            }
        });

    }

    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()==null)
        {
            finish();
            startActivity(new Intent(this, MainActivity.class));

        }
    }
    private void loaduserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null) {
            if (user.getPhotoUrl() != null) {
                //String photourl = user.getPhotoUrl().toString();
                Glide.with(this).load(user.getPhotoUrl().toString()).into(imageView);

            }
            if (user.getDisplayName() != null) {
                //String displayname = user.getDisplayName();
                editText.setText(user.getDisplayName());

            }

        }

    }

    private void saveuserinfo() {
        String displayname = editText.getText().toString();

        if (displayname.isEmpty())
        {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getInstance().getCurrentUser();

        if (user!=null && profileimageurl!=null)
        {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayname).setPhotoUri(Uri.parse(profileimageurl)).build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Profile updated",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //in this  method we will get the selected image
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData()!=null)
        {
            uriprofileimage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriprofileimage);
                imageView.setImageBitmap(bitmap);
                //to store the image on firebase storage
                //first enable firebase storage from assistant
                uploadimageTofirebaseStorage();



            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void uploadimageTofirebaseStorage() {
        StorageReference profileimagereference = FirebaseStorage.getInstance().getReference("Profilepics/"+System.currentTimeMillis() + ".jpg");

        if (uriprofileimage != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            profileimagereference.putFile(uriprofileimage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.GONE);
                    profileimageurl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();




                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    private void showimagechooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select profile picture"),CHOOSE_IMAGE);

    }
}