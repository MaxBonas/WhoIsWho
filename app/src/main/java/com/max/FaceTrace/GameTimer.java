package com.max.FaceTrace;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameTimer {
    private CountDownTimer countDownTimer;
    private TextView timerTextView;
    private TextView scoreTextView;
    private long duration;
    private int score;
    private MainActivity mainActivity;
    private Context context;

    public GameTimer(long duration, TextView timerTextView, TextView scoreTextView, MainActivity mainActivity, Context context) {
        this.duration = duration;
        this.timerTextView = timerTextView;
        this.scoreTextView = scoreTextView;
        initializeTimer();
        this.mainActivity = mainActivity;
        this.context = context;
    }

    private void initializeTimer() {
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerTextView.setText(String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                // Calcula el porcentaje de tiempo restante
                double percentageLeft = (double) millisUntilFinished / duration;
                score = (int) (percentageLeft * 100);

                scoreTextView.setText(context.getString(R.string.score, score));

            }

            @Override
            public void onFinish() {
                timerTextView.setText(context.getString(R.string.time_up));
                mainActivity.endGame(context.getString(R.string.you_lose));
            }
        };
    }

    public void start() {
        countDownTimer.start();
    }

    public void stop() {
        countDownTimer.cancel();
    }

    public int getScore() {
        return score;
    }
}
