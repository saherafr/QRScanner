package com.example.droiddesign.view.AttendeeAndOrganizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Adapters.EventsAdapter;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.example.droiddesign.view.Everybody.EventMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * DiscoverEventsActivity presents a list of events fetched from Firestore for users to browse.
 * It allows users to view details about each event by clicking on them in the list.
 */
public class DiscoverEventsActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;

    /**
     * Called when the activity is first created.
     * Sets up the activity layout and fetches events from Firestore.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_events); //

        eventsRecyclerView = findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchEvents();

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventMenuActivity.class);
            startActivity(intent);
            finish();
        });

    }

    /**
     * Fetches events from Firestore and updates the UI with the fetched events.
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return; // Handle the case where the user is not logged in
        }

        try {
            db.collection("Users").document(currentUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            List<String> signedUpEvents = user.getSignedEventsList();

                            try {
                                db.collection("EventsDB")
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                List<Event> fetchedEvents = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Event event = document.toObject(Event.class);
                                                    if (!signedUpEvents.contains(event.getEventId())) {
                                                        fetchedEvents.add(event);
                                                    }
                                                }
                                                updateUI(fetchedEvents);
                                            } else {
                                                Log.w("DiscoverEventsActivity", "Error getting documents.", task.getException());
                                            }
                                        });
                            } catch (Exception e) {
                                Log.e("DiscoverEventsActivity", "Error fetching events", e);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w("DiscoverEventsActivity", "Error fetching user data", e));
        } catch (Exception e) {
            Log.e("DiscoverEventsActivity", "Error fetching user document", e);
        }
    }

    /**
     * Called when the activity is resumed.
     * Fetches events from Firestore when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        fetchEvents();
    }

    /**
     * Gets the ID of the currently logged-in user.
     *
     * @return The ID of the currently logged-in user.
     */
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

    /**
     * Updates the UI with the fetched events.
     *
     * @param events The list of events fetched from Firestore.
     */
    private void updateUI(List<Event> events) {
        if (!events.isEmpty()) {
            EventsAdapter eventsAdapter = new EventsAdapter(events, event -> {
                Intent detailIntent = new Intent(DiscoverEventsActivity.this, EventDetailsActivity.class);
                detailIntent.putExtra("EVENT_ID", event.getEventId());
                detailIntent.putExtra("ORIGIN", "DiscoverEventsActivity");
                startActivity(detailIntent);
            });
            eventsRecyclerView.setAdapter(eventsAdapter);
        }
    }
}
