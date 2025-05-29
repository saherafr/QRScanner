package com.example.droiddesign.model;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.droiddesign.controller.UploadQR;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
/**
 * A class that generates and uploads QR codes to Firebase Storage and Firestore.
 */
public class QRcode {
    private String qrId, eventId, type, uri;
    // Bitmap object to hold the generated QR code image.
    private Bitmap mQrBitmap;

    /** Constructor for generating a share QR code in AddEvent when user chooses to scan or upload their own..
     * @param eventId The event ID to be encoded in the QR code.
     * @param qrId The QR code ID.
     * @param mQrBitmap The QR code image.
     */
    public QRcode(String eventId, String qrId, Bitmap mQrBitmap) {
        this.eventId = eventId;
        this.qrId = qrId;
        this.mQrBitmap = mQrBitmap;
        this.type = "share";
    }

    /** Constructor for generating a QR code.
     * @param eventId The event ID to be encoded in the QR code.
     * @param type The type of the QR code. Must be either 'share' or 'check_in'.
     */
    public QRcode(String eventId, String type) {
        this.eventId = eventId;
        this.type = type;

        if (!"share".equals(type) && !"check_in".equals(type)) {
            throw new IllegalArgumentException("Invalid type: " + type + ". Type must be either 'share' or 'check_in'.");
        }

        this.qrId = UUID.randomUUID().toString();

        // Generate the actual QR code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(qrId, BarcodeFormat.QR_CODE, 200, 200);
            BitMatrix trimmedMatrix = getTrimmedMatrix(bitMatrix);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            mQrBitmap = barcodeEncoder.createBitmap(trimmedMatrix);
        } catch (WriterException e) {
            Log.e("QrCodeGenerator", "Error generating QR code", e);
        }
    }

    /** Trims the white space around the QR code image.
     * @param bitMatrix The BitMatrix object representing the QR code image.
     * @return The trimmed BitMatrix object.
     */
    @NonNull
    private static BitMatrix getTrimmedMatrix(BitMatrix bitMatrix) {
        int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
        int startX = enclosingRectangle[0];
        int startY = enclosingRectangle[1];
        int width = enclosingRectangle[2];
        int height = enclosingRectangle[3];

        BitMatrix trimmedMatrix = new BitMatrix(width, height);
        for (int i = startX; i < startX + width; i++) {
            for (int j = startY; j < startY + height; j++) {
                if (bitMatrix.get(i, j)) {
                    trimmedMatrix.set(i - startX, j - startY);
                }
            }
        }
        return trimmedMatrix;
    }

    /** Uploads the QR code image to Firebase Storage and the metadata to Firestore.
     * @param listener The listener to be notified of the upload status.
     */
    public void upload(OnQrCodeUploadListener listener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mQrBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference qrCodeRef = storageRef.child(System.currentTimeMillis() + ".png");

        UploadTask uploadTask = qrCodeRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                this.uri = uri.toString();
                uploadMetadataToFirestore(uri.toString(), listener);
            }).addOnFailureListener(e -> {
                listener.onQrCodeUploadFailure(e.getMessage());
            });
        }).addOnFailureListener(e -> {
            listener.onQrCodeUploadFailure(e.getMessage());
        });
    }

    /** Uploads the metadata of the QR code to Firestore.
     * @param downloadUrl The download URL of the QR code image.
     * @param listener The listener to be notified of the upload status.
     */
    private void uploadMetadataToFirestore(String downloadUrl, OnQrCodeUploadListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UploadQR upload = new UploadQR(downloadUrl, eventId, type);

        db.collection("qrcodes").document(qrId).set(upload)
                .addOnSuccessListener(aVoid -> listener.onQrCodeUploadSuccess())
                .addOnFailureListener(e -> listener.onQrCodeUploadFailure(e.getMessage()));
    }


    /** Listener interface for the QR code upload process. */
    public interface OnQrCodeUploadListener {
        void onQrCodeUploadSuccess();

        void onQrCodeUploadFailure(String errorMessage);
    }

    /** Gets the QR code image.
     * @return The QR code image.
     */
    public Bitmap getmQrBitmap() {
        return this.mQrBitmap;
    }

    /** Sets the QR code image.
     * @param mQrBitmap The QR code image.
     */
    public void setmQrBitmap(Bitmap mQrBitmap) {
        this.mQrBitmap = mQrBitmap;
    }

    /** Gets the QR code ID.
     * @return The QR code ID.
     */
    public String getQrId() {
        return qrId;
    }

    /** Sets the QR code ID.
     * @param qrId The QR code ID.
     */
    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    /** Gets the event ID of the QR code.
     * @return The event ID of the QR code.
     */
    public String getEventId() {
        return eventId;
    }

    /** Sets the event ID of the QR code.
     * @param eventId The event ID to be encoded in the QR code.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /** Gets the type of the QR code.
     * @return The type of the QR code.
     */
    public String getType() {
        return type;
    }

    /** Gets the URI of the QR code.
     * @return The URI of the QR code.
     */
    public String getUri() {
        return uri;
    }

    /** Sets the URI of the QR code.
     * @param uri The URI of the QR code.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /** Sets the type of the QR code.
     * @param type The type of the QR code. Must be either 'share' or 'check_in'.
     */
    public void setType(String type) {
        this.type = type;

        if (!"share".equals(type) && !"check_in".equals(type)) {
            throw new IllegalArgumentException("Invalid type: " + type + ". Type must be either 'share' or 'check_in'.");
        }
    }
}