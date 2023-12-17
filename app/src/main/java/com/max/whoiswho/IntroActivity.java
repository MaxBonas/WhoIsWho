package com.max.whoiswho;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    private static final int TEXT_ANIMATION_DURATION = 50;  // Duración de cada letra

    private ImageView introImage;
    private TextView introText;

    private final int[] images = {
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3,
            R.drawable.intro4,
            R.drawable.intro5
    };

    private final String[] texts = {
            getString(R.string.intro_text_1)
            getString(R.string.intro_text_2)
            getString(R.string.intro_text_3)
            getString(R.string.intro_text_4)
            getString(R.string.intro_text_5)
    };

    private int currentIndex = 0;

    // Referencias para el Handler y el Runnable
    private Handler textHandler = new Handler();
    private Runnable textRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Iniciar el AudioManager
        AudioManager audioManager = new AudioManager(this);
        audioManager.startSpecificMusic(4);  // 4 es el índice de comedy_detective.mp3

        introImage = findViewById(R.id.introImage);
        introText = findViewById(R.id.introText);

        showTextWithAnimation();

        Button skipButton = findViewById(R.id.skipButton);
        skipButton.setOnClickListener(v -> {
            textHandler.removeCallbacks(textRunnable); // Cancelar el retraso

            startActivity(new Intent(IntroActivity.this, TitleActivity.class));
            finish();
        });
    }

    private void showTextWithAnimation() {
        if (currentIndex < texts.length) {
            introImage.setImageResource(images[currentIndex]);

            // Animación de zoom para la imagen
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(introImage, "scaleX", 1f, 1.1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(introImage, "scaleY", 1f, 1.1f);
            AnimatorSet scaleSet = new AnimatorSet();
            scaleSet.playTogether(scaleX, scaleY);
            scaleSet.setDuration((long) texts[currentIndex].length() * TEXT_ANIMATION_DURATION);

            // Mostrar el texto letra por letra
            textRunnable = new Runnable() {
                private int charCount = 0;

                @Override
                public void run() {
                    introText.setText(texts[currentIndex].substring(0, charCount++));

                    if (charCount <= texts[currentIndex].length()) {
                        textHandler.postDelayed(this, TEXT_ANIMATION_DURATION);
                    } else {
                        // Cambiar al siguiente texto e imagen después de un retraso
                        textHandler.postDelayed(() -> {
                            currentIndex++;
                            showTextWithAnimation();
                        }, 3000);
                    }
                }
            };

            textHandler.postDelayed(textRunnable, TEXT_ANIMATION_DURATION);

            scaleSet.start();

        } else {
            // Si todos los textos han sido mostrados, ir a TitleActivity
            startActivity(new Intent(IntroActivity.this, TitleActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textHandler.removeCallbacks(textRunnable); // Cancelar el retraso
        AudioManager.pauseMusic(); // Detener la música aquí
    }
}
