/**
 * FindActivity
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

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.moodtracker.R;
import com.example.moodtracker.helpers.BottomNavigationViewHelper;
import com.example.moodtracker.model.User;
import com.example.moodtracker.view.mood.AddMoodEventActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.moodtracker.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FindActivity extends AppCompatActivity {

    ListView userListView;
    ArrayAdapter<User> userArrayAdapter;
    ArrayList<User> userDataList;

    EditText searchText;
    Button searchButton;

    FirebaseFirestore db;
    /**
     * On Create
     *
     * @param savedInstanceState the instance
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        searchText = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        userListView = findViewById(R.id.result_list);
        db = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = db.collection("usernames");
        userDataList = new ArrayList<>();
        userArrayAdapter = new CustomFindList(this, userDataList);
        userListView.setAdapter(userArrayAdapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReference
                        .document(searchText.getText().toString())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();
                                if (task.isSuccessful()){
                                    if(doc.exists()){
                                        // populate ArrayList with results
                                        
                                        // display in the ArrayAdapter
                                        userArrayAdapter.notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(FindActivity.this, "COULDN'T FIND THAT USERNAME", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

        Button followBtn = findViewById(R.id.af_follow_button);
        EditText followUsernameText = findViewById(R.id.af_username_follow);

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get username of myself
                String myId = FirebaseAuth.getInstance().getUid();
                // get username of the person trying to follow
                FirebaseFirestore.getInstance().collection("users").whereEqualTo("username", followUsernameText.getText().toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        if (doc.exists()) {
                                            Log.d("HOME", "DocumentSnapshot data: " + doc.getId());
                                            // put in a hashmap
                                            Map<String, Object> followMap = new HashMap<>();
                                            followMap.put("follower_id", myId);
                                            followMap.put("following_id", doc.getId());

                                            // store in db
                                            FirebaseFirestore.getInstance().collection("follow").document(UUID.randomUUID().toString())
                                                    .set(followMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(FindActivity.this, "Followed " + followUsernameText.getText().toString(), Toast.LENGTH_LONG).show();
                                                            Log.d("HOME", "DocumentSnapshot successfully written!");

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {


                                                            Log.d("HOME", "Error writing document", e);
                                                        }
                                                    });
                                        } else {
//                                            Log.d("HOME", "No such document");
                                            Toast.makeText(FindActivity.this, "COULDN'T FIND THAT USERNAME", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else{
                                    Log.d("HOME", "get failed with ", task.getException());
                                }
                            }

                        });
            }
        });


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.ic_Profile:
                        Intent intent0 = new Intent(FindActivity.this, ProfileFragment.class);
                        startActivity(intent0);
                        break;
                    case R.id.ic_Search:
                        break;
                    case R.id.ic_Add:
                        Intent intent2 = new Intent(FindActivity.this, AddMoodEventActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.ic_Map:
//                        Intent intent3 = new Intent(FindActivity.this, MapActivity.class);
//                        startActivity(intent3);
                        break;
                    case R.id.ic_Feed:
                        break;
                }
                return false;
            }
        });

    }
}
