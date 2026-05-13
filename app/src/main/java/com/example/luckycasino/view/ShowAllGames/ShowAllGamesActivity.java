package com.example.luckycasino.view.ShowAllGames;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.luckycasino.R;
import com.example.luckycasino.view.PlaceBet.PlaceBetActivity;

import java.util.ArrayList;
import java.util.List;



/**
 * Η GamesLobbyActivity εμφανίζει τη λίστα παιχνιδιών με δυνατότητα φιλτραρίσματος.
 * Υλοποιεί το GamesLobbyView και επικοινωνεί με τον GamesLobbyPresenter.
 * Ακολουθεί το αρχιτεκτονικό πρότυπο MVP (Model-View-Presenter).
 */
public class ShowAllGamesActivity extends AppCompatActivity implements ShowAllGamesView {

    private ShowAllGamesPresenter presenter;

    // φίλτρα
    private EditText    etMinStars;
    private Spinner     spinnerBetCategory;
    private Spinner     spinnerRiskLevel;
    private Button      btnFilter;
    private Button      btnShowAll;
    private ProgressBar progressBar;

    // λίστα παιχνιδιών — dynamically created buttons
    private LinearLayout gamesContainer;

    private String customerId;

    // αποθηκεύει τα raw strings των παιχνιδιών
    private List<String> currentGames = new ArrayList<>();

    /**
     * Μέθοδος που καλείται κατά τη δημιουργία της δραστηριότητας.
     * Αρχικοποιεί το UI και τον presenter.
     * @param savedInstanceState Το αποθηκευμένο state της δραστηριότητας
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_games);

        // παίρνει το username από το HomescreenActivity
        customerId = getIntent().getStringExtra("customer_id");

        // αρχικοποίηση presenter
        presenter = new ShowAllGamesPresenter(this);

        // αρχικοποίηση UI στοιχείων
        etMinStars         = findViewById(R.id.et_min_stars);
        spinnerBetCategory = findViewById(R.id.spinner_bet_category);
        spinnerRiskLevel   = findViewById(R.id.spinner_risk_level);
        btnFilter          = findViewById(R.id.btn_filter);
        btnShowAll         = findViewById(R.id.btn_show_all);
        progressBar        = findViewById(R.id.progress_bar);
        gamesContainer     = findViewById(R.id.games_container);

        // Spinner για betCategory
        ArrayAdapter<String> betAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Any", "$", "$$", "$$$"});
        betAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBetCategory.setAdapter(betAdapter);

        // Spinner για riskLevel
        ArrayAdapter<String> riskAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Any", "low", "medium", "high"});
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskLevel.setAdapter(riskAdapter);

        // κουμπί φιλτραρίσματος
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String starsStr = etMinStars.getText().toString().trim();
                int minStars = starsStr.isEmpty() ? 0 : Integer.parseInt(starsStr);

                String betCategory = spinnerBetCategory.getSelectedItem().toString();
                if (betCategory.equals("Any")) betCategory = "";

                String riskLevel = spinnerRiskLevel.getSelectedItem().toString();
                if (riskLevel.equals("Any")) riskLevel = "";

                progressBar.setVisibility(View.VISIBLE);
                btnFilter.setEnabled(false);
                gamesContainer.removeAllViews();

                presenter.filterGames(minStars, betCategory, riskLevel);
            }
        });

        // κουμπί εμφάνισης όλων
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btnShowAll.setEnabled(false);
                gamesContainer.removeAllViews();
                etMinStars.setText("");
                spinnerBetCategory.setSelection(0);
                spinnerRiskLevel.setSelection(0);

                presenter.loadAllGames();
            }
        });

        // φορτώνει όλα τα παιχνίδια κατά την εκκίνηση
        progressBar.setVisibility(View.VISIBLE);
        presenter.loadAllGames();
    }

    /**
     * Εμφανίζει ένα μήνυμα λάθους στον χρήστη.
     * @param message Το μήνυμα προς εμφάνιση
     */
    @Override
    public void showError(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                btnFilter.setEnabled(true);
                btnShowAll.setEnabled(true);
                Toast.makeText(ShowAllGamesActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ορίζει τη λίστα παιχνιδιών και δημιουργεί κουμπιά για κάθε παιχνίδι.
     * Κάθε κουμπί οδηγεί στο PlaceBetActivity.
     * @param games Η λίστα με τα raw strings των παιχνιδιών
     */
    @Override
    public void setGameList(List<String> games) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                btnFilter.setEnabled(true);
                btnShowAll.setEnabled(true);
                gamesContainer.removeAllViews();
                currentGames = games;

                if (games.isEmpty()) {
                    Toast.makeText(ShowAllGamesActivity.this, "No games found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // δημιουργεί κουμπί για κάθε παιχνίδι
                for (String gameStr : games) {
                    String[] parts = gameStr.split(",");
                    if (parts.length < 6) continue;

                    String gameName    = parts[0];
                    String provider    = parts[1];
                    int    stars       = 0;
                    try { stars = Integer.parseInt(parts[2].trim()); } catch (Exception ignored) {}
                    String minBet      = parts[3];
                    String riskLevel   = parts[5];
                    String betCategory = parts.length > 6 ? parts[6] : "";

                    final String finalGameName = gameName;
                    final int    finalStars    = stars;

                    // card layout για κάθε παιχνίδι
                    LinearLayout card = new LinearLayout(ShowAllGamesActivity.this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setPadding(24, 24, 24, 24);
                    card.setBackgroundColor(0xFF1E1E2E);

                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    cardParams.setMargins(0, 0, 0, 24);
                    card.setLayoutParams(cardParams);

                    // τίτλος παιχνιδιού
                    android.widget.TextView txtName = new android.widget.TextView(ShowAllGamesActivity.this);
                    txtName.setText(gameName);
                    txtName.setTextSize(18f);
                    txtName.setTextColor(0xFFFFFFFF);
                    txtName.setPadding(0, 0, 0, 8);
                    card.addView(txtName);

                    // πληροφορίες
                    android.widget.TextView txtInfo = new android.widget.TextView(ShowAllGamesActivity.this);
                    txtInfo.setText(provider + "  |  ★" + stars + "  |  " + betCategory + "  |  Risk: " + riskLevel + "  |  Min Bet: " + minBet);
                    txtInfo.setTextSize(13f);
                    txtInfo.setTextColor(0xFFAAAAAA);
                    txtInfo.setPadding(0, 0, 0, 16);
                    card.addView(txtInfo);

                    // κουμπί PLAY
                    Button btnPlay = new Button(ShowAllGamesActivity.this);
                    btnPlay.setText("PLAY");
                    btnPlay.setTextColor(0xFFFFFFFF);
                    btnPlay.setBackgroundColor(0xFF0A3C0B);

                    LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    btnPlay.setLayoutParams(btnParams);

                    // πηγαίνει στο PlaceBetActivity
                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowAllGamesActivity.this, PlaceBetActivity.class);
                            intent.putExtra("customer_id", customerId);
                            intent.putExtra("game_name",   finalGameName);
                            intent.putExtra("game_stars",  finalStars);
                            intent.putExtra("game_logo",   finalGameName.toLowerCase());
                            startActivity(intent);
                        }
                    });

                    card.addView(btnPlay);
                    gamesContainer.addView(card);
                }

                Toast.makeText(ShowAllGamesActivity.this, games.size() + " games found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}