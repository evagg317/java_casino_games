package com.example.luckycasino.view.GamesLobby;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckycasino.R;
import com.example.luckycasino.model.Request;
import com.example.luckycasino.model.Response;
import com.example.luckycasino.model.TCP;
import com.example.luckycasino.view.PlaceBet.PlaceBetActivity;

public class GamesLobbyActivity extends AppCompatActivity implements GamesLobbyView {

    private LinearLayout gamesContainer;
    private String customerId;
    private GamesLobbyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_lobby);

        gamesContainer = findViewById(R.id.games_container);
        customerId = getIntent().getStringExtra("customer_id");

        fetchGamesFromServer();
    }

    //@Override
    private void fetchGamesFromServer() {
        new Thread(() -> {
            try {

                Request req = new Request(Request.GET_GAMES);
                TCP tcp = new TCP();
                String rawResponse = tcp.sendRequest(req.serialize());


                Response resp = Response.deserialize(rawResponse);

                if (resp.isSuccess() && resp.getData() != null) {
                    runOnUiThread(() -> createGameButtons(resp.getData()));
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void createGameButtons(String data) {
        // dedomena se morfi: GameName,Provider,Stars,MinBet...;
        String[] games = data.split(";");

        for (String gameStr : games) {
            if (gameStr.trim().isEmpty()) continue;

            String[] parts = gameStr.split(",");
            if (parts.length < 3) continue;

            String gameName = parts[0]; // perno mono to onoma
            int stars = Integer.parseInt(parts[2]); // perno asteria

            Button btnGame = new Button(this);
            btnGame.setText("PLAY " + gameName + " (" + stars + " Stars)");
            btnGame.setTextSize(18f);

            // kena gia koybia
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            btnGame.setLayoutParams(params);

            btnGame.setOnClickListener(v -> {
                Intent intent = new Intent(GamesLobbyActivity.this, PlaceBetActivity.class);
                intent.putExtra("customer_id", customerId);
                intent.putExtra("game_name", gameName);
                intent.putExtra("game_stars", stars);
                intent.putExtra("game_logo", gameName.toLowerCase());
                startActivity(intent);
            });

            gamesContainer.addView(btnGame);
        }
    }
}