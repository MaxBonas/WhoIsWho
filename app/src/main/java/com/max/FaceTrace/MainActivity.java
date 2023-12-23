package com.max.FaceTrace;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;

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
    GameTimer gameTimer;
    // Añade esto al inicio de MainActivity
    private ImageView floatingImageView;
    boolean gameHasEnded = false;
    String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerName = TitleActivity.getCurrentPlayerName(this);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String playerName = intent.getStringExtra("playerName");

        if (playerName == null) {
            playerName = getString(R.string.guest);  // O manejar este caso como mejor te parezca
        }
        characters = CharacterRepository.getAllCharacters();

        int characterCount = getIntent().getIntExtra("CHARACTER_COUNT", 10); // 10 por defecto
        if (characterCount <= 0) {
            Toast.makeText(this, getString(R.string.invalid_character_count), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        characters = getRandomCharacters(characterCount);

        String difficulty = getIntent().getStringExtra("DIFFICULTY");

        if (difficulty == null) {
            difficulty = getString(R.string.easy);  // asignar un valor predeterminado
        }

        if (difficulty.equals(getString(R.string.easy))) {
            gameDuration = 2 * 60 * 1000;  // 2 minutos
        } else if (difficulty.equals(getString(R.string.normal))) {
            gameDuration = (2 * 60 + 30) * 1000;  // 2,5 minutos
        } else if (difficulty.equals(getString(R.string.hard))) {
            gameDuration = 3 * 60 * 1000;  // 3 minutos
        } else if (difficulty.equals(getString(R.string.very_hard))) {
            gameDuration = 2 * 60 * 1000;  // 2 minutos
        } else if (difficulty.equals(getString(R.string.extreme))) {
            gameDuration = 2 * 60 * 1000;  // 2 minutos
        } else {
            gameDuration = 2 * 60 * 1000;  // Por defecto, 2 minutos
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
            if (AudioManager.isMusicPlaying()) {
                AudioManager.pauseMusic();
                pausePlayButton.setBackgroundResource(R.drawable.ic_media_play);  // Cambia a icono de play
            } else {
                AudioManager.playMusic();
                pausePlayButton.setBackgroundResource(R.drawable.ic_media_pause); // Cambia a icono de pausa
            }
        });

        nextSongButton.setOnClickListener(v -> {
            AudioManager.nextSong();
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
        gameTimer = new GameTimer(gameDuration, timerTextView, scoreTextView, this, this);
        gameTimer.start();  // Iniciar el temporizador

        // Configura el comportamiento del botón "Preguntar"
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Deshabilita el botón "Preguntar"
                askButton.setEnabled(false);

                int questionIndex = questionsSpinner.getSelectedItemPosition();
                boolean answer = QuestionManager.askQuestion(chosenCharacter, questionIndex);
                answerTextView.setText(answer ? getString(R.string.yes) : getString(R.string.no));
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
// Pasa dos listas de personajes idénticas al constructor de CharacterAdapter
        characterAdapter = new CharacterAdapter(this, Arrays.asList(characters), floatingImageView);
        characterRecyclerView.setAdapter(characterAdapter);
        ScrollableGridLayoutManager gridLayoutManager = new ScrollableGridLayoutManager(this, 2); // 2 columnas
        characterRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private void guessCharacter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.guess_the_character));

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
        builder.setPositiveButton(getString(R.string.guess), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String guess = characterGuessSpinner.getSelectedItem().toString();
                if (guess.equalsIgnoreCase(chosenCharacter.getName())) {
                    endGame(getString(R.string.you_win));
                } else {
                    endGame(getString(R.string.you_lose));
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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


    public void endGame(String message) {
        if (gameHasEnded) return;  // Si el juego ya ha terminado, no hacer nada

        gameHasEnded = true;  // Marcar el juego como terminado
        // Detener el temporizador
        gameTimer.stop();

        int finalScore = gameTimer.getScore();  // Obtener la puntuación final desde GameTimer

        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (message.equals(getString(R.string.you_lose))) {
                // Si el jugador ha perdido, muestra el personaje correcto y su imagen
                message += "\n" + getString(R.string.correct_character, chosenCharacter.getName());
                // Crear un ImageView para el diálogo
                ImageView characterImageView = new ImageView(this);
                characterImageView.setImageResource(chosenCharacter.getImagePath());
                characterImageView.setAdjustViewBounds(true);
                characterImageView.setMaxHeight(300);  // Establecer la altura máxima
                characterImageView.setMaxWidth(300);   // Establecer el ancho máximo

                builder.setView(characterImageView);
            } else {
                message += getString(R.string.your_score_is, finalScore); // Muestra la puntuación solo si el jugador gana
            }

            builder.setTitle(message);
            builder.setPositiveButton(getString(R.string.menu), new DialogInterface.OnClickListener() {
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String playerId = playerName;  // Reemplaza esto con el ID real del jugador
        String date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        String difficulty = getIntent().getStringExtra("DIFFICULTY");

        ScoreEntry scoreEntry = new ScoreEntry(date, finalScore, difficulty, playerId);
        databaseReference.child("scores").push().setValue(scoreEntry, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error != null) {
                    Log.e("MainActivity", "Error al guardar la puntuación", error.toException());
                    Toast.makeText(MainActivity.this, getString(R.string.error_saving_scores), Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d("MainActivity", "Guardando puntuación: " + score);
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
        builder.setMessage(getString(R.string.confirm_exit));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (gameTimer != null) {
                    gameTimer.stop();
                }
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), null);
        builder.show();
    }

    public void setRecyclerViewScrollEnabled(boolean enabled) {
        if (characterRecyclerView.getLayoutManager() instanceof ScrollableGridLayoutManager) {
            ScrollableGridLayoutManager gridLayoutManager = (ScrollableGridLayoutManager) characterRecyclerView.getLayoutManager();
            gridLayoutManager.setScrollEnabled(enabled);
        }
    }
}