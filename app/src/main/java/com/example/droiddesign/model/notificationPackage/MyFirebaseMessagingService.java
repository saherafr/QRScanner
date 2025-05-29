package com.example.droiddesign.model.notificationPackage;

import static com.example.droiddesign.view.Everybody.FirebaseServiceUtils.getFirebaseAuth;
import static com.example.droiddesign.view.Everybody.FirebaseServiceUtils.getFirestore;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	private static final String TAG = "FCM Service";
    private String eventId;
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom( ));

		// Extract the event ID from the message data if available
		eventId = remoteMessage.getData().get("eventId");
		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
			// Pass the event ID to the method that creates the notification
		}
		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		sendNotification(remoteMessage.getFrom( ), remoteMessage.getNotification( ).getTitle( ));
		sendNotification(remoteMessage.getNotification( ).getTitle( ));
	}

	private void sendNotification(String from, String body) {
		Intent intent = new Intent(this, EventDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Check if eventId is not null and add it as an extra to the intent
		if (eventId != null && !eventId.isEmpty()) {
			intent.putExtra("EVENT_ID", eventId);
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);



		new Handler(Looper.getMainLooper( )).post(new Runnable( ) {
			@Override
			public void run() {
				Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext( ), from + " -> " + body, Toast.LENGTH_SHORT).show( );
			}
		});
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "annoucementChannel")
				.setSmallIcon(R.mipmap.conclavelogo_round).setContentTitle("New Announcement")
				.setContentText(body).setAutoCancel(true).setContentIntent(pendingIntent);

		NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		managerCompat.notify(101, builder.build( ));

	}

	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, EventDetailsActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		String channelId = "fcm_default_channel";
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.mipmap.conclavelogo_round)
						.setContentTitle("New Announcement")
						.setContentText(messageBody)
						.setAutoCancel(true)
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}

	@Override
	public void onNewToken(@NonNull String token) {
		Log.d("FCM", "The new token is: " + token);
		// Assume you have a method to get the current user's ID
		FirebaseUser currentUser = getFirebaseAuth().getCurrentUser();
		if(currentUser != null) {
			String userId = currentUser.getUid();
			FirebaseFirestore db = getFirestore();
			FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
				if(task.isSuccessful()){
					db.collection("Users").document(userId).update("fcmToken",token)
							.addOnSuccessListener(aVoid -> Log.d("UpdateToken", "Token successfully updated for user: " + userId))
							.addOnFailureListener(e -> Log.e("UpdateToken", "Error updating token", e));
				}
			});
		}
	}



}
