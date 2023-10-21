package com.max.whoiswho;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class TitleActivity extends AppCompatActivity {

    private static List<MediaPlayer> mediaPlayers = new ArrayList<>();
    private static int currentSongIndex = 0;

    private static final int FADE_DURATION = 2000;  // Duración del fade-in en milisegundos
    private float volume = 0.0f;  // Volumen inicial
    private final float volumeStep = 0.05f;  // Cantidad que se aumentará el volumen en cada paso
    private final Handler fadeInHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        ImageView backgroundGif = findViewById(R.id.background_gif);
        Glide.with(this).load(R.drawable.fondo_futurista).into(backgroundGif);

        // Si la lista de mediaPlayers está vacía, la llenamos
        if (mediaPlayers.isEmpty()) {
            mediaPlayers.add(MediaPlayer.create(this, R.raw.piano_bso));
            mediaPlayers.add(MediaPlayer.create(this, R.raw.lone_wolf));
            mediaPlayers.add(MediaPlayer.create(this, R.raw.mystery_unfold));
            mediaPlayers.add(MediaPlayer.create(this, R.raw.mysterious_forest));
            mediaPlayers.add(MediaPlayer.create(this, R.raw.comedy_detective));
            for (MediaPlayer mp : mediaPlayers) {
                mp.setLooping(true);
            }
        }

        // Si el mediaPlayer actual no está en reproducción, lo iniciamos
        if (!mediaPlayers.get(currentSongIndex).isPlaying()) {
            volume = 0.75f;  // Ajustar el volumen inicial al 75%
            mediaPlayers.get(currentSongIndex).setVolume(volume, volume);
            new Handler().postDelayed(() -> {
                mediaPlayers.get(currentSongIndex).start();
                fadeInHandler.post(fadeInRunnable);
            }, 500);  // Retraso de medio segundo
        }

        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> {
            // Iniciar la actividad del menú principal
            startActivity(new Intent(TitleActivity.this, MainMenuActivity.class));
            finish();  // Cierra la actividad actual después de iniciar la siguiente
        });
    }

    private final Runnable fadeInRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (mediaPlayers.get(currentSongIndex) != null && volume < 1.0f) {
                    volume += volumeStep;
                    mediaPlayers.get(currentSongIndex).setVolume(volume, volume);
                    fadeInHandler.postDelayed(this, FADE_DURATION / (int) (1.0f / volumeStep));
                }
            } catch (Exception e) {
                Log.e("TitleActivity", "Error adjusting volume for mediaPlayer", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TitleActivity", "onDestroy called");
        fadeInHandler.removeCallbacks(fadeInRunnable);
        // No liberamos el mediaPlayer aquí porque queremos que la música continúe reproduciéndose en las siguientes actividades
    }

    public static void playMusic() {
        if (!mediaPlayers.isEmpty() && !mediaPlayers.get(currentSongIndex).isPlaying()) {
            mediaPlayers.get(currentSongIndex).start();
        }
    }

    public static void pauseMusic() {
        if (!mediaPlayers.isEmpty() && mediaPlayers.get(currentSongIndex).isPlaying()) {
            mediaPlayers.get(currentSongIndex).pause();
        }
    }

    public static boolean isMusicPlaying() {
        return !mediaPlayers.isEmpty() && mediaPlayers.get(currentSongIndex).isPlaying();
    }

    public static void nextSong() {
        if (!mediaPlayers.isEmpty()) {
            mediaPlayers.get(currentSongIndex).stop();
            currentSongIndex = (currentSongIndex + 1) % mediaPlayers.size();
            mediaPlayers.get(currentSongIndex).start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TitleActivity", "onStart called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TitleActivity", "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TitleActivity", "onResume called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TitleActivity", "onStop called");
    }

}
