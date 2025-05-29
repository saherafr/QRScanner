package com.example.droiddesign.view.AttendeeAndOrganizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;
/**
 * The ProfileSettingsActivity class represents the user profile settings screen. It allows users to view and edit their profile settings, including their username, email, contact number, company, and profile picture.
 */
public class ProfileSettingsActivity extends AppCompatActivity {

    private EditText editUsername, editUserEmail, editUserContactNumber, editUserCompany, editDisplayUserName, editDisplayUserCompany;
    /**
     * Button for saving the profile settings.
     */
    private Button saveButton;

    /**
     * ImageView for displaying the user's profile picture.
     */
    private ImageView profileImageView;

    /**
     * Instance of FirebaseFirestore used for database operations.
     */
    private FirebaseFirestore db;

    /**
     * Instance of the current FirebaseUser.
     */
    private FirebaseUser currentUser;
    /**
     * URI of the selected image for the profile picture.
     */
    private Uri imageUri;

    /**
     * URL of the user's profile picture in Firebase Storage.
     */
    String profilePicUrl;

    /**
     * Reference to Firebase Storage where the profile picture will be uploaded.
     */
    private StorageReference mStorageRef;

    /**
     * Helper class for managing shared preferences.
     */
    SharedPreferenceHelper prefsHelper;
    private String imageUrl;
    String userId;
    String avatarUrl;

    /**
     * Called when the activity is starting..
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        // Initialize EditText fields
        editUsername = findViewById(R.id.editUsername);
        editUserEmail = findViewById(R.id.editUserEmail);
        editUserContactNumber = findViewById(R.id.editUserContactNumber);
        editUserCompany = findViewById(R.id.editUserCompany);
        editDisplayUserName= findViewById(R.id.UserDisplayName);
        editDisplayUserCompany = findViewById(R.id.UserCompanyDisplay);
        profileImageView = findViewById(R.id.profile_image_view);
        Button editProfilePicButton = findViewById(R.id.edit_image_button);
        Button deleteProfilePicButton = findViewById(R.id.delete_image_button);
        Button editProfileButton = findViewById(R.id.edit_profile_button);
        Button deleteProfileButton = findViewById(R.id.delete_profile);
        saveButton = findViewById(R.id.buttonSave);
        ImageButton backButton = findViewById(R.id.button_back);
        deleteProfileButton.setVisibility(View.GONE);
        deleteProfilePicButton.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) throw new AssertionError();
        userId = currentUser.getUid();

        prefsHelper = new SharedPreferenceHelper(this);
        String savedUserId = prefsHelper.getUserId();
        String userRole = prefsHelper.getRole();;


        if ("Admin".equalsIgnoreCase(userRole)) {
            userId = getIntent().getStringExtra("USER_ID");
            editProfileButton.setVisibility(View.GONE);
            editProfilePicButton.setVisibility(View.GONE);
            deleteProfileButton.setVisibility(View.VISIBLE);

        } else {
            userId = savedUserId;
        }

        avatarUrl = "https://robohash.org/" + userId;

        mStorageRef = FirebaseStorage.getInstance().getReference("profile-pics");

        deleteProfileButton.setOnClickListener(v -> {
            db.collection("Users")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileSettingsActivity.this, "Profile deleted successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error deleting profile.", Toast.LENGTH_SHORT).show());
        });




        deleteProfilePicButton.setOnClickListener(v -> {
            if (currentUser != null) {
                db.collection("Users").document(currentUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String currentProfilePic = documentSnapshot.getString("profilePic");
                            if ((currentProfilePic != null && currentProfilePic.startsWith("https://ui-avatars.com")) || (currentProfilePic != null && currentProfilePic.startsWith("https://robohash.org/"))) {
                                // Current picture is the default, so don’t delete it
                                Toast.makeText(ProfileSettingsActivity.this, "Default profile picture cannot be removed.", Toast.LENGTH_SHORT).show();

                            } else {
                                // Picture is not the default, so delete it and set back to the default
                                String userName = documentSnapshot.getString("userName");
                                String defaultAvatarUrl;

                                if (userName != null && !userName.isEmpty()) {
                                    // Construct the avatar URL with the userName
                                    defaultAvatarUrl = "https://ui-avatars.com/api/?name=" + userName + "&background=random";
                                } else {
                                    // Use a default avatar URL or another placeholder when userName is null or empty
                                    defaultAvatarUrl = avatarUrl;
                                }

                                Glide.with(this).load(defaultAvatarUrl).into(profileImageView);

                                db.collection("Users").document(currentUser.getUid())
                                        .update("profilePic", defaultAvatarUrl)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(ProfileSettingsActivity.this, "Profile picture reset to default.", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(ProfileSettingsActivity.this, "Failed to remove profile picture.", Toast.LENGTH_SHORT).show());
                                deleteProfilePicButton.setVisibility(View.GONE);
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(ProfileSettingsActivity.this, "Failed to fetch profile picture info.", Toast.LENGTH_SHORT).show());
            }
        });




        // Set up profile picture upload button
        editProfilePicButton.setOnClickListener(view -> {
            ImagePicker.with(ProfileSettingsActivity.this)
                    .crop()  // Crop image (optional)
                    .compress(1024)  // Final image size will be less than 1 MB (optional)
                    .maxResultSize(1080, 1080)  // Final image resolution will be less than 1080 x 1080 (optional)
                    .start();

            deleteProfilePicButton.setVisibility(View.VISIBLE);
        });

        // Set up text change listeners
        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editDisplayUserName.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editUserCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editDisplayUserCompany.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editDisplayUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                if (!editUsername.getText().toString().equals(newText)) {
                    editUsername.setText(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editDisplayUserCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String newText = charSequence.toString();
                if (!editUserCompany.getText().toString().equals(newText)) {
                    editUserCompany.setText(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        editUserContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString();
                if (phoneNumber.length() != 10) {
                    editUserContactNumber.setError("Phone number must be 10 digits");
                } else {
                    editUserContactNumber.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString();
                if (phoneNumber.length() != 10) {
                    editUserContactNumber.setError("Phone number must be 10 digits");
                } else {
                    editUserContactNumber.setError(null);
                }
            }
        });

        // Initialize Buttons

        // Set initial state of EditTexts to be non-editable
        setEditingEnabled(false);

        // Load existing profile settings
        if (currentUser != null) {
            loadProfileSettings();
        }

        // Set up button listeners
        backButton.setOnClickListener(v -> finish());

        editProfileButton.setOnClickListener(v -> {
                    setEditingEnabled(true);
                    editProfileButton.setVisibility(View.GONE);
                    Toast.makeText(this, "Ready to edit! Click on any field to update.", Toast.LENGTH_SHORT).show();
                });


        saveButton.setOnClickListener(v -> {
            // Perform validation checks
            boolean isValid = validateProfileFields();

            if (isValid) {
                saveProfileSettings();
                setEditingEnabled(false); // Disable editing after save
                editProfileButton.setVisibility(View.VISIBLE);
            }

        });
    }

    /**
     * Enables or disables editing of the profile settings.
     * @param isEnabled True if editing should be enabled, false otherwise.
     */
    private void setEditingEnabled(boolean isEnabled) {
        editUsername.setEnabled(isEnabled);
        editUserEmail.setEnabled(isEnabled);
        editUserContactNumber.setEnabled(isEnabled);
        editUserCompany.setEnabled(isEnabled);
        editDisplayUserName.setEnabled(isEnabled);
        editDisplayUserCompany.setEnabled(isEnabled);
        saveButton.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Loads the user's profile settings from Firestore and populates the EditTexts.
     */
    private void loadProfileSettings() {
        // Fetch user settings from Firestore and populate EditTexts

        if (currentUser != null) {
            db.collection("Users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editUsername.setText(documentSnapshot.getString("userName"));
                            editUserEmail.setText(documentSnapshot.getString("email"));
                            editUserContactNumber.setText(documentSnapshot.getString("phone"));
                            editUserCompany.setText(documentSnapshot.getString("company"));
                            editDisplayUserName.setText(documentSnapshot.getString("userName"));
                            editDisplayUserCompany.setText(documentSnapshot.getString("company"));

                            // Load profile picture with Glide
                            profilePicUrl = documentSnapshot.getString("profilePic");

                            if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                                profilePicUrl = avatarUrl;
                                // Update the Firestore document with the avatarUrl for profilePic
                                db.collection("Users").document(userId)
                                        .update("profilePic", avatarUrl)
                                        .addOnSuccessListener(aVoid -> {
                                            // Profile picture field updated successfully
                                            Log.d("ProfileSettings", "Profile picture field updated with avatar URL");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle the error
                                            Log.e("ProfileSettings", "Error updating profile picture", e);
                                        });
                            }

                            Glide.with(this).load(profilePicUrl).into(profileImageView);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }
    }

    /**
     * Saves the updated profile settings to Firestore.
     */


    private void saveProfileSettings() {
        // Save updated settings to Firestore
        String newUsername = editUsername.getText().toString();
        String newUserEmail = editUserEmail.getText().toString();
        String newUserContactNumber = editUserContactNumber.getText().toString();
        String newUserCompany = editUserCompany.getText().toString();

        // Generate new profile picture URL based on the updated username
        String newProfilePicUrl = "https://ui-avatars.com/api/?name=" + newUsername + "&background=random";

        // Update user data in Firestore, including the new profile picture URL
        db.collection("Users").document(userId)
                .update(
                        "userName", newUsername,
                        "email", newUserEmail,
                        "phone", newUserContactNumber,
                        "company", newUserCompany,
                        "profilePic", newProfilePicUrl
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully.", Toast.LENGTH_SHORT).show();
                    Glide.with(this).load(newProfilePicUrl).into(profileImageView);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    // Handle failure
                });
    }

    /**
     * Validates the input fields for the profile settings.
     *
     * This method checks if the username, email, contact number, and company fields are filled in.
     * It also validates the email format and the length of the contact number.
     *
     * @return true if all fields are valid, false otherwise.
     */
    private boolean validateProfileFields() {
        String newUsername = editUsername.getText().toString();
        String newUserEmail = editUserEmail.getText().toString();
        String newUserContactNumber = editUserContactNumber.getText().toString();
        String newUserCompany = editUserCompany.getText().toString();

        if (newUsername.isEmpty() || newUserEmail.isEmpty() || newUserContactNumber.isEmpty() || newUserCompany.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!newUserEmail.contains("@") || !newUserEmail.contains(".")) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (newUserContactNumber.length() != 10) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }



    /**
     * Saves the profile picture to Firebase Storage.
     */
    private void saveProfilePicture() {

        if (imageUri == null) {
            Toast.makeText(this, "Please upload a picture first", Toast.LENGTH_SHORT).show();
            return;
        }
        String filename = UUID.randomUUID().toString() + ".png";
        StorageReference fileRef = mStorageRef.child(filename);

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    imageUrl = uri.toString();
                    profilePicUrl = imageUrl;
                    String userId = currentUser.getUid();
                    db.collection("Users").document(userId)
                            .update("profilePic", imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Handle success
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                            });

                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }


    /**
     * Called when an activity you launched exits, giving the requestCode it started with, the resultCode it returned, and any additional data from it.
     * @param requestCode The integer request code originally supplied to startActivityForResult().
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            saveProfilePicture();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
