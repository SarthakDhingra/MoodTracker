package com.example.moodtracker.view.mood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.moodtracker.R;
import com.example.moodtracker.constants;
import com.example.moodtracker.helpers.SocialSituation;
import com.example.moodtracker.model.Mood;
import com.example.moodtracker.model.MoodEvent;
import com.example.moodtracker.model.MoodHistory;
import com.google.type.LatLng;

import java.util.Date;
import java.util.HashMap;

public class AddMoodEventActivity extends AppCompatActivity {
    private String user_id;
    ArrayAdapter<String> adapt;
    ArrayAdapter<String> social_adapt;
    Spinner mood_dropdown;
    Spinner social_situation_dropdown;
    Button submit_btn;
    HashMap<String, String> mood_name_to_num_mapper;
    HashMap<String, Integer> social_situation_mapper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        submit_btn = findViewById(R.id.add_mood_event_submit);
        // Create Hashmaps
        mood_name_to_num_mapper.put("Neutral", "0");
        mood_name_to_num_mapper.put("Happy", "1");
        mood_name_to_num_mapper.put("Surprised", "2");

        // Social Situation Mapper
        social_situation_mapper.put("None", constants.NONE);
        social_situation_mapper.put("Alone", constants.ALONE);
        social_situation_mapper.put("One Other", constants.ONE_OTHER);
        social_situation_mapper.put("Two Others", constants.TWO_OTHER);
        social_situation_mapper.put("Several", constants.SEVERAL);
        social_situation_mapper.put("Crowd", constants.CROWD);

        //get the spinner from the xml.
        mood_dropdown = findViewById(R.id.mood_type_selector);
        String[] mood_items = new String[]{"Happy", "Neutral", "Surprised"};
        adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mood_items);
        mood_dropdown.setAdapter(adapt);

        social_situation_dropdown = findViewById(R.id.social_sitation_selector);
        final String[] social_items = new String[]{"None","Alone", "One Other", "Two Others", "Several", "Crowd"};
        social_adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, social_items);
        social_situation_dropdown.setAdapter(social_adapt);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the MoodEvent here
                EditText reason_view = findViewById(R.id.reason_edit);
                // Add it to the DB
                // Re-get the MoodHistory
                String mood = mood_dropdown.getSelectedItem().toString();
                String reason = reason_view.getText().toString();

                String situation = social_situation_dropdown.getSelectedItem().toString();
                int actual_situation = social_situation_mapper.get(situation);
                SocialSituation social_situation = new SocialSituation(actual_situation);

                // Get lat and lng
                EditText latitude = findViewById(R.id.latitude);
                EditText longitude = findViewById(R.id.longitude);
                String lat = latitude.getText().toString();
                String lng = longitude.getText().toString();
                MoodEvent new_item;
                if (!reason.equals("") &&  !lat.equals("") && !lng.equals("")) {
                    // Convert this to LATLNG
                    Double lat_value = Double.parseDouble(lat);
                    Double lng_value = Double.parseDouble(lng);
                    new_item = new MoodEvent(mood, user_id, new Date(), reason, "", lat_value,lng_value, social_situation);
                } else if (!reason.equals("") ){
                    new_item = new MoodEvent(mood, user_id, new Date(), reason);
                } else {
                    new_item = new MoodEvent(mood, user_id, new Date());
                }
                MoodHistory.externalAddMoodEvent(new_item, new MoodHistory.FirebaseCallback<Void>() {
                    @Override
                    public void onSuccess(Void document) {
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        });
    }

}
