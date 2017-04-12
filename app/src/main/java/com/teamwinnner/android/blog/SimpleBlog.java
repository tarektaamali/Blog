package com.teamwinnner.android.blog;

import android.app.Application;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Android on 10/04/2017.
 */

public class SimpleBlog extends Application {
    @Override
    public  void onCreate(){
        super.onCreate();
        /*
        *http://stackoverflow.com/questions/37346363/java-lang-illegalstateexception-firebaseapp-with-name-default
        *
        *
         */
        if(!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        }
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


    }
}
