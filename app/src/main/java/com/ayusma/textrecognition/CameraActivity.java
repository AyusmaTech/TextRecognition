package com.ayusma.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ayusma.textrecognition.Helper.SharedHelper;
import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import java.util.Objects;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraKitView cameraKitView;
    private ImageView btn_capture, btn_flashlight;
    private String flashMode = "";
    private AlertDialog dialog;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        mContext = this;

        cameraKitView = findViewById(R.id.camera);
        btn_capture = findViewById(R.id.btn_capture);
        btn_flashlight = findViewById(R.id.btn_flashlight);
        cameraKitView.requestPermissions(this);


        flashMode = SharedHelper.getKey(this, "flashMode");

        if (flashMode.isEmpty() || flashMode.equals("off")) {
            flashMode = "off";
            flash("off");
        }

        flash(flashMode);

        btn_flashlight.setOnClickListener(this);
        btn_capture.setOnClickListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.processing);
        builder.setView(R.layout.loading_dialoag);
        dialog = builder.create();


    }


    public void flash(String mode) {
        switch (mode) {
            case "off":
                cameraKitView.setFlash(CameraKit.FLASH_OFF);
                btn_flashlight.setImageDrawable(getDrawable(R.drawable.ic_flash_off_white_24dp));
                break;
            case "on":
                cameraKitView.setFlash(CameraKit.FLASH_ON);
                btn_flashlight.setImageDrawable(getDrawable(R.drawable.ic_flash_on_white_24dp));
                break;
            case "auto":
                cameraKitView.setFlash(CameraKit.FLASH_AUTO);
                btn_flashlight.setImageDrawable(getDrawable(R.drawable.ic_flash_auto_white_24dp));
                break;
        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "You must allow permission to use the camera", Toast.LENGTH_SHORT).show();
            finish();
        }

        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onClick(View view) {
        if (view == btn_capture) {
            cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                @Override
                public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                    // capturedImage contains the image from the CameraKitView.
                    captureSound();
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length, opts);
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    imageProcessor(image);
                    dialog.show();
                    cameraKitView.onStop();


                }
            });

        } else if (view == btn_flashlight) {
            switch (flashMode) {
                case "off":
                    flashMode = "on";
                    flash("on");
                    SharedHelper.putKey(this, "flashMode", "on");
                    break;
                case "on":
                    flashMode = "auto";
                    flash("auto");
                    SharedHelper.putKey(this, "flashMode", "auto");
                    break;
                case "auto":
                    flashMode = "off";
                    flash("off");
                    SharedHelper.putKey(this, "flashMode", "off");
                    break;
            }


        }
    }

    public void imageProcessor(FirebaseVisionImage image) {
        FirebaseVisionDocumentTextRecognizer detector = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();

        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionDocumentText>() {
                    @Override
                    public void onSuccess(FirebaseVisionDocumentText result) {
                        dialog.dismiss();
                        Intent intent = new Intent(CameraActivity.this, TextActivity.class);
                        intent.putExtra("result", result.getText());
                        startActivity(intent);
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("Processing Failed");
                        builder.setMessage(e.getLocalizedMessage());

                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cameraKitView.onStart();
                                cameraKitView.onResume();
                            }
                        }).show();
                    }
                });

    }

    public void captureSound(){
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch( audio.getRingerMode() ){
            case AudioManager.RINGER_MODE_NORMAL:
                MediaActionSound sound = new MediaActionSound();
                sound.play(MediaActionSound.SHUTTER_CLICK);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                break;
        }
    }
}
