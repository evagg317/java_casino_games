package com.example.luckycasino.view.Homescreen;
import android.os.Bundle;
import android.content.Intent;

import com.example.luckycasino.view.ShowAllGames.ShowAllGamesActivity;

import com.example.luckycasino.view.GamesLobby.GamesLobbyActivity;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luckycasino.R;

public class  HomescreenActivity extends AppCompatActivity implements HomescreenView {
    private HomescreenPresenter presenter;
    private TextInputEditText usernameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        presenter = new HomescreenPresenter(this);

        usernameInput = findViewById(R.id.txt_username);

        findViewById(R.id.button_login).setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            presenter.onPlayer(username);
        });
    }

    @Override
    public void playerLogin(String username) {
        //paei stin epomeni othoni
        Intent intent = new Intent(this, ShowAllGamesActivity.class);
        intent.putExtra("customer_id", username);
        startActivity(intent);
    }
    @Override
    public void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}