package com.example.droiddesign.view.Organizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.UploadQR;
import com.example.droiddesign.model.QRcode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Activity for generating and uploading a QR code.
 * This activity allows users to generate a QR code based on an input string (event ID)
 * and upload the generated QR code image to Firebase Storage.
 */
public class QrCodeGeneratorActivity extends AppCompatActivity {

    /**
     * Firebase Storage reference for storing QR code images.
     */
    private StorageReference mStorageRef;

    /**
     * Firestore database instance for saving QR code metadata.
     */
    private FirebaseFirestore mFirestoreDb;

    /**
     * Bitmap object to hold the generated QR code image.
     */

    // QRcode objects to represent the share and check-in QR codes.
    private QRcode shareQrCode, checkInQrCode;
    private String shareQrUrl, checkInQrUrl;
    private int uploadCount = 0;

    /**
     * Initializes the activity, setting up UI components and button click listeners.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        Button buttonGenerate = findViewById(R.id.button_generate);
        ImageView mImageViewQrCode = findViewById(R.id.qr_code);
        Button mButtonSaveQrCode = findViewById(R.id.button_save_qr);
        Button buttonBack = findViewById(R.id.button_back);

        mStorageRef = FirebaseStorage.getInstance().getReference("qrcodes");
        mFirestoreDb = FirebaseFirestore.getInstance();

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String eventId = intent.getStringExtra("eventID");

                if (eventId == null || eventId.trim().isEmpty()) {
                    Toast.makeText(QrCodeGeneratorActivity.this, "Event ID is missing", Toast.LENGTH_SHORT).show();
                }

                shareQrCode = new QRcode(eventId, "share");
                checkInQrCode = new QRcode(eventId, "check_in");

                mImageViewQrCode.setImageBitmap(shareQrCode.getmQrBitmap());
            }
        });

        mButtonSaveQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareQrCode != null && checkInQrCode != null) {
                    if (shareQrCode.getmQrBitmap() != null && checkInQrCode.getmQrBitmap() != null) {
                        uploadQrCode(shareQrCode, new OnQrCodeUploadListener() {
                            @Override
                            public void onQrCodeUploadSuccess(String qrUrl) {
                                shareQrUrl = qrUrl;
                                uploadCount++;
                                checkUploadCompletion();
                            }

                            @Override
                            public void onQrCodeUploadFailure(String errorMessage) {
                                Toast.makeText(QrCodeGeneratorActivity.this, "Share QR Code upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Upload the check-in QR code
                        uploadQrCode(checkInQrCode, new OnQrCodeUploadListener() {
                            @Override
                            public void onQrCodeUploadSuccess(String qrUrl) {
                                checkInQrUrl = qrUrl;
                                uploadCount++;
                                checkUploadCompletion();
                            }

                            @Override
                            public void onQrCodeUploadFailure(String errorMessage) {
                                Toast.makeText(QrCodeGeneratorActivity.this, "Check-in QR Code upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(QrCodeGeneratorActivity.this, "No QR Code generated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(QrCodeGeneratorActivity.this, "No QR Code generated", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Checks if both QR codes have been uploaded successfully.
             * If so, sends the URLs of the uploaded QR codes back to the calling activity.
             */
            private void checkUploadCompletion() {
                if (uploadCount == 2) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("shareQrUrl", shareQrUrl);
                    resultIntent.putExtra("shareQrId", shareQrCode.getQrId());
                    resultIntent.putExtra("checkInQrUrl", checkInQrUrl);
                    resultIntent.putExtra("checkInId", checkInQrCode.getQrId());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    /**
     * Uploads the generated QR code image to Firebase Storage and saves its metadata to Firestore.
     * @param qrCode The QRcode to be uploaded to the firestore.
     */
    private void uploadQrCode(QRcode qrCode, final OnQrCodeUploadListener listener) {
        Bitmap bitmap = qrCode.getmQrBitmap();
        String qrId = qrCode.getQrId();
        String eventId = qrCode.getEventId();
        String type = qrCode.getType();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + ".png");

        fileRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UploadQR upload = new UploadQR(uri.toString(), eventId, type);
                                mFirestoreDb.collection("qrcodes").document(qrId).set(upload)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                listener.onQrCodeUploadSuccess(uri.toString());
                                                Toast.makeText(QrCodeGeneratorActivity.this,"QR code creation complete!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                listener.onQrCodeUploadFailure(e.getMessage());
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onQrCodeUploadFailure(e.getMessage());
                    }
                });
    }

    /**
     * Listener interface for handling QR code upload events.
     */
    private interface OnQrCodeUploadListener {
        void onQrCodeUploadSuccess(String qrUrl);
        void onQrCodeUploadFailure(String errorMessage);
    }
}