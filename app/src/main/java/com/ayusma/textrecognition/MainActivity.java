package com.ayusma.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ayusma.textrecognition.Adapter.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import java.util.List;

import static com.ayusma.textrecognition.Operation.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textView;
    private FloatingActionButton fab_camera,fab_gallery;
    private Operation operation;
    private Context mContext;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        operation = new Operation(this);
        mContext=this;

        recyclerView = findViewById(R.id.recycler_view);
        textView = findViewById(R.id.no_saved_text);
        fab_camera = findViewById(R.id.camera_floatingActionButton);
        fab_gallery = findViewById(R.id.gallery_floatingActionButton);

        fab_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });
        fab_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImagePicker.Builder(MainActivity.this)
                        .mode(ImagePicker.Mode.GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.NONE)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .allowMultipleImages(false)
                        .build();
            }
        });


        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(operation.getAllSavedText());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (recyclerViewAdapter.getItemCount() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setAdapter(recyclerViewAdapter);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.processing);
        builder.setView(R.layout.loading_dialoag);
        dialog = builder.create();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                operation.export(operation.getSavedText(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE), 0);

            } else {
                Toast.makeText(this, R.string.enable_text_permission, Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            String path = mPaths.get(0);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(mPaths.get(0),opts);
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
            imageProcessor(image);
            dialog.show();
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
                        Intent intent = new Intent(MainActivity.this, TextActivity.class);
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

                        builder.setPositiveButton(R.string.ok, null).show();
                    }
                });

    }
}
