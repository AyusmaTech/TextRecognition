package com.ayusma.textrecognition;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ayusma.textrecognition.Helper.SQLiteDatabaseHelper;
import com.ayusma.textrecognition.Helper.Saver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Operation {

    public static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
    private Context context;
    private SQLiteDatabaseHelper db;


    public Operation(Context context) {
        db = new SQLiteDatabaseHelper(context);
        this.context = context;
    }

    public void save(String text) {
        db.addText(text);
        Toast.makeText(context, "saved", Toast.LENGTH_SHORT).show();
    }

    public void delete(Saver saver) {
        db.delete(saver);
    }

    public String getSavedText(int id) {
        return db.getSavedText(id).getText();
    }

    public List<Saver> getAllSavedText() {
        return db.getAllSavedText();
    }

    public void copy(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "copied", Toast.LENGTH_SHORT).show();


    }

    public void share(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Text Recognized");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    public void export(String data, int id) {
        writeToFile(data, id);
    }

    private void writeToFile(String data, int id) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = id;
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            int num = 0;
            if (data.length() < 10) {
                num = 7;
            }
             else if (data.length() > 20){
                num = 15;
            }

            String title = data.substring(0, num);


            FileWriter out = null;
            try {
                out = new FileWriter(new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), title + ".txt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (out != null) {
                    out.write(data);
                    out.close();
                    Toast.makeText(context, R.string.exported_successfully, Toast.LENGTH_SHORT).show();

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
