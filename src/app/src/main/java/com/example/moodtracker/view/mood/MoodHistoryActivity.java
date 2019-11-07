package com.example.moodtracker.view.mood;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.moodtracker.R;
import com.example.moodtracker.adapter.MoodHistoryAdapter;
import com.example.moodtracker.constants;
import com.example.moodtracker.controller.MoodEventController;
import com.example.moodtracker.controller.MoodHistoryController;
import com.example.moodtracker.model.Mood;
import com.example.moodtracker.model.MoodEvent;
import com.example.moodtracker.model.MoodHistory;

import java.util.ArrayList;
import java.util.Date;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class MoodHistoryActivity extends AppCompatActivity {

    private ListView moodHistoryList;
    private ArrayAdapter<MoodEvent> HistoryAdapter;
    private MoodHistory moodHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);
        moodHistory = MoodHistoryController.getMoodHistory();
        ArrayList<MoodEvent> mock_events = new ArrayList<>();
        // Mock Data
        String a = "1234";
        Date b = new Date();
        Mood m = new Mood(constants.HAPPY);
        MoodEvent mock_1 = new MoodEvent(m, a, b);
        mock_events.add(mock_1);
        moodHistory.history = mock_events;
        // Get the ListView to assign the new data to
        moodHistoryList = findViewById(R.id.mood_history);
        // Point the list towards the data
        HistoryAdapter = new MoodHistoryAdapter(this,  moodHistory.history);
        moodHistoryList.setAdapter(HistoryAdapter);
    }
}
