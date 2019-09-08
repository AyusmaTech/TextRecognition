package com.ayusma.textrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TextActivity extends AppCompatActivity {
    private String result, preview;
    private EditText editText;
    private TextView textView;
    private Operation op;
    private int click = 0;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        op = new Operation(this);

        editText = findViewById(R.id.edit_text);
        textView = findViewById(R.id.text_view);

        result = getIntent().getStringExtra("result");
        preview = getIntent().getStringExtra("Text");
        if (preview != null) {
            result = preview;
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            textView.setText(result);
        }
        editText.setText(result);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        item = menu.findItem(R.id.item_save);
        if (preview != null) {
            inflater.inflate(R.menu.text_activity_menu_preview, menu);
        } else {
            inflater.inflate(R.menu.text_activity_menu, menu);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                if(click == 0){
                    op.save(editText.getText().toString());
                    item.setIcon(R.drawable.ic_save_grey_24dp);
                    click++;
                }

                break;
            case R.id.item_copy:
                op.copy(editText.getText().toString());


                break;
            case R.id.item_share:
                op.share(editText.getText().toString());
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (preview == null){
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition( R.anim.push_left_in,R.anim.push_left_out);
            finishAffinity();
        }

    }
}
