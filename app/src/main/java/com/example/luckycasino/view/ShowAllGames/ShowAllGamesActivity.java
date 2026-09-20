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


//GamesLobbyActivity emfanizei ti lista me ta paixnidia
//ylopoiei to GamesLobbyView kai epikoinonei me ton GamesLobbyPresenter
public class ShowAllGamesActivity extends AppCompatActivity implements ShowAllGamesView {
    private ShowAllGamesPresenter presenter;
    //filtra
    private EditText etMinStars;
    private Spinner spinnerBetCategory;
    private Spinner spinnerRiskLevel;
    private Button btnFilter;
    private Button btnShowAll;
    private ProgressBar progressBar;
    private LinearLayout gamesContainer;
    private String customerId;

    //apothikeyei ta raw strings ton paixnidion
    private List<String> currentGames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_all_games);

        //pairnei to username apo to homescreenactivity
        customerId = getIntent().getStringExtra("customer_id");

        presenter = new ShowAllGamesPresenter(this);

        etMinStars = findViewById(R.id.et_min_stars);
        spinnerBetCategory = findViewById(R.id.spinner_bet_category);
        spinnerRiskLevel = findViewById(R.id.spinner_risk_level);
        btnFilter = findViewById(R.id.btn_filter);
        btnShowAll = findViewById(R.id.btn_show_all);
        progressBar = findViewById(R.id.progress_bar);
        gamesContainer = findViewById(R.id.games_container);

        //spinner gia betCategory
        ArrayAdapter<String> betAdapter = new ArrayAdapter<>(spinnerBetCategory.getContext(), android.R.layout.simple_spinner_item, new String[]{"Any", "$", "$$", "$$$"});
        betAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBetCategory.setAdapter(betAdapter);

        //spinner gia riskLevel
        ArrayAdapter<String> riskAdapter = new ArrayAdapter<>(spinnerRiskLevel.getContext(), android.R.layout.simple_spinner_item, new String[]{"Any", "low", "medium", "high"});
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRiskLevel.setAdapter(riskAdapter);

        //koumpi gia to filtrarisma
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

        //koumpi gia emfanisi olon ton paixnidion
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

        //fortonei ola ta paixnidia kata tin ekkinisi
        progressBar.setVisibility(View.VISIBLE);
        presenter.loadAllGames();
    }


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

   //orizei th lista paixnidion kai dimiourgei koumpia gia kathe paixnidi
   //kathe koumpi odhgei sto PlaceBetActivity
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

                //dimiourgei koumpi gia kathe paixnidi
                for (String gameStr : games) {
                    String[] parts = gameStr.split(",");
                    if (parts.length < 6) continue;

                    String gameName = parts[0];
                    String provider = parts[1];
                    int stars = 0;
                    try { stars = Integer.parseInt(parts[2].trim()); } catch (Exception ignored) {}
                    String minBet = parts[3];
                    String riskLevel = parts[5];
                    String betCategory = parts.length > 6 ? parts[6] : "";

                    final String finalGameName = gameName;
                    final int finalStars = stars;

                    //card layout
                    LinearLayout card = new LinearLayout(ShowAllGamesActivity.this);
                    card.setOrientation(LinearLayout.VERTICAL);
                    card.setPadding(24, 24, 24, 24);
                    card.setBackgroundColor(0xFF1E1E2E);

                    LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    cardParams.setMargins(0, 0, 0, 24);
                    card.setLayoutParams(cardParams);

                    //(LinearLayout)
                    LinearLayout headerLayout = new LinearLayout(ShowAllGamesActivity.this);
                    headerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    headerLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                    headerLayout.setPadding(0, 0, 0, 8);

                    //titlos paixnidiou
                    android.widget.TextView txtName = new android.widget.TextView(ShowAllGamesActivity.this);
                    txtName.setText(finalGameName);
                    txtName.setTextSize(20f);
                    txtName.setTextColor(0xFFFFFFFF);
                    txtName.setTypeface(null, android.graphics.Typeface.BOLD);

                    // layout_weight=1.0f gia topothesia asterion
                    LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f
                    );
                    txtName.setLayoutParams(nameParams);
                    headerLayout.addView(txtName);

                    //asterakia
                    android.widget.RatingBar ratingBar = new android.widget.RatingBar(ShowAllGamesActivity.this);
                    ratingBar.setNumStars(5);
                    ratingBar.setStepSize(1.0f);


                    // topikos xoros apothikeusis "LuckyCasinoRatings"
                    android.content.SharedPreferences prefs = getSharedPreferences("LuckyCasinoRatings", MODE_PRIVATE);

                    //monadiko kleidi gia kathe paikti kai paixnidi
                    String ratingKey = customerId + "_" + finalGameName;

                    // diabazo vathmologia allios 0
                    int savedRating = prefs.getInt(ratingKey, 0);
                    ratingBar.setRating(savedRating);

                    // emfanisi
                    ratingBar.setScaleX(0.7f);
                    ratingBar.setScaleY(0.7f);
                    ratingBar.setPivotX(0f);
                    ratingBar.setPivotY(0f);

                    // xristis allazei vathmo
                    ratingBar.setOnRatingBarChangeListener(new android.widget.RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(android.widget.RatingBar rb, float rating, boolean fromUser) {
                            if (fromUser) {
                                int givenStars = (int) rating;

                                //apothikeusi genika
                                prefs.edit().putInt(ratingKey, givenStars).apply();

                                // apothikeusi ston server
                                presenter.rateGame(customerId, finalGameName, givenStars);
                            }
                        }
                    });
                    headerLayout.addView(ratingBar);

                    card.addView(headerLayout);

                    //info
                    android.widget.TextView txtInfo = new android.widget.TextView(ShowAllGamesActivity.this);
                    txtInfo.setText(provider + "  |  ★" + stars + "  |  " + betCategory + "  |  Risk: " + riskLevel + "  |  Min Bet: " + minBet);
                    txtInfo.setTextSize(13f);
                    txtInfo.setTextColor(0xFFAAAAAA);
                    txtInfo.setPadding(0, 0, 0, 16);
                    card.addView(txtInfo);

                    Button btnPlay = new Button(ShowAllGamesActivity.this);
                    btnPlay.setText("PLAY");
                    btnPlay.setTextColor(0xFFFFFFFF);
                    btnPlay.setBackgroundColor(0xFF0A3C0B);

                    LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    btnPlay.setLayoutParams(btnParams);

                    //pigainei sto PlaceBetActivity
                    btnPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowAllGamesActivity.this, PlaceBetActivity.class);
                            intent.putExtra("customer_id", customerId);
                            intent.putExtra("game_name", finalGameName);
                            intent.putExtra("game_stars", finalStars);
                            intent.putExtra("game_logo", finalGameName.toLowerCase());
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