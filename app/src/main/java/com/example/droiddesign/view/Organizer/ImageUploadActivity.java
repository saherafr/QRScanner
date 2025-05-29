package com.example.droiddesign.view.Organizer;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.droiddesign.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.controller.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

/**
 * An activity for uploading images to Firebase Storage.
 */
public class ImageUploadActivity extends AppCompatActivity {

    /**
     * Request code for picking an image from the device.
     */
    private static final int PICK_IMAGE_REQUEST = 1;

    /**
     * Button for choosing an image from the device.
     */
    private Button mButtonChooseImage;

    /**
     * Button for uploading the selected image.
     */
    private Button mButtonUpload;

    /**
     * EditText for entering the file name.
     */
    private EditText mEditTextFileName;

    /**
     * ImageView for previewing the selected image.
     */
    private ImageView mImageView;

    /**
     * ProgressBar for showing the upload progress of the image.
     */
    private ProgressBar mProgressBar;

    /**
     * Reference to Firebase Storage.
     */
    private StorageReference mStorageRef;

    /**
     * Reference to Firestore database.
     */
    private FirebaseFirestore mFirestoreDb;

    /**
     * Task for handling image upload to Firebase Storage.
     */
    private StorageTask mUploadTask;

    /**
     * URI of the selected image.
     */
    private Uri mImageUri;


    /**
     * Called when the activity is created.
     * @param savedInstanceState The saved instance state Bundle.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mImageView = findViewById(R.id.image_preview);
        mProgressBar = findViewById(R.id.progress_bar);
        Button buttonBack = findViewById(R.id.button_back_upload);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mFirestoreDb = FirebaseFirestore.getInstance(); // Initialize Firestore

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ImageUploadActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });


        buttonBack.setOnClickListener(v -> finish());
    }

    /**
     * Gets the file extension from the given URI.
     * @param uri The URI of the file.
     * @return The file extension.
     */

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    /**
     * Uploads the chosen image to Firebase Storage.
     */

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> mProgressBar.setProgress(0), 500);

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload("", uri.toString());
                                    String uploadId = mFirestoreDb.collection("uploads").document().getId();
                                    mFirestoreDb.collection("uploads").document(uploadId).set(upload)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(ImageUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("imagePosterUrl", uri.toString());
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ImageUploadActivity.this, "Upload failed", Toast.LENGTH_LONG).show());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setProgress(0);
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        mProgressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Opens a file chooser to select an image.
     */

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Called when an activity launched by this activity exits, giving the requestCode and resultCode.
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }
}
