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
import android.content.Intent;
import android.view.View;
import android.widget.Button;


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
            "Como detective y fisonomista profesional, enfrento casos que desafían la lógica misma.",
            "Uno tras otro, distintos testigos ocupan esa silla, respondiendo a mis preguntas para desgranar el caso.",
            "A menudo, las unicas imágenes del círculo íntimo de la víctima se extraen de los rincones más oscuros de las redes sociales.",
            "Los testigos... siempre un enigma. Ofrecen pistas envueltas en sombras y percepciones. Es esencial actuar con cautela, evitando descartar sospechosos a la ligera.",
            "¡Comienza una nueva búsqueda! Adentrémonos en el misterio y descubramos... ¡Quién es!"
    };

    private int currentIndex = 0;
    private MediaPlayer mediaPlayer;

    // Referencias para el Handler y el Runnable
    private Handler textHandler = new Handler();
    private Runnable textRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Iniciar la música
        mediaPlayer = MediaPlayer.create(this, R.raw.comedy_detective);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.start();

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

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}