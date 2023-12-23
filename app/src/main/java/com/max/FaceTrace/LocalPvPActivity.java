package com.max.FaceTrace;

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
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LocalPvPActivity extends AppCompatActivity {

    Character[] characters = CharacterRepository.getAllCharacters();
    Character chosenCharacterP1, chosenCharacterP2;
    Button askButtonP1, askButtonP2;
    TextView answerTextViewP1, answerTextViewP2;
    Spinner questionsSpinnerP1, questionsSpinnerP2;
    ImageView floatingImageView;
    CountDownTimer timerP1, timerP2;
    List<Character> boardP1;
    List<Character> boardP2;
    String currentPlayer = getString(R.string.player_1);
    TextView timerTextViewP1, timerTextViewP2;
    TextView currentPlayerTextView;
    LinearLayout player1Container, player2Container;
    boolean hasAskedQuestionP1 = false;
    boolean hasAskedQuestionP2 = false;
    Character lastCrossedOutCharacterP1 = null;
    Character lastCrossedOutCharacterP2 = null;
    RecyclerView characterRecyclerViewP1, characterRecyclerViewP2;
    CharacterAdapter characterAdapterP1, characterAdapterP2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_pvp);

        characters = getRandomCharacters(30);

        boardP1 = new ArrayList<>();
        boardP2 = new ArrayList<>();

        for (Character character : characters) {
            boardP1.add(character.deepCopy());
            boardP2.add(character.deepCopy());
        }

        floatingImageView = new ImageView(this);
        floatingImageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        floatingImageView.setVisibility(View.GONE);
        floatingImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((FrameLayout) findViewById(R.id.container)).addView(floatingImageView);
        characterRecyclerViewP1 = findViewById(R.id.character_recycler_view_pvp);
        characterRecyclerViewP2 = findViewById(R.id.character_recycler_view_pvp_2);

        characterAdapterP1 = new CharacterAdapter(this, boardP1, floatingImageView);
        characterAdapterP2 = new CharacterAdapter(this, boardP2, floatingImageView);

        if (characterRecyclerViewP1 != null) {
            characterRecyclerViewP1.setAdapter(characterAdapterP1);
            MainActivity.ScrollableGridLayoutManager gridLayoutManager = new MainActivity.ScrollableGridLayoutManager(this, 2);
            characterRecyclerViewP1.setLayoutManager(gridLayoutManager);
        }

        if (characterRecyclerViewP2 != null) {
            characterRecyclerViewP2.setAdapter(characterAdapterP2);
            MainActivity.ScrollableGridLayoutManager gridLayoutManager = new MainActivity.ScrollableGridLayoutManager(this, 2);
            characterRecyclerViewP2.setLayoutManager(gridLayoutManager);
        }


        askButtonP1 = findViewById(R.id.ask_button_p1);
        askButtonP2 = findViewById(R.id.ask_button_p2);
        answerTextViewP1 = findViewById(R.id.answer_textview_p1);
        answerTextViewP2 = findViewById(R.id.answer_textview_p2);
        questionsSpinnerP1 = findViewById(R.id.questions_spinner_p1);
        questionsSpinnerP2 = findViewById(R.id.questions_spinner_p2);

        currentPlayerTextView = findViewById(R.id.current_player_textview);
        player1Container = findViewById(R.id.player1_container);
        player2Container = findViewById(R.id.player2_container);

        askButtonP1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasAskedQuestionP1) {
                    int questionIndex = questionsSpinnerP1.getSelectedItemPosition();
                    boolean answer = QuestionManager.askQuestion(chosenCharacterP2, questionIndex);
                    answerTextViewP1.setText(answer ? getString(R.string.yes) : getString(R.string.no));
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
                    answerTextViewP2.setText(answer ? getString(R.string.yes) : getString(R.string.no));
                    hasAskedQuestionP2 = true;
                    askButtonP2.setEnabled(false);
                }
            }
        });

        chooseCharacterForP1();
        timerTextViewP1 = findViewById(R.id.timer_textview_p1);
        timerTextViewP2 = findViewById(R.id.timer_textview_p2);
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
        builder.setTitle(R.string.choose_character_p1);

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
        builder.setTitle(R.string.choose_character_p2);

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
        timerP1 = createTimer(getString(R.string.player_1));
        timerP2 = createTimer(getString(R.string.player_2));

        askButtonP1.setEnabled(true);
        questionsSpinnerP1.setEnabled(true);

        askButtonP2.setEnabled(false);
        questionsSpinnerP2.setEnabled(false);

        nextTurn(getString(R.string.player_1));
    }
    private CountDownTimer createTimer(String player) {
        return new CountDownTimer(2 * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String timeLeft = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                if (getString(R.string.player_1).equals(player)) {
                    timerTextViewP1.setText(timeLeft);
                } else {
                    timerTextViewP2.setText(timeLeft);
                }
            }

            public void onFinish() {
                endGame(player + " " + getString(R.string.has_lost));
            }
        };
    }
    private void switchTimers(String activePlayer) {
        if (getString(R.string.player_1).equals(activePlayer)) {
            if (timerP2 != null) timerP2.cancel();
            if (timerP1 != null) timerP1.start();
        } else {
            if (timerP1 != null) timerP1.cancel();
            if (timerP2 != null) timerP2.start();
        }
    }
    private void endTurnButton() {
        Button endTurnButton = findViewById(R.id.end_turn_button);
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextTurn(currentPlayer.equals(getString(R.string.player_1)) ? getString(R.string.player_2) : getString(R.string.player_1));
            }
        });
    }

    private void nextTurn(String nextPlayer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.turn_of, nextPlayer);
        builder.setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startTurn(nextPlayer);
            }
        });
        builder.setCancelable(false);
        builder.show();
        currentPlayer = nextPlayer;

        switchTimers(currentPlayer);
        hasAskedQuestionP1 = false;
        hasAskedQuestionP2 = false;
        askButtonP1.setEnabled(true);
        askButtonP2.setEnabled(true);
    }
    private void startTurn(String player) {
        if (characterRecyclerViewP1 != null && characterRecyclerViewP2 != null) {
            if (getString(R.string.player_1).equals(player)) {
                characterRecyclerViewP1.setVisibility(View.VISIBLE);
                characterRecyclerViewP2.setVisibility(View.GONE);
            askButtonP1.setEnabled(true);
            questionsSpinnerP1.setEnabled(true);
            player1Container.setVisibility(View.VISIBLE);

            askButtonP2.setEnabled(false);
            questionsSpinnerP2.setEnabled(false);
            player2Container.setVisibility(View.GONE);

            } else {
                characterRecyclerViewP1.setVisibility(View.GONE);
                characterRecyclerViewP2.setVisibility(View.VISIBLE);
                askButtonP1.setEnabled(false);
                questionsSpinnerP1.setEnabled(false);
                player1Container.setVisibility(View.GONE);

                askButtonP2.setEnabled(true);
                questionsSpinnerP2.setEnabled(true);
                player2Container.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("LocalPvPActivity", "RecyclerViews no inicializados");
        }

        currentPlayerTextView.setText(getString(R.string.turn_of));

        if (getString(R.string.player_1).equals(player)) {
            checkForWinner(chosenCharacterP2, getString(R.string.player_1), characterAdapterP1);
        } else {
            checkForWinner(chosenCharacterP1, getString(R.string.player_2), characterAdapterP2);
        }
    }

    private void checkForWinner(Character chosenCharacter, String player, CharacterAdapter adapter) {
        int numberOfUncrossed = adapter.getNumberOfUncrossedCharacters();
        if (numberOfUncrossed == 1) {
            Character remainingCharacter = adapter.getUncrossedCharacter();
            promptGuess(remainingCharacter, chosenCharacter, player, adapter);
        }
    }

    private void promptGuess(Character remainingCharacter, Character chosenCharacter, String player, CharacterAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = getString(R.string.guess_character_prompt, remainingCharacter.getName());
        builder.setTitle(title);

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (remainingCharacter.getName().equals(chosenCharacter.getName())) {
                    endGame(player + " ha ganado");
                } else {
                    endGame(player + " ha perdido");
                }
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Restaura el último personaje tachado para el jugador actual
                if (player.equals(getString(R.string.player_1))) {
                    if (lastCrossedOutCharacterP1 != null) {
                        characterAdapterP1.restoreLastCrossedOut();
                        lastCrossedOutCharacterP1 = null;
                    }
                } else {
                    if (lastCrossedOutCharacterP2 != null) {
                        characterAdapterP2.restoreLastCrossedOut();
                        lastCrossedOutCharacterP2 = null;
                    }
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void restoreLastCrossedOut(String player) {
        if (getString(R.string.player_1).equals(player) && lastCrossedOutCharacterP1 != null) {
            characterAdapterP1.restoreLastCrossedOut();
            lastCrossedOutCharacterP1 = null;
        } else if (getString(R.string.player_2).equals(player) && lastCrossedOutCharacterP2 != null) {
            characterAdapterP2.restoreLastCrossedOut();
            lastCrossedOutCharacterP2 = null;
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
        String[] names = new String[characters.length];
        for (int i = 0; i < characters.length; i++) {
            names[i] = characters[i].getName();
        }
        return names;
    }

    private void endGame(String winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.game_over));
        builder.setMessage(winner);
        builder.setPositiveButton(getString(R.string.menu), new DialogInterface.OnClickListener() {
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
    public void setRecyclerViewScrollEnabled(RecyclerView recyclerView, boolean enabled) {
        if (recyclerView != null && recyclerView.getLayoutManager() instanceof MainActivity.ScrollableGridLayoutManager) {
            MainActivity.ScrollableGridLayoutManager gridLayoutManager = (MainActivity.ScrollableGridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setScrollEnabled(enabled);
        }
    }

    public void checkForWinnerAfterCrossing(String currentPlayer) {
        if (currentPlayer.equals(getString(R.string.player_1))) {
            checkForWinner(chosenCharacterP2, getString(R.string.player_1), characterAdapterP1);
        } else {
            checkForWinner(chosenCharacterP1, getString(R.string.player_2), characterAdapterP2);
        }
    }
}
