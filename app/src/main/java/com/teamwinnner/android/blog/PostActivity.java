package com.teamwinnner.android.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class PostActivity extends AppCompatActivity {
private ImageButton mSelectImage;
    private  static  final int GALLERY_REQUEST = 1;
    private EditText mtitle;
    private EditText mpost;
    private Button submit;
    private Uri imageUri = null;
    private StorageReference mStorageRef;
    private ProgressDialog mprogress;
    private DatabaseReference mdatabase;
    private  FirebaseAuth mAuth;
    private FirebaseUser mCurrentuser;
    private DatabaseReference mDatabaseuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setTitle("Publier Article ");

        mAuth=FirebaseAuth.getInstance();
        mCurrentuser=mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseuser=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentuser.getUid());
        mtitle=(EditText) findViewById(R.id.txttitre);
        mpost=(EditText) findViewById(R.id.txtdesc);
        submit =(Button) findViewById(R.id.btnsubmit);
        mprogress=new ProgressDialog(this );
        mSelectImage =(ImageButton)findViewById(R.id.imageselect) ;
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,GALLERY_REQUEST);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Startposting();
            }

            private void Startposting() {
                mprogress.show();
             final String title,desc;
                title=mtitle.getText().toString().trim();
                desc=mpost.getText().toString().trim();
       if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty(desc)&imageUri != null){
           mprogress.setMessage("posting");


           StorageReference filepath = mStorageRef.child("Blog_Images").child(imageUri.getLastPathSegment());

               filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     final  Uri downloadUrl = taskSnapshot.getDownloadUrl();
                       final  DatabaseReference newpost=mdatabase.push();

                        mDatabaseuser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                newpost.child("title").setValue(title);
                                newpost.child("desc").setValue(desc);
                                newpost.child("image").setValue(downloadUrl.toString());
                                newpost.child("uid").setValue(mCurrentuser.getUid());
                                newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                           Intent i = new Intent(PostActivity.this,MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                       Intent i = new Intent(PostActivity.this,MainActivity.class);
                       startActivity(i);
                       mprogress.dismiss();
                   }
               });




















       }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode == RESULT_OK){
          imageUri= data.getData();
            mSelectImage.setImageURI(imageUri);

        }
    }
}
