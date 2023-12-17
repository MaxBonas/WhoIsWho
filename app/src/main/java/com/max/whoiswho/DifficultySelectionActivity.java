package com.max.whoiswho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DifficultySelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_selection);

        ImageView backgroundGif = findViewById(R.id.background_gif);
        Glide.with(this).load(R.drawable.fondo_futurista).into(backgroundGif);

        Button easyButton = findViewById(R.id.easy_button);
        Button normalButton = findViewById(R.id.normal_button);
        Button hardButton = findViewById(R.id.hard_button);
        Button veryHardButton = findViewById(R.id.very_hard_button);
        Button extremeButton = findViewById(R.id.extreme_button);

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(getString(R.string.easy));
            }
        });

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(getString(R.string.normal));
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(getString(R.string.hard));
            }
        });

        veryHardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(getString(R.string.very_hard));
            }
        });

        extremeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(getString(R.string.extreme));
            }
        });
    }

    private void startGame(String difficulty) {
        int characterCount;
        switch(difficulty) {
            case getString(R.string.easy):
                characterCount = 10;
                break;
            case getString(R.string.normal):
                characterCount = 20;
                break;
            case getString(R.string.hard):
                characterCount = 30;
                break;
            case getString(R.string.very_hard):
                characterCount = 30;
                break;
            case getString(R.string.extreme):
                characterCount = 40;
                break;
            default:
                characterCount = 10; // Por defecto
        }

        // Pasar la dificultad seleccionada y la cantidad de personajes a la actividad del juego.
        Intent intent = new Intent(DifficultySelectionActivity.this, MainActivity.class);
        intent.putExtra("CHARACTER_COUNT", characterCount);
        intent.putExtra("DIFFICULTY", normalizeDifficulty(difficulty));
        startActivity(intent);
    }

    private String normalizeDifficulty(String difficulty) {
        return difficulty.toLowerCase().replace("á", "a");
    }
}