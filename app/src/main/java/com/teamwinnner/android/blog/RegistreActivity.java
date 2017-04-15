package com.teamwinnner.android.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistreActivity extends AppCompatActivity {
    private EditText mNameField,mEmailField,mPasswordField;
    private Button mRegistreBtn;
    private FirebaseAuth mauth;
    private DatabaseReference mDatabase ;
    private ProgressDialog mproress;
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registre);
        getSupportActionBar().setTitle("Sign  up ");

        txt=(TextView)findViewById(R.id.textView);
        mNameField=(EditText)findViewById(R.id.nameField);
        mEmailField=(EditText)findViewById(R.id.emailField);
        mPasswordField=(EditText)findViewById(R.id.passwordField);
        mproress = new ProgressDialog(this);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mRegistreBtn=(Button)findViewById(R.id.registreBtn);
        mauth=FirebaseAuth.getInstance();
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistreActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        mRegistreBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Startregistre();
            }


        });


    }

    private void Startregistre() {
        final String name= mNameField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();
        String password=mPasswordField.getText().toString().trim();

        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
            mproress.setMessage("signing up .....");
            mproress.show();
            mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String user_id=mauth.getCurrentUser().getUid();
                        DatabaseReference current_user_db =  mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");
                        mproress.dismiss();
                        Intent o=new Intent(RegistreActivity.this,MainActivity.class);
                        o.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(o);










                    }
                }
            });

        }
    }
}