package com.example.droiddesign.model;

import android.util.Log;

import okhttp3.MediaType;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Manages the attendance database operations such as user check-ins and milestone notifications.
 */
public class AttendanceDB {

    /**
     * The Firestore database instance used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Initializes a new instance of the AttendanceDB class with a reference to the Firestore database.
     */
    public AttendanceDB() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Checks in a user for an event and increments their check-in count in the database.
     *
     * @param eventId  The ID of the event where the user is checking in.
     * @param userId   The ID of the user who is checking in.
     * @param latitude  The latitude of the check-in location, can be null if location is not provided.
     * @param longitude The longitude of the check-in location, can be null if location is not provided.
     */
    public void checkInUser(String eventId, String userId, Double latitude, Double longitude) {
        String documentId = eventId + "_" + userId;

        db.collection("AttendanceDB")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    long checkInCount = 1;  // Default to 1 if document doesn't exist
                    if (documentSnapshot.exists() && documentSnapshot.contains("check_in_count")) {
                        checkInCount = documentSnapshot.getLong("check_in_count");
                        checkInCount++;  // Increment the count
                    }

                    Map<String, Object> checkInData = new HashMap<>();
                    checkInData.put("event_id", eventId);
                    checkInData.put("user_id", userId);
                    checkInData.put("latitude", latitude);
                    checkInData.put("longitude", longitude);
                    checkInData.put("check_in_count", checkInCount);
                    checkInData.put("timestamp", System.currentTimeMillis());

                    db.collection("AttendanceDB")
                            .document(documentId)
                            .set(checkInData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.d("AttendanceDB", "Check-in count updated successfully");
                                checkEventMilestones(eventId);  // Call to check milestones after check-in
                            })
                            .addOnFailureListener(e -> Log.d("AttendanceDB", "Error updating check-in count", e));
                })
                .addOnFailureListener(e -> Log.e("AttendanceDB", "Error fetching document", e));
    }
    /**
     * Checks if the current check-ins for an event have reached any of the predefined milestones
     * and sends a notification if a milestone is reached.
     *
     * @param eventId The ID of the event to check milestones for.
     */
    private void checkEventMilestones(String eventId) {
        Log.d("AttendanceDB", "Entering checkEventMilestones method for eventId: " + eventId);

        db.collection("AttendanceDB")
                .whereEqualTo("event_id", eventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalCheckIns = queryDocumentSnapshots.size(); // Total number of check-ins for the event
                    Long totalCheckInsLong = (long) totalCheckIns;
                    Log.d("AttendanceDB", "Total check-ins for event " + eventId + ": " + totalCheckIns);

                    db.collection("EventsDB")
                            .document(eventId)
                            .get()
                            .addOnSuccessListener(eventDocumentSnapshot -> {
                                if (eventDocumentSnapshot.exists()) {
                                    String organizerOwnerId = eventDocumentSnapshot.getString("organizerOwnerId");
                                    List<Long> milestones = (List<Long>) eventDocumentSnapshot.get("milestone");

                                    Log.d("AttendanceDB", "Event details fetched for organizer " + organizerOwnerId + " with milestones: " + milestones);

                                    if (milestones != null && !milestones.isEmpty()) {
                                        Object firstMilestone = milestones.get(0);  // Now safe to access after checking if not empty
                                        Log.d("AttendanceDB", "Type of first milestone: " + firstMilestone.getClass().getName());

                                        if (milestones.contains(totalCheckInsLong)) {
                                            Log.d("AttendanceDB", "Milestone reached for event " + eventId + ": " + totalCheckIns);
                                            sendNotification(organizerOwnerId, totalCheckIns, eventId);
                                        } else {
                                            Log.d("AttendanceDB", "Current check-ins " + totalCheckIns + " do not match any milestones for event " + eventId);
                                        }
                                    }
                                } else {
                                    Log.d("AttendanceDB", "Event document does not exist for eventId: " + eventId);
                                }
                            })
                            .addOnFailureListener(e -> Log.e("AttendanceDB", "Error fetching event details for eventId: " + eventId, e));
                })
                .addOnFailureListener(e -> Log.e("AttendanceDB", "Error counting total check-ins for eventId: " + eventId, e));
    }

    /**
     * Sends a notification to the event organizer when a milestone of check-ins is reached.
     *
     * @param organizerOwnerId The user ID of the event organizer.
     * @param totalCheckIns    The total number of check-ins for the event.
     * @param eventId          The ID of the event where the milestone was reached.
     */
    private void sendNotification(String organizerOwnerId, int totalCheckIns, String eventId) {
        Log.d("AttendanceDB", "Entering sendNotification method for event: " + eventId + " with total check-ins: " + totalCheckIns + " for organizer: " + organizerOwnerId);

        // Fetch the organizer's FCM token from Firestore
        db.collection("Users").document(organizerOwnerId).get()
                .addOnSuccessListener(userSnapshot -> {
                    Log.d("AttendanceDB", "Fetched user snapshot for organizer: " + organizerOwnerId);

                    String token = userSnapshot.getString("fcmToken");
                    if (token != null && !token.isEmpty()) {
                        Log.d("AttendanceDB", "FCM token found for user: " + organizerOwnerId + ", token: " + token);

                        // Prepare the list of tokens for notification
                        List<String> tokens = new ArrayList<>();
                        tokens.add(token);

                        // Log the notification details
                        String title = "Milestone Reached";
                        String message = "Your event has reached " + totalCheckIns + " check-ins!";
                        Log.d("AttendanceDB", "Preparing to send notification. Title: " + title + ", Message: " + message);

                        // Call method to send the notification
                        sendNotificationsToTokens(title, tokens, message, eventId);
                    } else {
                        Log.e("AttendanceDB", "FCM token is null or empty for user: " + organizerOwnerId);
                    }
                })
                .addOnFailureListener(e -> Log.e("AttendanceDB", "Failed to fetch FCM token for user: " + organizerOwnerId, e));
    }

    /**
     * Sends notifications to specified tokens using Firebase Cloud Messaging (FCM).
     *
     * @param title    The title of the notification.
     * @param tokens   The FCM tokens to which the notification will be sent.
     * @param message  The message body of the notification.
     * @param eventId  The ID of the event related to the notification.
     */
    private void sendNotificationsToTokens(String title, List<String> tokens, String message, String eventId) {
        Log.d("AttendanceDB", "Entering sendNotificationsToTokens method");
        Log.d("AttendanceDB", "Title: " + title + ", Message: " + message + ", Event ID: " + eventId);
        Log.d("AttendanceDB", "Tokens to notify: " + tokens.toString());

        // Prepare the payload
        JSONObject payload = new JSONObject();
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", message);
            Log.d("AttendanceDB", "Notification JSON prepared");

            // Adding custom data including event ID
            JSONObject data = new JSONObject();
            data.put("eventId", eventId);
            Log.d("AttendanceDB", "Data JSON prepared with eventId");

            payload.put("registration_ids", new JSONArray(tokens));
            payload.put("notification", notification);
            Log.d("AttendanceDB", "Payload prepared: " + payload.toString());

            // Define the MediaType for the request body
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(JSON, payload.toString());
            Log.d("AttendanceDB", "Request body created");

            // Build the request
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .addHeader("Authorization", "key=AAAA1Lwwer4:APA91bHuSelA6Mkvst7R_BZ7Vf2ot9gafIXbpW0e3NyVLAIN60xpGuRRc_QjM0jPYyIT0J4PxBejgGQOo5NuRLfOZn_M9C4m6Pl9uv_CTwKgKRimllR_00ZsVtTHghZ86yAxGuXGUGiE")
                    .post(requestBody)
                    .build();
            Log.d("AttendanceDB", "HTTP request built");

            // Create a new OkHttpClient instance
            OkHttpClient client = new OkHttpClient();

            // Asynchronously send the request
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("sendNotificationsToTokens", "Failed to send notifications", e);
                }
                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e("sendNotificationsToTokens", "Failed to send notifications: " + response);
                    } else {
                        Log.d("sendNotificationsToTokens", "Notifications sent successfully, response: " + response);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e("sendNotificationsToTokens", "JSON exception while preparing the payload", e);
        }
    }



}

