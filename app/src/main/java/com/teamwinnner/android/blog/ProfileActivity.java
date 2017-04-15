package com.teamwinnner.android.blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mdatabase;
    private ImageView imagprofile ;
    private TextView nameuser ;
    private FirebaseAuth  mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile ");

        imagprofile=(ImageView)findViewById(R.id.imageViewprofile) ;
        nameuser=(TextView)findViewById(R.id.nametxt);
        mAuth=FirebaseAuth.getInstance();
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        Bundle b =getIntent().getExtras();
        String user=b.getString("1");
        Boolean cas=b.getBoolean("x");
        if(cas == true){
       final  String a=mAuth.getCurrentUser().getUid();
 mdatabase.child(a).addValueEventListener(new ValueEventListener() {
     @Override
     public void onDataChange(DataSnapshot dataSnapshot) {
         String post_desc=(String) dataSnapshot.child("name").getValue();
         String post_image=(String) dataSnapshot.child("image").getValue();
         nameuser.setText(post_desc);
         Picasso.with(ProfileActivity.this).load(post_image).into(imagprofile);

     }

     @Override
     public void onCancelled(DatabaseError databaseError) {

     }
 });
    }
    else {
            mdatabase.child(user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String post_desc=(String) dataSnapshot.child("name").getValue();
                    String post_image=(String) dataSnapshot.child("image").getValue();
                    nameuser.setText(post_desc);
                    Picasso.with(ProfileActivity.this).load(post_image).into(imagprofile);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    @Override
    public void onBackPressed() {
        Intent BackpressedIntent = new Intent();
        BackpressedIntent .setClass(getApplicationContext(),MainActivity.class);
        BackpressedIntent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(BackpressedIntent );
        finish();
    }
}
