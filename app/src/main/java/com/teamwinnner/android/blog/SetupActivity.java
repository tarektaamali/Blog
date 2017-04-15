package com.teamwinnner.android.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {
 private ImageButton imageButton;
    private EditText  Name;
    private Button btn ;
    private Uri mImageUri=null;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private StorageReference mstorageImage  ;
 private ProgressDialog mprogress ;
    private static  final int Gallery_request=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().setTitle("Setting ");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
                mAuth=FirebaseAuth.getInstance();
        mstorageImage= FirebaseStorage.getInstance().getReference().child("Profil_image");
        imageButton =(ImageButton)findViewById(R.id.setupimage);
        Name =(EditText) findViewById(R.id.setupName);
        mprogress=new ProgressDialog(this);
        btn=(Button) findViewById(R.id.setupbtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent= new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,Gallery_request);

            }
        });
       btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startsetupaccount();
            }
        });


    }

    private void startsetupaccount() {
        final String user_id=mAuth.getCurrentUser().getUid();
      final String mName=Name.getText().toString().trim();
     if(!TextUtils.isEmpty(mName)&& mImageUri!=null){
         mprogress.setMessage("uploading ..");
         mprogress.show();
         StorageReference filepath = mstorageImage.child(mImageUri.getLastPathSegment());
         filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
             @Override
             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 String downloaderuri=taskSnapshot.getDownloadUrl().toString();


                 mDatabaseUsers.child(user_id).child("name").setValue(mName);
                 mDatabaseUsers.child(user_id).child("image").setValue(downloaderuri);
                 mprogress.dismiss();
                 Intent i = new Intent(SetupActivity.this,MainActivity.class);
                 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                 startActivity(i);
             }
         });




     }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_request && resultCode == RESULT_OK){
            Uri imageUri= data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

            //
            //mSelectImage.setImageURI(imageUri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                imageButton.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
    }
}}

