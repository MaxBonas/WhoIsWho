package com.max.FaceTrace;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class TitleActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static GoogleSignInAccount account;
    private Button continueButton;
    private AudioManager audioManager;  // Nueva variable para el AudioManager

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        ImageView backgroundGif = findViewById(R.id.background_gif);
        Glide.with(this).load(R.drawable.fondo_futurista).into(backgroundGif);

        // Inicializar mGoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar AudioManager
        audioManager = new AudioManager(this);
        audioManager.startSpecificMusic(0);  // 0 es el índice de piano_bso.mp3

        // Inicialización de la variable de instancia
        continueButton = findViewById(R.id.continue_button);
        continueButton.setEnabled(false);

        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, MainMenuActivity.class);
            if (account != null) {
                intent.putExtra("playerName", account.getDisplayName());
            } else {
                intent.putExtra("playerName", getString(R.string.guest));
            }
            startActivity(intent);
            finish();
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                account = task.getResult(ApiException.class);

                // Autenticar con Firebase utilizando el token de ID de Google
                firebaseAuthWithGoogle(account.getIdToken());

                Toast.makeText(this, getString(R.string.sign_in_success, account.getDisplayName()), Toast.LENGTH_LONG).show();
            } catch (ApiException e) {
                Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_LONG).show();
            }
            continueButton.setEnabled(true); // Habilitar el botón en ambos casos
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso, actualiza la interfaz de usuario con la información del usuario
                    } else {
                        // Si falla el inicio de sesión, muestra un mensaje al usuario.
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isConnectedToInternet()) {
            account = GoogleSignIn.getLastSignedInAccount(this);
            if (account != null) {
                continueButton.setEnabled(true);
                Toast.makeText(this, getString(R.string.already_authenticated, account.getDisplayName()), Toast.LENGTH_LONG).show();
            } else {
                signIn();
            }
        } else {
            continueButton.setEnabled(true);
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }
    }


    public boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getCurrentPlayerName(Context context) {
        if (account != null) {
            return account.getDisplayName();
        }
        return context.getString(R.string.guest);
    }
}