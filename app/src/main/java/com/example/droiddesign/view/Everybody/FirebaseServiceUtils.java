package com.example.droiddesign.view.Everybody;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseServiceUtils {
	private static FirebaseFirestore _firestore = null;
	private static FirebaseAuth _firebaseAuth = null;
	private FirebaseServiceUtils() { }

	public static void initialize(FirebaseFirestore fireStore, FirebaseAuth firebaseAuth) {
		FirebaseServiceUtils._firestore = fireStore;
		FirebaseServiceUtils._firebaseAuth = firebaseAuth;
	}
	public static void initialize() {
		FirebaseServiceUtils._firestore = FirebaseFirestore.getInstance();
		FirebaseServiceUtils._firebaseAuth = FirebaseAuth.getInstance();
	}

	public static FirebaseFirestore getFirestore() {
		if (_firestore == null) {
			_firestore = FirebaseFirestore.getInstance();
		}
		return _firestore;
	}

	public static FirebaseAuth getFirebaseAuth() {
		if (_firebaseAuth == null) {
			_firebaseAuth = FirebaseAuth.getInstance();
		}
		return _firebaseAuth;
	}
	// Method to directly fetch the current user's ID
	@Nullable
	public static String getCurrentUserId() {
		FirebaseUser user = _firebaseAuth.getCurrentUser();
		return (user != null) ? user.getUid() : null;
	}

	// Method to directly fetch an event ID from Firestore given a path
	@NonNull
	public static Task<String> getEventId(String documentPath) {
		return _firestore.document(documentPath).get().continueWith(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				DocumentSnapshot snapshot = task.getResult();
				if (snapshot.exists() && snapshot.contains("eventId")) {
					return snapshot.getString("eventId");
				}
			}
			return null; // Return null if the document does not exist or does not contain an eventId
		});
	}
}
