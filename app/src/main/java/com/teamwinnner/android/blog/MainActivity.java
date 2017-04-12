package com.teamwinnner.android.blog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import static com.teamwinnner.android.blog.R.id.log;

public class MainActivity extends AppCompatActivity {
private RecyclerView mbloglist;
    private DatabaseReference mdatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    public boolean mProcessLike =false;
    private DatabaseReference mDatabaseCurrentUsre;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Query mQueryCurrent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbloglist=(RecyclerView)findViewById(R.id.blog_list) ;
        mbloglist.setHasFixedSize(true);
        mbloglist.setLayoutManager(new LinearLayoutManager(this));
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");


        String CurrentUserId=mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUsre= FirebaseDatabase.getInstance().getReference().
                child("Blog");
        mQueryCurrent=mDatabaseCurrentUsre.orderByChild("uid").equalTo(CurrentUserId);
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabaseUsers.keepSynced(true);
        mdatabase.keepSynced(true);
        mAuth= FirebaseAuth.getInstance();
        CheckUserExist();
        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        };
    }
    @Override
    protected  void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListener);
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter
                =   new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(Blog.class,R.layout.blog_row,BlogViewHolder.class,mdatabase) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {
              final String post_key=getRef(position).getKey();

                 viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setlikebtn(post_key);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   Intent i = new Intent(MainActivity.this,BlogSingleActivity.class);
                        i.putExtra("blog_id",post_key);
                        startActivity(i);
                   //     Toast.makeText(getApplication(),post_key,Toast.LENGTH_SHORT).show();

                    }
                });
                viewHolder.mlikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike=true;

mDatabaseLike.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (mProcessLike) {
            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                mProcessLike = false;

            } else {
                mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                mProcessLike = false;
            }
        }
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

                    }
                });
            }
        };

mbloglist.setAdapter(firebaseRecyclerAdapter);

    }
    private void CheckUserExist() {
        if(mAuth.getCurrentUser() != null){
        final String user_id=mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( !dataSnapshot.hasChild(user_id)){
                    Intent i = new Intent(MainActivity.this,SetupActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(i);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        }
    }
 public static class BlogViewHolder extends RecyclerView.ViewHolder{
View mView ; TextView post_title;
     ImageButton mlikebtn ;
     DatabaseReference mDatabaseLike ;
     FirebaseAuth mAuth;
     public BlogViewHolder(View itemView) {

         super(itemView);
         mView = itemView;
          post_title=(TextView)mView.findViewById(R.id.post_title);
         mlikebtn =(ImageButton)mView.findViewById(R.id.mlikebtn);
         mDatabaseLike=  FirebaseDatabase.getInstance().getReference().child("Likes");
         mAuth=FirebaseAuth.getInstance();
         mDatabaseLike.keepSynced(true);

         post_title.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

             }
         });

     }
     public void setlikebtn(final String post_key){
         mDatabaseLike.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
              mlikebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            }else {
                mlikebtn.setImageResource(R.drawable.ic_red);
            }


             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });


     }
    public void setTitle(String  txttitle){
        post_title.setText(txttitle);
    }
     public void setDesc(String  txtdesc){
         TextView post_desc=(TextView)mView.findViewById(R.id.post_desc);
         post_desc.setText(txtdesc);
     }
     public void setImage(Context ctx, String image){
         ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
         Picasso.with(ctx).load(image).into(post_image);
     }
     public void setUsername(String username){
         TextView post_username=(TextView)mView.findViewById(R.id.post_username);
         post_username.setText(username);
     }

 }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
if (item.getItemId() == R.id.action_add){
    Intent i =new Intent(MainActivity.this,PostActivity.class);
    startActivity(i);
}
        else if(item.getItemId() ==  log){
    logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
