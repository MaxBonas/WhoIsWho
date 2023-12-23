package com.max.FaceTrace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PvPSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp_selection);

        Button localPvPButton = findViewById(R.id.local_pvp_button);
        localPvPButton.setOnClickListener(v -> {
            startActivity(new Intent(PvPSelectionActivity.this, LocalPvPActivity.class));
        });

        Button onlinePvPButton = findViewById(R.id.online_pvp_button);
        onlinePvPButton.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.online_pvp_coming_soon), Toast.LENGTH_SHORT).show();
        });
    }
}
