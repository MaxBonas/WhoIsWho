package com.max.whoiswho;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class MainMenuActivity extends AppCompatActivity {

    private boolean isMusicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ImageView backgroundGif = findViewById(R.id.background_gif);
        Glide.with(this).load(R.drawable.fondo_futurista).into(backgroundGif);

        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
            // Iniciar la actividad de selección de dificultad
            startActivity(new Intent(MainMenuActivity.this, DifficultySelectionActivity.class));
        });

        Button musicButton = findViewById(R.id.music_button);
        musicButton.setOnClickListener(v -> {
            if (isMusicPlaying) {
                // Detener la música
                TitleActivity.pauseMusic();
                musicButton.setText("Música OFF");
            } else {
                // Reiniciar la música
                TitleActivity.playMusic();
                musicButton.setText("Música ON");
            }
            isMusicPlaying = !isMusicPlaying;  // Cambia el estado de la música
        });
    }
}
