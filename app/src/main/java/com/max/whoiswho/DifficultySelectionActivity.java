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

        Button tutorialButton = findViewById(R.id.tutorial_button);
        Button easyButton = findViewById(R.id.easy_button);
        Button normalButton = findViewById(R.id.normal_button);
        Button hardButton = findViewById(R.id.hard_button);

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DifficultySelectionActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });
        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("Facil");
            }
        });

        normalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("Normal");
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame("Dificil");
            }
        });
    }

    private void startGame(String difficulty) {
        int characterCount;
        switch(difficulty) {
            case "Facil":
                characterCount = 10;
                break;
            case "Normal":
                characterCount = 20;
                break;
            case "Dificil":
                characterCount = 30;
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