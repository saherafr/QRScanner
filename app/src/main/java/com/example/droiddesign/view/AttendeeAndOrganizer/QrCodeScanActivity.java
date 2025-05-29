package com.example.droiddesign.view.AttendeeAndOrganizer;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import androidx.appcompat.app.AlertDialog;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.UploadQR;
import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.example.droiddesign.model.AttendanceDB;
import com.example.droiddesign.model.QRcode;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.util.UUID;

/**
 * An activity that handles QR code scanning using the camera.
 * This activity requests camera permissions, handles user permissions responses,
 * and processes the QR code scanning results.
 */
public class QrCodeScanActivity extends AppCompatActivity {

    /**
     * Firebase Storage reference for storing QR code images.
     */
    private StorageReference mStorageRef;

    /**
     * Firestore database instance for saving QR code metadata.
     */
    private FirebaseFirestore mFirestoreDb;

    /**
     * Binding instance for the activity_qr_code_scan layout.
     *
     *
     */
    private ActivityQrCodeScanBinding binding;

    private AttendanceDB attendanceDB;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean isCheckedIn = false;

    /**
     * Initializes the activity and view binding.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();

        mStorageRef = FirebaseStorage.getInstance().getReference("qrcodes");
        mFirestoreDb = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        attendanceDB = new AttendanceDB();
        ImageButton backButton = findViewById(R.id.button_back2);
        backButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String origin = intent.getStringExtra("ORIGIN");

        if ("AddEventSecondActivity".equals(origin)) {
            TextView header = findViewById(R.id.header);
            header.setText("Recycle QRs!");
        }
    }

    /**
     * An activity result launcher for handling the permission request result for using the camera.
     * It shows the camera to the user if the permission is granted.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    //show why user needs permission
                }
            });

    /**
     * An activity result launcher for the QR code scanning activity.
     * It processes the scanning result and passes it to the next activity.
     */
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            String sanitizedContents = result.getContents().replaceAll("^https?://", "");
            setResult(sanitizedContents);
        }
    });

    /**
     * Processes the QR code result and starts the EventDetailsActivity with the scanned event ID.
     *
     * @param contents The scanned content from the QR code.
     */

    private void setResult(String contents) {

        Intent returnIntent = new Intent();
        if ("AddEventSecondActivity".equals(getIntent().getStringExtra("ORIGIN"))) {
            String eventId = getIntent().getStringExtra("EVENT_ID"); // Retrieve the event ID passed from AddEventSecondActivity

            // Generate a new check-in QR code
            QRcode checkInQrCode = new QRcode(eventId, "check_in");
            String qrId = UUID.randomUUID().toString(); // Generate a unique ID for this QR code
            String type = "check_in"; // Assuming you want to store this as a check-in type

            uploadQrCode(qrId, eventId, type, contents, new OnQrCodeUploadListener() {
                @Override
                public void onQrCodeUploadSuccess(String qrUrl) {
                    // Send back the new check-in QR code ID and the original QR code URL
                    returnIntent.putExtra("checkInQrId", qrId);
                    returnIntent.putExtra("checkInQrUrl", qrUrl); // Use the contents as the QR URL
                    setResult(Activity.RESULT_OK, returnIntent);
                    Toast.makeText(QrCodeScanActivity.this, "QR code recycled!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onQrCodeUploadFailure(String errorMessage) {
                    Toast.makeText(QrCodeScanActivity.this, "Upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference qrCodeRef = db.collection("qrcodes").document(contents);

            qrCodeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot qrCodeDocument = task.getResult();
                    String eventId = qrCodeDocument.getString("eventId");
                    String type = qrCodeDocument.getString("type");

                    if (eventId != null && type != null) {
                        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        intent.putExtra("ORIGIN", "QrCodeScanActivity");

                        if ("check_in".equals(type)) {
                            checkInUser(eventId);
                            startActivity(intent);
                            finish();
                        } else if ("share".equals(type)) {
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e("QrCodeScanActivity", "Event ID or Type not found in QR code document.");
                    }
                } else {
                    // No document found by ID, try to find by qrurl
                    searchByQRUrl(contents);
                }
            });
        }

    }

    /**
     * Checks in the user for the specified event.
     * If geolocation is enabled for the user, it fetches the current location and performs check-in with location data.
     * Otherwise, it performs a check-in without location data.
     *
     * @param eventId The ID of the event for which the user is checking in.
     */
    private void checkInUser(String eventId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch the user's geolocation setting from Firestore
        mFirestoreDb.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocation");
                        if (Boolean.TRUE.equals(geolocationEnabled)) {
                            getCurrentLocation((latitude, longitude) -> {
                                attendanceDB.checkInUser(eventId, userId, latitude, longitude);
                                Toast.makeText(QrCodeScanActivity.this, "Checked in successfully with location", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            attendanceDB.checkInUser(eventId, userId, null, null);
                            Toast.makeText(QrCodeScanActivity.this, "Checked in successfully without location", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("checkInUser", "User document does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e("checkInUser", "Error fetching user geolocation setting", e));
    }

    /**
     * Searches for a QR code URL in the Firestore database and processes the check-in if found.
     * If a matching QR code is found, it initiates the user check-in process and navigates to the event details.
     *
     * @param qrUrl The URL to search for in the QR codes collection.
     */
    private void searchByQRUrl(String qrUrl) {

        String sanitizedUrl = qrUrl.replaceAll("^https?://", "");
        Log.d("QrCodeScanActivity", "Searching for QR URL: " + qrUrl);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("qrcodes").whereEqualTo("qrUrl", sanitizedUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String eventId = document.getString("eventId");
                    if (eventId != null) {
                        checkInUser(eventId);
                        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        intent.putExtra("ORIGIN", "QrCodeScanActivity");
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
            }
            Toast.makeText(QrCodeScanActivity.this, "No matching QR code found.", Toast.LENGTH_SHORT).show();
        });
    }


    /**
     * Uploads the QR code data to Firestore and invokes the appropriate callback based on the operation's success or failure.
     *
     * @param qrId The unique identifier for the QR code.
     * @param eventId The event ID associated with the QR code.
     * @param type The type of QR code (e.g., "check_in" or "share").
     * @param contents The actual content or URL embedded in the QR code.
     * @param listener The listener to receive callback notifications.
     */
    private void uploadQrCode(String qrId, String eventId, String type, String contents, final OnQrCodeUploadListener listener) {
        // Sanitize the URL before storing it in Firestore
        String sanitizedUrl = contents.replaceAll("^https?://", "");
        UploadQR upload = new UploadQR(sanitizedUrl, eventId, type);

        mFirestoreDb.collection("qrcodes").document(qrId).set(upload)
                .addOnSuccessListener(aVoid -> listener.onQrCodeUploadSuccess(sanitizedUrl))  // Pass the sanitized URL
                .addOnFailureListener(e -> listener.onQrCodeUploadFailure(e.getMessage()));
    }


    /**
     * Listener interface for QR code upload operations, providing success and failure callback methods.
     */
    public interface OnQrCodeUploadListener {
        void onQrCodeUploadSuccess(String qrUrl);
        void onQrCodeUploadFailure(String errorMessage);
    }


    /**
     * Fetches the current location of the device and passes the coordinates to the provided callback.
     * Location updates are requested with high accuracy and are stopped as soon as the first location result is obtained.
     *
     * @param callback The callback to be invoked with the obtained location.
     */
    private void getCurrentLocation(MyLocationCallback callback) {

        if (isCheckedIn) {
            return;  // If already checked in, do not proceed
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions and then call getCurrentLocation again if permission granted
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 10000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            double latitude = locationResult.getLastLocation().getLatitude();
                            double longitude = locationResult.getLastLocation().getLongitude();

                            fusedLocationClient.removeLocationUpdates(this);

                            if (!isCheckedIn) {
                                isCheckedIn = true;
                                callback.onLocationObtained(latitude, longitude);
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }


    /**
     * Callback interface for obtaining the latitude and longitude from the current location.
     */
    private interface MyLocationCallback {
        void onLocationObtained(double latitude, double longitude);
    }



    /**
     * Initializes the QR code scanner options and starts the camera.
     */
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }



    /**
     * Initializes the view components and sets up the camera permission check.
     */
    private void initViews() {
        Button myButton = findViewById(R.id.scan_button);
        myButton.setOnClickListener(view -> {
            checkPermissionAndShowActivity(this);
        });
    }

    /**
     * Checks for camera permission. If not granted, it requests permission. Otherwise, it shows the camera.
     *
     * @param context The context in which the activity is running.
     */
    private void checkPermissionAndShowActivity(Context context) {
        String origin = getIntent().getStringExtra("ORIGIN");
        boolean isFromAddEventSecondActivity = "AddEventSecondActivity".equals(origin);
        SharedPreferenceHelper preferenceHelper = new SharedPreferenceHelper(QrCodeScanActivity.this);
        String currentUserId = preferenceHelper.getUserId();

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (!isFromAddEventSecondActivity) {
                mFirestoreDb.collection("Users").document(currentUserId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Boolean geolocationEnabled = documentSnapshot.getBoolean("geolocation");
                            if (geolocationEnabled != null && geolocationEnabled) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // Request location permission
                                    requestLocationPermission();
                                } else {
                                    showCamera();
                                }
                            } else {
                                showCamera();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("QrCodeScanActivity", "Error fetching user geolocation setting", e);
                            showCamera();  // Proceed with showing the camera if there's an error fetching the setting
                        });
            } else {
                showCamera();
            }
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * An ActivityResultLauncher to handle the result of location permission request.
     * It triggers an action based on whether the location permission is granted or not.
     * If the permission is granted, it proceeds with operations that require location access.
     * If the permission is denied, it displays an AlertDialog informing the user of the importance
     * of location permission and provides a way to navigate to the app's settings to enable it.
     */
    private final ActivityResultLauncher<String> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Location permission granted, proceed with the operation that requires location
                } else {
                    // Inform the user that location permission is needed
                    new AlertDialog.Builder(this)
                            .setTitle("Location Permission Denied")
                            .setMessage("Location permission is needed for accurate check-ins. Please consider enabling it in your app settings.")
                            .setPositiveButton("Go to Settings", (dialogInterface, i) -> {
                                // Intent to open app settings for location permission
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                            })
                            .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                            .create()
                            .show();
                }
            });

    /**
     * Requests location permission from the user.
     * If the permission is not already granted, this method triggers a prompt asking the user for location access.
     * The result of this request is handled by the {@code requestLocationPermissionLauncher} ActivityResultLauncher.
     */
    private void requestLocationPermission() {
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Initializes the data binding for this activity.
     */
    private void initBinding() {
        binding = ActivityQrCodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
