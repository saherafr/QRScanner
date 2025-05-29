package com.example.droiddesign.view.Organizer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.QRcode;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.view.AttendeeAndOrganizer.QrCodeScanActivity;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddEventSecondActivity extends AppCompatActivity {

    /**
     * Instance of FirebaseFirestore to interact with the Firebase Firestore database.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Launcher for starting the QR code generator activity and handling its result.
     */
    private ActivityResultLauncher<Intent> qrGeneratorLauncher, scanQrLauncher;

    /**
     * A unique identifier for the event being added, generated using UUID.
     */
    private final String uniqueID = UUID.randomUUID().toString();


    private String eventName, eventLocation, eventStartTime, eventEndTime, eventDate, shareQrUrl, shareQrId, checkInQrUrl, checkInQrId, imagePosterId;

    /**
     * List of milestones associated with the event.
     */
    private List<Integer> milestoneList = new ArrayList<>();

    /**
     * Launcher for starting the image upload activity and handling its result.
     */
    private ActivityResultLauncher<Intent> imageUploadLauncher;


    private double eventLongitude, eventLatitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        qrGeneratorLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            shareQrUrl = data.getStringExtra("shareQrUrl");
                            shareQrId = data.getStringExtra("shareQrId");
                            checkInQrUrl = data.getStringExtra("checkInQrUrl");
                            checkInQrId = data.getStringExtra("checkInId");
                            Log.d("AddEventSecondActivity", "Share QR Code ID: " + shareQrId);
                            Log.d("AddEventSecondActivity", "Check-in QR Code ID: " + checkInQrId);
                        }
                    }
                });


        scanQrLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    // Retrieve the check-in QR code URL and ID from the result
                    checkInQrUrl = data.getStringExtra("checkInQrUrl");
                    checkInQrId = data.getStringExtra("checkInQrId");
                    generateShareQrCode();

                }
            }
        });



        Intent intent = getIntent();
        populateEventFromIntent(intent);

        MaterialButton buttonUploadPoster = findViewById(R.id.button_upload_poster);
        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        Button finishAddButton = findViewById(R.id.finish_add_button);
        Button setMilestonesButton = findViewById(R.id.set_milestones_button);
        setMilestonesButton.setOnClickListener(view -> showMilestonesDialog());

        setupDropdownMenu(dropdownMenu);

        imageUploadLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imagePosterId = data.getStringExtra("imagePosterUrl");

                    }
                }
        );

        buttonUploadPoster.setOnClickListener(view -> {
            Intent imageUploadIntent = new Intent(AddEventSecondActivity.this, ImageUploadActivity.class);
            imageUploadLauncher.launch(imageUploadIntent);
        });



        finishAddButton.setOnClickListener(view -> {
            saveEvent();
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());
    }

    /**
     * Populates the event data from the passed intent.
     * @param intent The intent containing the event data.
     */
    private void populateEventFromIntent(Intent intent) {
        try {
            eventName = intent.getStringExtra("eventName");
            eventLocation = intent.getStringExtra("eventLocation");
            eventStartTime = intent.getStringExtra("startTime");
            eventEndTime = intent.getStringExtra("endTime");
            eventDate = intent.getStringExtra("startDate");
            eventLongitude = intent.getDoubleExtra("longitude", -113.5257417);
            eventLatitude = intent.getDoubleExtra("latitude", 53.5281589);

        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error populating event from intent", e);
            Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up the dropdown menu for selecting QR code options.
     * @param dropdownMenu The dropdown menu to set up.
     */
    private void setupDropdownMenu(AutoCompleteTextView dropdownMenu) {
        try {
            String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, listItems);
            dropdownMenu.setAdapter(adapter);

            dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if ("Generate New QR".equals(selectedItem)) {
                    Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                    qrGeneratorIntent.putExtra("eventID", uniqueID);
                    qrGeneratorLauncher.launch(qrGeneratorIntent);
                } else if ("Use Existing QR".equals(selectedItem)) {
                    Intent intent = new Intent(AddEventSecondActivity.this, QrCodeScanActivity.class);
                    intent.putExtra("ORIGIN", "AddEventSecondActivity");
                    intent.putExtra("EVENT_ID", uniqueID);
                    scanQrLauncher.launch(intent);
                }

            });
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error setting up dropdown menu", e);
            Toast.makeText(this, "Error setting up QR options", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Displays a dialog for adding milestones to the event.
     */
    private void showMilestonesDialog() {
        Dialog milestonesDialog = new Dialog(this);
        milestonesDialog.setContentView(R.layout.dialog_milestones);

        if (milestonesDialog.getWindow() != null) {
            milestonesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout milestoneContainer = milestonesDialog.findViewById(R.id.milestone_container);
        final int[] milestoneCount = {1}; // Use an array to hold the count

        Button addMilestoneButton = milestonesDialog.findViewById(R.id.add_milestone);
        Button doneButton = milestonesDialog.findViewById(R.id.done_button);
        Button cancelButton = milestonesDialog.findViewById(R.id.dialog_cancel_button);

        addMilestoneButton.setOnClickListener(v -> {
            milestoneCount[0]++; // Increment the count in the array
            EditText newMilestoneEditText = new EditText(this);
            newMilestoneEditText.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            newMilestoneEditText.setHint("Milestone " + milestoneCount[0]);
            milestoneContainer.addView(newMilestoneEditText); // Add the new EditText to the container
        });

        doneButton.setOnClickListener(v -> {
            milestoneList.clear(); // Clear the list to prevent duplicates if the dialog is opened again
            for (int i = 0; i < milestoneContainer.getChildCount(); i++) {
                View child = milestoneContainer.getChildAt(i);
                if (child instanceof EditText) {
                    String milestoneText = ((EditText) child).getText().toString();
                    if (!milestoneText.isEmpty()) {
                        int milestoneValue = Integer.parseInt(milestoneText);
                        milestoneList.add(milestoneValue);
                    }
                }
            }
            milestonesDialog.dismiss();
        });
        cancelButton.setOnClickListener(v -> milestonesDialog.dismiss());

        milestonesDialog.show();
    }

    /**
     * Generates a share QR code for the event.
     * @return The QR code object.
     */
    private void generateShareQrCode() {
        QRcode shareQrCode = new QRcode(uniqueID, "share");
        shareQrId = shareQrCode.getQrId();

        shareQrCode.upload(new QRcode.OnQrCodeUploadListener() {
            @Override
            public void onQrCodeUploadSuccess() {
                shareQrUrl = shareQrCode.getUri();
                Log.d("AddEventSecondActivity", "Check-in QR code uploaded successfully. URL: " + checkInQrUrl);
            }

            @Override
            public void onQrCodeUploadFailure(String errorMessage) {
                // Handle upload failure
                Log.e("CheckInQRCode", "Check-in QR code upload failed: " + errorMessage);
                // Display an error message or take appropriate action
            }
        });
    }

    /**
     * Saves the event details to Firestore and navigates to the event details page.
     */
    private void saveEvent() {
        TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
        String eventDescription = eventDescriptionTextView.getText().toString();
        Log.d("AddEvent", "Max Attendees String: '" + eventDescription + "'");
        TextView maxAttendeesTextView = findViewById(R.id.input_number_max_attendees);
        String maxAttendeesString = maxAttendeesTextView.getText().toString().trim();

        int maxAttendees = 0;
        if (!maxAttendeesString.isEmpty()) {
            try {
                maxAttendees = Integer.parseInt(maxAttendeesString);
                if (maxAttendees <= 0) {
                    Toast.makeText(AddEventSecondActivity.this, "Number of maximum attendees must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(AddEventSecondActivity.this, "Invalid number for maximum attendees", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add user event to their managedEventsList
        SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
        String currentUserId = prefsHelper.getUserId();

        Event event = new Event(
                uniqueID, eventName, eventDate, eventLocation, eventLongitude, eventLatitude, eventStartTime, eventEndTime, currentUserId,
                imagePosterId, eventDescription, maxAttendees, 0, milestoneList, shareQrUrl, checkInQrUrl, shareQrId, checkInQrId);

        event.saveToFirestore();

        DocumentReference userRef = db.collection("Users").document(currentUserId);
        userRef.update("managedEventsList", FieldValue.arrayUnion(event.getEventId()))
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("AddEventSecondActivity", "Error adding event to user's managedEventsList", e));

        Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
        detailsIntent.putExtra("EVENT_ID", event.getEventId());
        detailsIntent.putExtra("ORIGIN", "AddEventSecondActivity");
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailsIntent);

        finish();
    }
}

