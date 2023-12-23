package com.max.FaceTrace;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MainMenuActivity extends AppCompatActivity {

    private boolean isMusicPlaying = true;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageView backgroundGif = findViewById(R.id.background_gif);
        Glide.with(this).load(R.drawable.fondo_futurista).into(backgroundGif);

        // Botón Tutorial
        Button tutorialButton = findViewById(R.id.tutorial_button);
        tutorialButton.setOnClickListener(v -> {
            startActivity(new Intent(MainMenuActivity.this, TutorialActivity.class));
        });

        // Botón Jugar vs IA
        Button playVsAIButton = findViewById(R.id.play_vs_ai_button);
        playVsAIButton.setOnClickListener(v -> {
            startActivity(new Intent(MainMenuActivity.this, DifficultySelectionActivity.class));
        });

        // Botón Jugar vs Player
        Button playVsPlayerButton = findViewById(R.id.play_vs_player_button);
        playVsPlayerButton.setOnClickListener(v -> {
            startActivity(new Intent(MainMenuActivity.this, PvPSelectionActivity.class));
        });

        // Botón Modo Historia (Próximamente)
        Button storyModeButton = findViewById(R.id.story_mode_button);
        storyModeButton.setOnClickListener(v -> {
            Toast.makeText(this, getString(R.string.story_mode_button), Toast.LENGTH_SHORT).show();
        });

        Button musicButton = findViewById(R.id.music_button);
        musicButton.setOnClickListener(v -> {
            if (isMusicPlaying) {
                // Detener la música
                AudioManager.pauseMusic();
                musicButton.setText(getString(R.string.music_off));
            } else {
                // Reiniciar la música
                AudioManager.playMusic();
                musicButton.setText(getString(R.string.music_on));
            }
            isMusicPlaying = !isMusicPlaying;  // Cambia el estado de la música
        });

        // Botón Puntuaciones
        Button scoresButton = findViewById(R.id.scores_button);
        scoresButton.setOnClickListener(v -> {
            startActivity(new Intent(MainMenuActivity.this, ScoresActivity.class));
        });
        // Botón Salir del juego
        Button exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.exit_game))
                    .setMessage(getString(R.string.confirm_exit))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        finishAffinity();  // Cierra todas las actividades y sale de la aplicación
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        });
    }
}
