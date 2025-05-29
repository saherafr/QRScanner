package com.example.droiddesign.view.Organizer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Adapters.UserListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for displaying signed up users.
 * This activity displays a list of users who have signed up for an event.
 */
public class SignedUpUsersActivity extends AppCompatActivity {

    /**
     * Adapter for the RecyclerView that displays the list of users.
     */
    private UserListAdapter usersListAdapter;

    /**
     * List of User objects representing the users to be displayed in the RecyclerView.
     */
    private final List<User> users = new ArrayList<>();

    /**
     * Unique identifier for the event. Used to fetch users related to this specific event.
     */
    private String eventId;

    /**
     * Instance of FirebaseFirestore used for fetching and updating data in the Firestore database.
     */
    private FirebaseFirestore firestore;

    /**
     * Method called when the activity is created.
     * @param savedInstanceState The saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_up_users);

        try {
            firestore = FirebaseFirestore.getInstance();
            eventId = getIntent().getStringExtra("EVENT_ID");

            RecyclerView usersRecyclerView = findViewById(R.id.signup_recyclerview);
            usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            usersListAdapter = new UserListAdapter(users, null, user -> {
                // Do nothing
            });
            usersRecyclerView.setAdapter(usersListAdapter);

            retrieveAttendees();

            ImageButton backButton = findViewById(R.id.button_back);
            backButton.setOnClickListener(v -> {
                finish();
            });
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Retrieves the list of attendees for the event.
     */
    @SuppressLint("NotifyDataSetChanged")
    private void retrieveAttendees() {
        firestore.collection("EventsDB").document(eventId).get().addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> attendeeList = (List<String>) task.getResult().get("attendeeList");
                    if (attendeeList != null) {
                        users.clear(); // Clear the existing users before fetching new ones

                        for (String userId : attendeeList) {
                            firestore.collection("Users").document(userId).get().addOnCompleteListener(userTask -> {
                                try {
                                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                                        User user = userTask.getResult().toObject(User.class);
                                        if (user != null) {
                                            users.add(user);
                                            // Update the adapter's dataset and refresh the RecyclerView
                                            usersListAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast.makeText(this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(this, "Error processing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to load event data.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error fetching attendees: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
