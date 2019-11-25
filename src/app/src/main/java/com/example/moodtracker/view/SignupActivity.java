/**
 * SignupActivity
 *
 * Version 1.0
 *
 * 11/8/2019
 *
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.moodtracker.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.moodtracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    String TAG = "signup";
    FirebaseAuth mAuth;
    Button signupBtn;
    EditText emailText;
    EditText passwordText;
    EditText usernameText;
    FirebaseFirestore db;
    String email;
    String username;
    TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        signupBtn = findViewById(R.id.button_signup);
        usernameText = findViewById(R.id.text_username);
        emailText = findViewById(R.id.text_email);
        passwordText = findViewById(R.id.text_password);

        loginText = findViewById(R.id.loginText);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  v) {
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


        db = FirebaseFirestore.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check username first
                db.collection("usernames").document(usernameText.getText().toString()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                          @Override
                          public void onSuccess(DocumentSnapshot documentSnapshot) {

                              if (documentSnapshot.exists()) {
                                  Toast.makeText(SignupActivity.this, "Username taken!", Toast.LENGTH_LONG).show();
                              } else {
                                  mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                                          .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                              @Override
                                              public void onComplete(@NonNull Task<AuthResult> task) {
                                                  if (task.isSuccessful()) {

                                                      Map<String, Object> userMap = new HashMap<>();
                                                      userMap.put("email", emailText.getText().toString());
                                                      userMap.put("username", usernameText.getText().toString());
                                                      FirebaseUser user = mAuth.getCurrentUser();
                                                      String uid = user.getUid();
                                                      // back end apiKey
                                                      Client client = new Client("GZMZW0XPIB", "258da6df8585aac2755616b472826982");
                                                      Index index = client.getIndex("dev_USERNAMES");
                                                      db.collection("usernames").document(usernameText.getText().toString())
                                                              .set(userMap)
                                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {
                                                                      Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                      // log the user profile map as a JSONObject
                                                                      index.addObjectAsync(new JSONObject(userMap), null);
                                                                  }
                                                              })
                                                              .addOnFailureListener(new OnFailureListener() {
                                                                  @Override
                                                                  public void onFailure(@NonNull Exception e) {
                                                                      Log.w(TAG, "Error writing document", e);
                                                                  }
                                                              });

                                                      db.collection("users").document(uid)
                                                              .set(userMap)
                                                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                  @Override
                                                                  public void onSuccess(Void aVoid) {
                                                                      Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                      Intent homeIntent = new Intent(SignupActivity.this, ProfileViewActivity.class);
                                                                      startActivity(homeIntent);
                                                                  }
                                                              })
                                                              .addOnFailureListener(new OnFailureListener() {
                                                                  @Override
                                                                  public void onFailure(@NonNull Exception e) {
                                                                      Log.w(TAG, "Error writing document", e);
                                                                  }
                                                              });

                                                  } else {
                                                      Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                              Toast.LENGTH_SHORT).show();
                                                  }
                                              }
                                          });


                              }
                          }
                      });

            }
        });

    }

}
