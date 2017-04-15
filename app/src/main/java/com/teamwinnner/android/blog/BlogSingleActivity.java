package com.teamwinnner.android.blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class BlogSingleActivity extends AppCompatActivity {
    private DatabaseReference mdatabase;
    private ImageView bpost_image ;
    private TextView bpost_title ,bpost_desc,bpost_username;
    private Button BtnRemove;
    private FirebaseAuth  mAuth;
    String post_uid ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        getSupportActionBar().setTitle("Article ");

        bpost_image=(ImageView)findViewById(R.id.post_image);
        bpost_title=(TextView)findViewById(R.id.post_title);
        bpost_desc=(TextView)findViewById(R.id.post_desc);
        bpost_username=(TextView)findViewById(R.id.post_user);
        BtnRemove=(Button)findViewById(R.id.mSingleBtn);
        mAuth=FirebaseAuth.getInstance();
        Bundle b =getIntent().getExtras();
        final String mpost_key=b.getString("blog_id");

        mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
  mdatabase.child(mpost_key).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
            String post_title=(String) dataSnapshot.child("title").getValue();
               String post_desc=(String) dataSnapshot.child("desc").getValue();
               String post_image=(String) dataSnapshot.child("image").getValue();
               post_uid=(String) dataSnapshot.child("uid").getValue();
               String post_user=(String) dataSnapshot.child("username").getValue();

               bpost_title.setText(post_title);
               bpost_desc.setText(post_desc);
               bpost_username.setText(post_user);
     Picasso.with(BlogSingleActivity.this).load(post_image).into(bpost_image);
               if(mAuth.getCurrentUser().getUid() .equals(post_uid)){
                   BtnRemove.setVisibility(View.VISIBLE);

               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
        BtnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdatabase.child(mpost_key).removeValue();
                Intent i = new Intent(BlogSingleActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        bpost_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a=post_uid;
                Intent env= new Intent(BlogSingleActivity.this,ProfileActivity.class);
                env.putExtra("1",a);
                env.putExtra("x",false);
                startActivity(env);
            }
        });

    }
}
