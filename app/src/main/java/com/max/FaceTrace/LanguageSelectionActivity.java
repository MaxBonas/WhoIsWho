package com.max.FaceTrace;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        // Configurar botones para cambiar el idioma
        setUpLanguageButton(R.id.english_button, "en");
        setUpLanguageButton(R.id.spanish_button, "es");
        setUpLanguageButton(R.id.german_button, "de");
        setUpLanguageButton(R.id.french_button, "fr");
        setUpLanguageButton(R.id.italian_button, "it");
        setUpLanguageButton(R.id.portuguese_button, "pt");
        setUpLanguageButton(R.id.chinese_button, "zh");
        setUpLanguageButton(R.id.japanese_button, "ja");
        setUpLanguageButton(R.id.korean_button, "ko");
    }

    private void setUpLanguageButton(int buttonId, String languageCode) {
        Button languageButton = findViewById(buttonId);
        languageButton.setOnClickListener(v -> setLocale(languageCode));
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Obtener el nombre del jugador de la intención
        String playerName = getIntent().getStringExtra("playerName");

        Intent intent = new Intent(LanguageSelectionActivity.this, IntroActivity.class);
        startActivity(intent);
        finish();
    }
}