package com.teamwinnner.android.blog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import static android.R.attr.x;
import static com.teamwinnner.android.blog.R.id.log;
import static com.teamwinnner.android.blog.R.id.profile;

public class MainActivity extends AppCompatActivity {
private RecyclerView mbloglist;
    private DatabaseReference mdatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabasedisLike;
    public boolean mProcessLike =false;
    public boolean mProcessLike1 =false;
    private DatabaseReference mDatabaseCurrentUsre;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mbloglist=(RecyclerView)findViewById(R.id.blog_list) ;
        mbloglist.setHasFixedSize(true);
        mbloglist.setLayoutManager(new LinearLayoutManager(this));
        mdatabase= FirebaseDatabase.getInstance().getReference().child("Blog");


       // String CurrentUserId=mAuth.getCurrentUser().getUid();
        mDatabaseCurrentUsre= FirebaseDatabase.getInstance().getReference().
                child("Blog");
        //mQueryCurrent=mDatabaseCurrentUsre.orderByChild("uid").equalTo(CurrentUserId);
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike= FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabasedisLike= FirebaseDatabase.getInstance().getReference().child("dislike");

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
                final String post_key1=getRef(position).getKey();
                 viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setdislikebtn(post_key1);
                viewHolder.setlikebtn(post_key);
                ;
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
            if (dataSnapshot.child("1").child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                mDatabaseLike.child("1").child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                mProcessLike = false;

            } else {
                mDatabaseLike.child("1").child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                mDatabaseLike.child("2").child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

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
viewHolder.mdislikebtn1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        mProcessLike1=true;
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessLike1) {
                    if (dataSnapshot.child("2").child(post_key1).hasChild(mAuth.getCurrentUser().getUid())) {
                        mDatabaseLike.child("2").child(post_key1).child(mAuth.getCurrentUser().getUid()).removeValue();
                        mProcessLike1 = false;

                    } else {
                        mDatabaseLike.child("2").child(post_key1).child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                        mDatabaseLike.child("1").child(post_key1).child(mAuth.getCurrentUser().getUid()).removeValue();

                        mProcessLike1 = false;
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
     ImageButton mdislikebtn1 ;
     DatabaseReference mDatabaseLike ;
     DatabaseReference mDatabasedisLike ;
     FirebaseAuth mAuth;
     TextView nbrelike ;
     TextView nbredislike ;
     public BlogViewHolder(View itemView) {

         super(itemView);
         mView = itemView;
         nbredislike=(TextView)mView.findViewById(R.id.textdislike);

         nbrelike=(TextView)mView.findViewById(R.id.nombrelike);
          post_title=(TextView)mView.findViewById(R.id.post_title);
         mlikebtn =(ImageButton)mView.findViewById(R.id.mlikebtn);
         mdislikebtn1 =(ImageButton)mView.findViewById(R.id.mdislikebtn);
         mDatabaseLike=  FirebaseDatabase.getInstance().getReference().child("Likes");
         mDatabasedisLike=  FirebaseDatabase.getInstance().getReference().child("dislike");

         mAuth=FirebaseAuth.getInstance();
         mDatabaseLike.keepSynced(true);
         mDatabasedisLike.keepSynced(true);
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
            if(dataSnapshot.child("1").child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
              mlikebtn.setImageResource(R.drawable.ic_thumb_up_red_24dp);


            }else {
                mlikebtn.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            }

                 long x= dataSnapshot.child("1").child(post_key).getChildrenCount();
                 nbrelike.setText(x+"");
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });


     }
     public void setdislikebtn(final String post_key1){
         mDatabaseLike.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 if(dataSnapshot.child("2").child(post_key1).hasChild(mAuth.getCurrentUser().getUid())){
                     mdislikebtn1.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                     long x= dataSnapshot.child("2").child(post_key1).getChildrenCount();
                     nbredislike.setText(x+"");


                 }else {
                     mdislikebtn1.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                     long x= dataSnapshot.child("2").child(post_key1).getChildrenCount();
                     nbredislike.setText(x+"");
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
         String a =txtdesc;
         if(a.length()>80){
            a= txtdesc.substring(0, 100);
             a=a+".... lire la suite";
         }
         post_desc.setText(a);
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
else if(item.getItemId()== profile){
    Intent i =new Intent(MainActivity.this,ProfileActivity .class);
    i.putExtra("x",true);

    startActivity(i);
}
       else {
    Intent i =new Intent(MainActivity.this,SetupActivity.class);
    startActivity(i);
       }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }
}
