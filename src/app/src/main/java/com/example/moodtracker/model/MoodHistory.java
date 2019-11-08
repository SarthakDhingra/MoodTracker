package com.example.moodtracker.model;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.moodtracker.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class MoodHistory implements Serializable {
    public interface FirebaseCallback<T> {
        /**
         * callback handler for handling success cases, returned the value is stored in `document` variable
         *
         * @param document depending on the operation, this variable can hold different type of values
         */
        void onSuccess(T document);

        /**
         * callback handler for handling failure cases
         *
         * @param e Exception thrown by the Firebase library
         */
        void onFailure(@NonNull Exception e);
    }


    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Access to the db
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private String user_id;
    // Todo: Make this private
    public ArrayList<MoodEvent> history = new ArrayList<>();

    public MoodHistory(final String user_id) {
        // Get the Mood history for the user
        this.user_id = user_id;
    }

    public static void getMoodHistory(ArrayAdapter adapter, MoodHistory h) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("moodEvents")
                .whereEqualTo("user_id", h.user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            if (doc.exists()) {
                                if (doc.get("reason") == "SAFE_PARCELABLE_NULL_STRING") {
                                    String reason = "";
                                }
                                if (doc.get("photo_url") == "SAFE_PARCELABLE_NULL_STRING" ) {
                                    String photo_url = "";
                                }
//                                Gson gson = new Gson();
//                                Object mood = doc.get("mood");
//                                String json = gson.toJson(mood);
//                                System.out.println(json);

                                MoodEvent me = new MoodEvent(new Mood(constants.HAPPY), h.user_id, new Date());
                                h.history.add(me);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    public void addMoodEvent(MoodEvent e, final FirebaseCallback<Void> cb) {
        db.collection("moodEvents").document(e.getMood_id())
                .set(e)
                .addOnSuccessListener(cb::onSuccess)
                .addOnFailureListener(cb::onFailure);
    }

    public void deleteMoodEvent(MoodEvent e, final FirebaseCallback<Void> cb) {
        // Deleting a mood event
        db.collection("moodEvents").document(e.getMood_id())
                .delete()
                .addOnSuccessListener(cb::onSuccess)
                .addOnFailureListener(cb::onFailure);

    }

}
