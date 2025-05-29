package com.example.droiddesign.view.Admin;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.ImageItem;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Adapters.ImagesAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity is used to browse and delete images from the database.
 * The user can select the type of images to browse and delete them.
 */
public class BrowseImagesActivity extends AppCompatActivity {
	private ImagesAdapter imagesAdapter;
	private List<ImageItem> imageItemList;
	private FirebaseFirestore firestore;
	private Spinner imageTypeSpinner;
	String selection;

	/**
	 * This method is called when the activity is created.
	 * It initializes the activity layout and loads the images from the database.
	 * @param savedInstanceState The saved instance state of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_images);

		try {
			firestore = FirebaseFirestore.getInstance();
			imageItemList = new ArrayList<>();
			RecyclerView imagesRecyclerView = findViewById(R.id.images_recyclerview);
			imageTypeSpinner = findViewById(R.id.image_type_spinner);

			imagesAdapter = new ImagesAdapter(imageItemList);
			int numberOfColumns = 3; // Adjust the number of columns as needed
			imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
			imagesRecyclerView.setAdapter(imagesAdapter);

			setupSpinner();

			findViewById(R.id.button_back).setOnClickListener(v -> finish());
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This method is called when the activity is resumed.
	 * It sets up the spinner to select the type of images to browse.
	 */
	private void setupSpinner() {
		try {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.image_types, R.layout.spinner_dropdown_item);
			adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
			imageTypeSpinner.setAdapter(adapter);
			imageTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					selection = parent.getItemAtPosition(position).toString();
					if ("User Profile Pics".equals(selection)) {
						loadUserProfileImages();
					} else if ("Event Posters".equals(selection)) {
						loadEventPosters();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while setting up the spinner: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This method is called when the activity is resumed.
	 * It loads the user profile images from the database.
	 */
	private void loadUserProfileImages() {
		try {
			firestore.collection("Users")
					.get()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							imageItemList.clear();
							for (DocumentSnapshot document : task.getResult()) {
								User user = document.toObject(User.class);
								String profilePic = user.getProfilePic();
								if (profilePic != null &&
										!profilePic.startsWith("https://ui-avatars.com") &&
										!profilePic.startsWith("https://robohash.org/")) {
									imageItemList.add(new ImageItem(profilePic, user.getUserId()));
								}
							}
							imagesAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(BrowseImagesActivity.this, "Failed to load user images.", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while loading user profile images: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}


	/**
	 * This method is called when the activity is resumed.
	 * It loads the event posters from the database.
	 */
	private void loadEventPosters() {
		try {
			firestore.collection("EventsDB")
					.get()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							imageItemList.clear();
							for (DocumentSnapshot document : task.getResult()) {
								String imageUrl = document.getString("imagePosterId");
								if (imageUrl != null) {
									imageItemList.add(new ImageItem(imageUrl, document.getString("eventId")));
								}
							}
							imagesAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(BrowseImagesActivity.this, "Failed to load event posters.", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while loading event posters: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * This method is called when the user clicks on an image.
	 * It shows a dialog with the image and the option to delete it.
	 * @param imageItem The image item that was clicked.
	 */
	public void showImageDialog(ImageItem imageItem) {
		try {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_image_preview);

			if (dialog.getWindow() != null) {
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			}

			ImageView imageView = dialog.findViewById(R.id.dialog_imageview);
			Button deleteButton = dialog.findViewById(R.id.dialog_delete_button);
			Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

			Glide.with(this).load(imageItem.getImageUrl()).override(150, 150).into(imageView);

			deleteButton.setOnClickListener(v -> {
				deleteImage(imageItem);
				imagesAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}); cancelButton.setOnClickListener(v -> dialog.dismiss());

				dialog.show();
			} catch (Exception e) {
				Toast.makeText(this, "An error occurred while showing image dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * This method is called when the user clicks on the delete button in the dialog.
		 * It deletes the image from the database.
		 * @param imageItem The image item to be deleted.
		 */
		private void deleteImage(ImageItem imageItem) {
			try {
				if ("User Profile Pics".equals(selection)) {
					// Delete user profile picture
					firestore.collection("Users")
							.document(imageItem.getOwnerId())
							.update("profilePic", null)
							.addOnSuccessListener(aVoid -> {
								Toast.makeText(BrowseImagesActivity.this, "Profile picture removed successfully.", Toast.LENGTH_SHORT).show();
								loadUserProfileImages();
							})
							.addOnFailureListener(e -> {
								Toast.makeText(BrowseImagesActivity.this, "Failed to remove profile picture.", Toast.LENGTH_SHORT).show();
							});
				} else if ("Event Posters".equals(selection)) {
					// Delete event poster
					firestore.collection("EventsDB")
							.document(imageItem.getOwnerId())
							.update("imagePosterId", null)
							.addOnSuccessListener(aVoid -> {
								Toast.makeText(BrowseImagesActivity.this, "Event poster removed successfully.", Toast.LENGTH_SHORT).show();
								loadEventPosters();
							})
							.addOnFailureListener(e -> {
								Toast.makeText(BrowseImagesActivity.this, "Failed to remove event poster.", Toast.LENGTH_SHORT).show();
							});
				}
			} catch (Exception e) {
				Toast.makeText(this, "An error occurred while deleting the image: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}	}
