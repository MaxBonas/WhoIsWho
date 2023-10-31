package com.max.whoiswho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LocalPvPActivity extends AppCompatActivity {

    Character[] characters = CharacterRepository.getAllCharacters();
    Character chosenCharacterP1, chosenCharacterP2;

    RecyclerView characterRecyclerView;
    CharacterAdapter characterAdapter;
    Button askButtonP1, askButtonP2;
    TextView answerTextViewP1, answerTextViewP2;
    Spinner questionsSpinnerP1, questionsSpinnerP2;
    ImageView floatingImageView;
    CountDownTimer timerP1, timerP2;
    List<Character> boardP1;
    List<Character> boardP2;
    String currentPlayer = "Player 1";
    // Agregar TextView para los temporizadores
    TextView timerTextViewP1, timerTextViewP2;
    TextView currentPlayerTextView;
    LinearLayout player1Container, player2Container;
    // Variables para rastrear si un jugador ya ha hecho una pregunta en el turno actual
    boolean hasAskedQuestionP1 = false;
    boolean hasAskedQuestionP2 = false;
    // Variable para guardar el último personaje que se cruzó
    Character lastCrossedOutCharacterP1 = null;
    Character lastCrossedOutCharacterP2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_pvp);

        characters = getRandomCharacters(30);

        boardP1 = new ArrayList<>(Arrays.asList(characters));
        boardP2 = new ArrayList<>(Arrays.asList(characters));
        characterAdapter = new CharacterAdapter(this, boardP1, boardP2, floatingImageView, true);


        characterRecyclerView = findViewById(R.id.character_recycler_view_pvp);
        floatingImageView = findViewById(R.id.floating_image_view_pvp);

        characterRecyclerView.setAdapter(characterAdapter);
        characterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        askButtonP1 = findViewById(R.id.ask_button_p1);
        askButtonP2 = findViewById(R.id.ask_button_p2);
        answerTextViewP1 = findViewById(R.id.answer_textview_p1);
        answerTextViewP2 = findViewById(R.id.answer_textview_p2);
        questionsSpinnerP1 = findViewById(R.id.questions_spinner_p1);
        questionsSpinnerP2 = findViewById(R.id.questions_spinner_p2);

        currentPlayerTextView = findViewById(R.id.current_player_textview);
        player1Container = findViewById(R.id.player1_container);
        player2Container = findViewById(R.id.player2_container);

        // Listeners for question buttons
        askButtonP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasAskedQuestionP1) {
                    int questionIndex = questionsSpinnerP1.getSelectedItemPosition();
                    boolean answer = QuestionManager.askQuestion(chosenCharacterP2, questionIndex);
                    answerTextViewP1.setText(answer ? "Sí" : "No");
                    updatePlayerBoard("Player 1", chosenCharacterP2.getName(), answer);
                    hasAskedQuestionP1 = true;
                    askButtonP1.setEnabled(false);
                }
            }
        });

        askButtonP2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasAskedQuestionP2) {
                    int questionIndex = questionsSpinnerP2.getSelectedItemPosition();
                    boolean answer = QuestionManager.askQuestion(chosenCharacterP1, questionIndex);
                    answerTextViewP2.setText(answer ? "Sí" : "No");
                    updatePlayerBoard("Player 2", chosenCharacterP1.getName(), answer);
                    hasAskedQuestionP2 = true;
                    askButtonP2.setEnabled(false);
                }
            }
        });

        chooseCharacterForP1();
        // Inicializar los TextView para los temporizadores
        timerTextViewP1 = findViewById(R.id.timer_textview_p1);
        timerTextViewP2 = findViewById(R.id.timer_textview_p2);
        // Inicializa el botón de fin de turno
        endTurnButton();
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

    private boolean hasDuplicateAttributes(Character character, Character[] array) {
        for (Character c : array) {
            if (c != null && character.hasSameAttributes(c)) {
                return true;
            }
        }
        return false;
    }
    private void chooseCharacterForP1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player 1: Escoge tu personaje");

        // Supongamos que tenemos un método que devuelve los nombres de los personajes en un array de strings
        String[] characterNames = getCharacterNames();
        builder.setItems(characterNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenCharacterP1 = characters[which];
                chooseCharacterForP2();
            }
        }).show();
    }

    private void chooseCharacterForP2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player 2: Escoge tu personaje");

        String[] characterNames = getCharacterNames();
        builder.setItems(characterNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chosenCharacterP2 = characters[which];
                startGame();
            }
        }).show();
    }

    private void startGame() {
        // Inicializar temporizadores aquí
        timerP1 = createTimer("Player 1");
        timerP2 = createTimer("Player 2");

        // Habilitar elementos de la UI aquí
        askButtonP1.setEnabled(true);
        questionsSpinnerP1.setEnabled(true);  // Habilitar spinner

        askButtonP2.setEnabled(false);  // Inicialmente deshabilitado
        questionsSpinnerP2.setEnabled(false);  // Inicialmente deshabilitado

        nextTurn("Player 1");
    }
    private CountDownTimer createTimer(String player) {
        return new CountDownTimer(2 * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Actualizar la UI para mostrar el tiempo restante
                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                if ("Player 1".equals(player)) {
                    timerTextViewP1.setText(timeLeft);
                } else {
                    timerTextViewP2.setText(timeLeft);
                }
            }

            public void onFinish() {
                endGame(player + " ha perdido");
            }
        };
    }
    private void switchTimers(String activePlayer) {
        if ("Player 1".equals(activePlayer)) {
            if (timerP2 != null) timerP2.cancel();
            if (timerP1 != null) timerP1.start();
        } else {
            if (timerP1 != null) timerP1.cancel();
            if (timerP2 != null) timerP2.start();
        }
    }
    // Añadir un botón para confirmar el fin del turno
    private void endTurnButton() {
        Button endTurnButton = findViewById(R.id.end_turn_button);
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextTurn(currentPlayer.equals("Player 1") ? "Player 2" : "Player 1");
            }
        });
    }

    private void nextTurn(String nextPlayer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Turno de " + nextPlayer);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTurn(nextPlayer);
            }
        });
        builder.setCancelable(false);
        builder.show();
        // Cambiar el jugador actual
        currentPlayer = nextPlayer;

        // Cambiar los temporizadores
        switchTimers(currentPlayer);
        // Reiniciar las variables de preguntas y habilitar los botones de nuevo
        hasAskedQuestionP1 = false;
        hasAskedQuestionP2 = false;
        askButtonP1.setEnabled(true);
        askButtonP2.setEnabled(true);
    }
    private void startTurn(String player) {
        if ("Player 1".equals(player)) {
            askButtonP1.setEnabled(true);
            questionsSpinnerP1.setEnabled(true);
            player1Container.setVisibility(View.VISIBLE);  // Mostrar sección del Jugador 1

            askButtonP2.setEnabled(false);
            questionsSpinnerP2.setEnabled(false);
            player2Container.setVisibility(View.GONE);  // Ocultar sección del Jugador 2

        } else {
            askButtonP1.setEnabled(false);
            questionsSpinnerP1.setEnabled(false);
            player1Container.setVisibility(View.GONE);  // Ocultar sección del Jugador 1

            askButtonP2.setEnabled(true);
            questionsSpinnerP2.setEnabled(true);
            player2Container.setVisibility(View.VISIBLE);  // Mostrar sección del Jugador 2
        }

        // Actualizar el TextView de turno actual
        currentPlayerTextView.setText("Turno de " + player);

        // Verificar si hay un ganador después de cada turno
        if ("Player 1".equals(player)) {
            checkForWinner(chosenCharacterP2, "Player 1");
        } else {
            checkForWinner(chosenCharacterP1, "Player 2");
        }
    }

    // Método para verificar si alguno de los jugadores ha ganado
    private void checkForWinner(Character chosenCharacter, String player) {
        int numberOfUncrossed = characterAdapter.getNumberOfUncrossedCharacters();
        if (numberOfUncrossed == 1) {
            Character remainingCharacter = characterAdapter.getUncrossedCharacter();
            if (remainingCharacter != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("¿Crees que es " + remainingCharacter.getName() + "?");

                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (remainingCharacter.getName().equals(chosenCharacter.getName())) {
                            endGame(currentPlayer + " ha ganado");
                        } else {
                            endGame(currentPlayer + " ha perdido");
                        }
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restoreLastCrossedOut(player);
                    }
                });

                builder.setCancelable(false);
                builder.show();
            }
        }
    }

    private void restoreLastCrossedOut(String player) {
        if ("Player 1".equals(player) && lastCrossedOutCharacterP1 != null) {
            // Aquí restauras el último personaje tachado para el Jugador 1
            characterAdapter.restoreCharacterP1(lastCrossedOutCharacterP1.getName());
        } else if ("Player 2".equals(player) && lastCrossedOutCharacterP2 != null) {
            // Aquí restauras el último personaje tachado para el Jugador 2
            characterAdapter.restoreCharacterP2(lastCrossedOutCharacterP2.getName());
        }
    }
    private void updatePlayerBoard(String player, String characterName, boolean answer) {
        if ("Player 1".equals(player)) {
            lastCrossedOutCharacterP1 = characterAdapter.updatePlayerBoard(characterName, answer, "Player 1");
        } else {
            lastCrossedOutCharacterP2 = characterAdapter.updatePlayerBoard(characterName, answer, "Player 2");
        }
    }


    private boolean characterInArray(Character character, Character[] array) {
        for (Character c : array) {
            if (c != null && c.getName().equals(character.getName())) {
                return true;
            }
        }
        return false;
    }

    private String[] getCharacterNames() {
        // Supongamos que este método devuelve los nombres de todos los personajes disponibles
        String[] names = new String[characters.length];
        for (int i = 0; i < characters.length; i++) {
            names[i] = characters[i].getName();
        }
        return names;
    }

    private void endGame(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Juego terminado");
        builder.setMessage("El ganador es: " + winner);
        builder.setPositiveButton("Menú", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LocalPvPActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
    public void setRecyclerViewScrollEnabled(boolean enabled) {
        if (characterRecyclerView.getLayoutManager() instanceof MainActivity.ScrollableGridLayoutManager) {
            MainActivity.ScrollableGridLayoutManager gridLayoutManager = (MainActivity.ScrollableGridLayoutManager) characterRecyclerView.getLayoutManager();
            gridLayoutManager.setScrollEnabled(enabled);
        }
    }
}
