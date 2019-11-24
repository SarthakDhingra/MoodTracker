/*
 * Copyright (c) 2019. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.moodtracker.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.moodtracker.R;
import com.example.moodtracker.constants;
import com.example.moodtracker.helpers.FirebaseHelper;
import com.example.moodtracker.helpers.MoodHistoryHelpers;
import com.example.moodtracker.model.Mood;
import com.example.moodtracker.model.MoodEvent;
import com.example.moodtracker.model.MoodHistory;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoodEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoodEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoodEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MOOD_EVENT = "mood_event";
    private static final String POSITION = "position";
    private static final String LOC_CHANGED = "location_changed";

    private LinearLayout fragment_layout;
    private LinearLayout edit_body;
    private TextView frag_mood;
    private ImageView mood_emoji;
    private ImageView frag_image;
    private Toolbar toolbar;
    private TextView date;
    private Button delete;
    private Button edit;
    private Button cancel;
    private Button done;
    private Button remove_photo;
    private Button upload_photo;
    private EditText reason;
    private Spinner social_situations;
    private Spinner moods;
    private ArrayAdapter<String> social_adapt;
    private ArrayAdapter<String> mood_adapt;
    private ProgressBar pbar;
    private Uri image = null;

    private boolean removed_image_from_me;
    // TODO: Rename and change types of parameters
    private MoodEvent mMoodEvent;
    private int mPosition;
    private boolean location_changed;

    private OnFragmentInteractionListener mListener;

    public MoodEventFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MoodEventFragment newInstance(MoodEvent moodEvent, int position, boolean location_changed) {
        MoodEventFragment fragment = new MoodEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOOD_EVENT, moodEvent);
        args.putInt(POSITION, position);
        args.putBoolean(LOC_CHANGED, location_changed);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            System.out.println("getting my stuff");
            mMoodEvent = getArguments().getParcelable(MOOD_EVENT);
            mPosition = getArguments().getInt(POSITION);
            location_changed = getArguments().getBoolean(LOC_CHANGED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mood_event, container, false);
        setUpFragmentWithMoodEvent(mMoodEvent, view);
        // Onclick Listeners
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        // For removing photos
        remove_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image = null;
                if (mMoodEvent.getPhoto_url()!= null) {
                    removed_image_from_me = true;
                }
                frag_image.setImageResource(0);
                remove_photo.setVisibility(View.GONE);
            }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileChooser();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelete(mPosition);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                done.setVisibility(View.VISIBLE);
                upload_photo.setVisibility(View.VISIBLE);
                if (mMoodEvent.getPhoto_url()!= null) {
                    remove_photo.setVisibility(View.VISIBLE);
                }
                //Enable the views
                enableViews();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disable the views again
                cancelAndEditVisibilities();
                setUpFragmentWithMoodEvent(mMoodEvent, view);
                disableViews();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean difference = false;
                if (location_changed) {
                    mMoodEvent.setLng(null);
                    mMoodEvent.setLat(null);
                }
                cancelAndEditVisibilities(); // Handles visibilities for Front end
                if (image != null) {
                    difference = true;
                }else if (image == null && removed_image_from_me) {
                    difference = true;
                    mMoodEvent.setPhoto_url(null);
                }
                // Reasons
                if (mMoodEvent.getReason() == null) {
                    if (!reason.getText().toString().equals("")) {
                        difference = true;
                        mMoodEvent.setReason(reason.getText().toString());
                    }
                }
                else if ((!mMoodEvent.getReason().equals(reason.getText().toString()))) {
                    difference = true;
                    if (reason.getText().toString().equals("")) {
                        mMoodEvent.setReason(null);
                    } else {
                        mMoodEvent.setReason(reason.getText().toString());
                    }
                }

                //Social Situation
                if (mMoodEvent.getSocialSituation() == null) {
                    if (!social_situations.getSelectedItem().toString().equals("None")) {
                        difference = true;
                        mMoodEvent.setSocial_situation(social_situations.getSelectedItem().toString());
                    }
                }
                else if (!mMoodEvent.getSocialSituation().equals(social_situations.getSelectedItem().toString())){
                    difference = true;
                    if (social_situations.getSelectedItem().toString().equals("None")) {
                        mMoodEvent.setSocial_situation(null);
                    } else {
                        mMoodEvent.setSocial_situation(social_situations.getSelectedItem().toString());
                    }
                }

                // Check if the mood is different
                Mood selected_mood = constants.mood_num_to_mood_obj_mapper.get(mMoodEvent.getMood());
                if (!selected_mood.getMoodName().equals(moods.getSelectedItem().toString())) {
                    difference = true;
                    mMoodEvent.setMood(constants.mood_name_to_num_mapper.get(moods.getSelectedItem().toString()));
                }
                // Now send it to the db and update the HistoryActivity
                if (difference) {
                    edit_body.setVisibility(View.GONE);
                    pbar.setVisibility(View.VISIBLE);
                    onUpdate(mMoodEvent, mPosition, image, new MoodHistory.FirebaseCallback() {
                        @Override
                        public void onSuccess(Object document) {
                            image = null;
                            edit_body.setVisibility(View.VISIBLE);
                            pbar.setVisibility(View.GONE);
                            setUpFragmentWithMoodEvent(mMoodEvent, view);
                            disableViews();
                        }

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            edit_body.setVisibility(View.VISIBLE);
                            pbar.setVisibility(View.GONE);
                            System.out.println("Failed to edit");
                        }
                    });
                } else {
                    setUpFragmentWithMoodEvent(mMoodEvent, view);
                    disableViews();
                }

            }
        });
        return view;
    }

    private void enableViews() {
        // Enable the views
        reason.setEnabled(true);
        social_situations.setEnabled(true);
        moods.setEnabled(true);
    }

    private void disableViews() {
        reason.setEnabled(false);
        social_situations.setEnabled(false);
        moods.setEnabled(false);
    }

    private void layoutEnableOrDisable(LinearLayout myLayout, boolean choice) {
        for ( int i = 0; i < myLayout.getChildCount();  i++ ){
            View view = myLayout.getChildAt(i);
            view.setEnabled(choice); // Or whatever you want to do with the view.
        }
    }

    private void cancelAndEditVisibilities() {
        cancel.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        upload_photo.setVisibility(View.GONE);
        remove_photo.setVisibility(View.GONE);
    }

    private void setUpFragmentWithMoodEvent(MoodEvent e, View v) {
        cancel = v.findViewById(R.id.cancel_edit);
        done = v.findViewById(R.id.done_edit);
        edit = v.findViewById(R.id.edit);
        upload_photo = v.findViewById(R.id.upload_photo);
        remove_photo = v.findViewById(R.id.remove_photo);
        // Show things based on permissions
        if (e.getUser_id().equals(FirebaseHelper.getUid())) {
            edit.setVisibility(View.VISIBLE);
        }

        delete = v.findViewById(R.id.delete);
        if (e.getUser_id().equals(FirebaseHelper.getUid())){
            delete.setVisibility(View.VISIBLE);
        }

        reason = v.findViewById(R.id.reason_me);
        if (e.getReason()!= null) {
            reason.setText(e.getReason());
        }

        // Image Handler
        frag_image = v.findViewById(R.id.me_frag_image);
        if (e.getPhoto_url()!= null) {
            Picasso.get().load(e.getPhoto_url()).into(frag_image);
        }
        //Spinner that holds all the possible social_situations
        social_situations = v.findViewById(R.id.me_frag_spinner);
        social_adapt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, constants.social_situations_list);
        social_situations.setAdapter(social_adapt);
        if (e.getSocialSituation()!= null) {
            social_situations.setSelection(constants.SS_name_to_index_mapper.get(e.getSocialSituation()));
        }
        social_situations.setEnabled(false);

        // Set up the mood as a spinner
        moods = v.findViewById(R.id.me_mood_frag_spinner);
        mood_adapt = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, constants.mood_list);
        moods.setAdapter(mood_adapt);
        moods.setSelection(constants.mood_num_to_index_mapper.get(e.getMood()));
        moods.setEnabled(false);


        // Setting the background based on the mood
        Mood selected_mood = constants.mood_num_to_mood_obj_mapper.get(e.getMood());
        fragment_layout = v.findViewById(R.id.fragment_layout);
        fragment_layout.setBackgroundColor(Color.parseColor(selected_mood.getColor()));

        // Handle Mood Related items
        mood_emoji = v.findViewById(R.id.emoji_view);
        mood_emoji.setImageResource(selected_mood.getIcon());

        // Handle the Date
        String formatted_date = MoodHistoryHelpers.formatDate(e.getDate());
        date = v.findViewById(R.id.date);
        date.setText(formatted_date);

        // Set up the Toolbar
        toolbar = v.findViewById(R.id.mood_event_view_tb);
        toolbar.setBackgroundColor(Color.parseColor(selected_mood.getColor()));

        pbar = v.findViewById(R.id.progress_bar);
        edit_body = v.findViewById(R.id.edit_body);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onDelete(int position) {
        if (mListener != null) {
            mListener.onDeleteFragmentInteraction(position);
            getActivity().onBackPressed();
        }
    }

    public void onUpdate(MoodEvent e, int position, Uri photo, MoodHistory.FirebaseCallback cb){
        if (mListener != null) {
            mListener.onUpdateFragmentInteraction(e, position, photo, cb);
        }
    }

    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode==RESULT_OK && data != null && data.getData()!= null) {
            image = data.getData();
            frag_image.setImageURI(image);
            removed_image_from_me = false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDeleteFragmentInteraction(int position);
        void onUpdateFragmentInteraction(MoodEvent e, int position, Uri photo, final MoodHistory.FirebaseCallback cb);
    }
}