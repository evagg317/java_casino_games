package com.example.luckycasino.view.PlaceBet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.example.luckycasino.R;

public class PlaceBetActivity extends AppCompatActivity implements PlaceBetView {

    private PlaceBetPresenter presenter;

    private android.widget.LinearLayout rootLayout;
    private TextView txtTitle, txtResult;
    private RatingBar ratingBar;
    private TextInputEditText inputBet;
    private Button btnPlaceBet;

    private String customerId;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_bet);

        presenter = new PlaceBetPresenter(this);

        rootLayout = findViewById(R.id.root_place_bet_layout);
        txtTitle = findViewById(R.id.txt_game_title);
        ratingBar = findViewById(R.id.rating_game);
        inputBet = findViewById(R.id.input_bet_amount);
        btnPlaceBet = findViewById(R.id.btn_place_bet);
        txtResult = findViewById(R.id.txt_result);

        //pernei dedomena apo proigoumeni othoni
        customerId = getIntent().getStringExtra("customer_id");
        gameName = getIntent().getStringExtra("game_name");
        int stars = getIntent().getIntExtra("game_stars", 0);
        String logoName = getIntent().getStringExtra("game_logo");

        presenter.loadData(gameName, stars, logoName);

        btnPlaceBet.setOnClickListener(v -> {
            String betStr = inputBet.getText().toString();
            presenter.onPlaceBetClicked(customerId, gameName, betStr);
        });
    }

    @Override
    public void displayGameInfo(String name, int stars, String logoResName) {
        txtTitle.setText(name);
        ratingBar.setRating(stars);

        if (logoResName != null) {
            int imageResource = getResources().getIdentifier(logoResName, "drawable", getPackageName());
            if (imageResource != 0) {
                rootLayout.setBackgroundResource(imageResource);
            }
        }
    }

    @Override
    public void showResult(String message) {
        //girnaei sto kentriko thread
        runOnUiThread(() -> {
            txtResult.setText(message);
        });
    }

    @Override
    public void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            txtResult.setText(message);
        });
    }
}
