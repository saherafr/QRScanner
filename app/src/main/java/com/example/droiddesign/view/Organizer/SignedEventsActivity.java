package com.example.droiddesign.view.Organizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.example.droiddesign.view.Adapters.EventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying signed events.
 * This activity displays a list of events that the user has signed up for.
 */
public class SignedEventsActivity extends AppCompatActivity {

    /**
     * The RecyclerView used to display the list of events the user has signed up for.
     */
    private RecyclerView eventsRecyclerView;

    /**
     * Instance of FirebaseFirestore used for database operations.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * List storing the IDs of the events that the user has signed up for.
     */
    private List<String> signedEventsIds;

    /**
     * List storing the Event objects that the user has signed up for, initially empty.
     */
    private final List<Event> signedEventsList = new ArrayList<>();

    /**
     * Method called when the activity is created.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_events);

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());

        try {
            fetchSignedEventsIds();
        } catch (Exception e) {
            Log.e("SignedEventsActivity", "Error in fetchSignedEventsIds", e);
        }
    }

    /**
     * Fetches the IDs of the events that the user has signed up for.
     */
    private void fetchSignedEventsIds() {
        try {
            SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
            String currentUserId = prefsHelper.getUserId();
            db.collection("Users").document(currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            User user = task.getResult().toObject(User.class);
                            if (user != null && user.getSignedEventsList() != null) {
                                signedEventsIds = user.getSignedEventsList();
                                try {
                                    fetchEventsDetails();
                                } catch (Exception e) {
                                    Log.e("SignedEventsActivity", "Error in fetchEventsDetails", e);
                                }
                            }
                        } else {
                            Log.w("SignedEventsActivity", "Error getting signed events list.", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e("SignedEventsActivity", "Error in fetchSignedEventsIds", e);
            throw e;
        }
    }

    /**
     * Fetches the details of the events that the user has signed up for.
     */
    private void fetchEventsDetails() {
        if (signedEventsIds != null && !signedEventsIds.isEmpty()) {
            for (String eventId : signedEventsIds) {
                try {
                    db.collection("EventsDB").document(eventId)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    Event event = task.getResult().toObject(Event.class);
                                    if (event != null) {
                                        signedEventsList.add(event);
                                        if (signedEventsList.size() == signedEventsIds.size()) {
                                            updateUI(signedEventsList);
                                        }
                                    }
                                } else {
                                    Log.w("SignedEventsActivity", "Error fetching event details.", task.getException());
                                }
                            });
                } catch (Exception e) {
                    Log.e("SignedEventsActivity", "Error fetching event details for event ID: " + eventId, e);
                }
            }
        }
    }

    /**
     * Updates the UI with the list of signed events.
     * @param events The list of signed events.
     */
    private void updateUI(List<Event> events) {
        EventsAdapter eventsAdapter = new EventsAdapter(events, event -> {
            Intent detailIntent = new Intent(SignedEventsActivity.this, EventDetailsActivity.class);
            detailIntent.putExtra("EVENT_ID", event.getEventId());
            detailIntent.putExtra("ORIGIN", "SignedEventsActivity");
            startActivity(detailIntent);
        });
        eventsRecyclerView.setAdapter(eventsAdapter);
    }
}
