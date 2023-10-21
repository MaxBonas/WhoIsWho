package com.max.whoiswho;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    Character[] characters = CharacterRepository.getAllCharacters();

    Character chosenCharacter;

    Spinner questionsSpinner;
    Button askButton;
    TextView answerTextView;
    Button guessButton;
    TextView timerTextView;
    TextView scoreTextView;
    int score = 100;

    RecyclerView characterRecyclerView;
    CharacterAdapter characterAdapter;
    long gameDuration;
    private CountDownTimer gameTimer;
    // Añade esto al inicio de MainActivity
    private ImageView floatingImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        characters = CharacterRepository.getAllCharacters();

        int characterCount = getIntent().getIntExtra("CHARACTER_COUNT", 10); // 10 por defecto
        characters = getRandomCharacters(characterCount);

        String difficulty = getIntent().getStringExtra("DIFFICULTY");

        if (difficulty == null) {
            difficulty = "facil";  // asignar un valor predeterminado
        }

        switch (difficulty) {
            case "facil":
                gameDuration = 2 * 60 * 1000;  // 2 minutos
                break;
            case "normal":
                gameDuration = (2 * 60 + 30) * 1000;  // 2,5 minutos
                break;
            case "dificil":
                gameDuration = 3 * 60 * 1000;  // 3 minutos
                break;
            default:
                gameDuration = 2 * 60 * 1000;  // Por defecto, 2 minutos
                break;
        }

        // Escoge un personaje al azar al inicio
        Random rand = new Random();
        chosenCharacter = characters[rand.nextInt(characters.length)];

        // Vincula los elementos de la UI
        ImageButton pausePlayButton = findViewById(R.id.pause_play_button);
        ImageButton nextSongButton = findViewById(R.id.next_song_button);
        questionsSpinner = findViewById(R.id.questions_spinner);
        askButton = findViewById(R.id.ask_button);
        answerTextView = findViewById(R.id.answer_textview);
        guessButton = findViewById(R.id.guess_button);
        timerTextView = findViewById(R.id.timer_textview);
        scoreTextView = findViewById(R.id.score_textview);

        pausePlayButton.setOnClickListener(v -> {
            if (TitleActivity.isMusicPlaying()) {
                TitleActivity.pauseMusic();
                pausePlayButton.setBackgroundResource(R.drawable.ic_media_play);  // Cambia a icono de play
            } else {
                TitleActivity.playMusic();
                pausePlayButton.setBackgroundResource(R.drawable.ic_media_pause); // Cambia a icono de pausa
            }
        });

        nextSongButton.setOnClickListener(v -> {
            TitleActivity.nextSong();
        });

        //imagen ampliada flotante
        floatingImageView = new ImageView(this);
        floatingImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        floatingImageView.setVisibility(View.GONE);
        floatingImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((FrameLayout) findViewById(R.id.container)).addView(floatingImageView);

        // Iniciar el temporizador
        gameTimer = new CountDownTimer(gameDuration, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                // Calcula el porcentaje de tiempo restante
                double percentageLeft = (double) millisUntilFinished / gameDuration;
                score = (int) (percentageLeft * 100);

                scoreTextView.setText("Puntuación: " + score);
            }

            public void onFinish() {
                timerTextView.setText("¡Tiempo agotado!");
                endGame("Has perdido!");
            }
        }.start();

        // Configura el comportamiento del botón "Preguntar"
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deshabilita el botón "Preguntar"
                askButton.setEnabled(false);

                int questionIndex = questionsSpinner.getSelectedItemPosition();
                boolean answer = askQuestion(chosenCharacter, questionIndex);
                answerTextView.setText(answer ? "Sí" : "No");
                answerTextView.setAlpha(0f);
                answerTextView.setVisibility(View.VISIBLE);
                answerTextView.animate()
                        .alpha(1f)
                        .setDuration(300)  // Duración del fade-in
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                answerTextView.animate()
                                        .alpha(0f)
                                        .setDuration(300)  // Duración del fade-out
                                        .setStartDelay(600)  // Duración de la pausa antes del fade-out
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                answerTextView.setVisibility(View.INVISIBLE);
                                                // Habilita el botón "Preguntar" nuevamente
                                                askButton.setEnabled(true);
                                            }
                                        });
                            }
                        });
            }
        });

        // Configura el comportamiento del botón "Adivinar"
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guessCharacter();
            }
        });

        // Inicializa y configura el RecyclerView
        characterRecyclerView = findViewById(R.id.character_recycler_view);
        characterAdapter = new CharacterAdapter(this, Arrays.asList(characters), floatingImageView);
        characterRecyclerView.setAdapter(characterAdapter);
        ScrollableGridLayoutManager gridLayoutManager = new ScrollableGridLayoutManager(this, 2); // 2 columnas
        characterRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private boolean askQuestion(Character character, int questionIndex) {
        switch (questionIndex) {
            case 0: return character.isHasGlasses();
            case 1: return character.isHasBeard();
            case 2: return character.isHasMustache();
            case 3: return character.isBald();
            case 4: return character.isWearsHat();
            case 5: return character.isWearsMakeup();
            case 6: return character.isHasPiercing();
            case 7: return character.isHasLongHair();
            case 8: return character.isHasFreckles();
            case 9: return character.isHasTattoos();
            case 10: return character.isWearsScarfOrBandana();
            default: return false;
        }
    }

    private void guessCharacter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adivina el personaje");

        View dialogView = getLayoutInflater().inflate(R.layout.guess_character_dialog, null);
        final Spinner characterGuessSpinner = dialogView.findViewById(R.id.character_guess_spinner);

        List<String> characterNames = new ArrayList<>();
        for (Character character : characters) {
            if (!character.isCrossedOut()) {
                characterNames.add(character.getName());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_text_item, characterNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        characterGuessSpinner.setAdapter(spinnerAdapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Adivinar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String guess = characterGuessSpinner.getSelectedItem().toString();
                if (guess.equalsIgnoreCase(chosenCharacter.getName())) {
                    int finalScore = score;
                    endGame("¡Has ganado! Tu puntuación es: " + finalScore);
                } else {
                    endGame("¡Has perdido!");
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public static class ScrollableGridLayoutManager extends GridLayoutManager {
        private boolean isScrollEnabled = true;

        public ScrollableGridLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }
    }


    private void endGame(String message) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(message);
            builder.setPositiveButton("Menú", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }


    private Character[] getRandomCharacters(int count) {
        Random rand = new Random();
        Character[] selectedCharacters = new Character[count];
        int selectedCount = 0;
        while (selectedCount < count) {
            Character character = characters[rand.nextInt(characters.length)];
            if (!characterInArray(character, selectedCharacters) && !hasDuplicateAttributes(character, selectedCharacters)) {
                selectedCharacters[selectedCount] = character;
                selectedCount++;
            }
        }
        return selectedCharacters;
    }

    private boolean characterInArray(Character character, Character[] array) {
        for (Character c : array) {
            if (c != null && c.getName().equals(character.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasDuplicateAttributes(Character character, Character[] array) {
        for (Character c : array) {
            if (c != null && character.hasSameAttributes(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MainActivity", "onPause called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy called");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de que quieres abandonar la partida?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (gameTimer != null) {
                    gameTimer.cancel();
                }
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    public void setRecyclerViewScrollEnabled(boolean enabled) {
        if (characterRecyclerView.getLayoutManager() instanceof ScrollableGridLayoutManager) {
            ScrollableGridLayoutManager gridLayoutManager = (ScrollableGridLayoutManager) characterRecyclerView.getLayoutManager();
            gridLayoutManager.setScrollEnabled(enabled);
        }
    }
}