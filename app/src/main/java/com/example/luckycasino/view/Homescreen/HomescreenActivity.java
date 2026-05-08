package com.example.luckycasino.view.Homescreen;

import android.os.Bundle;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.luckycasino.R;

public class  HomescreenActivity extends AppCompatActivity implements HomescreenView {
    private HomescreenPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        presenter = new HomescreenPresenter(
                this
        );

        findViewById(R.id.buttonPlayer).setOnClickListener(v -> {
            Intent intent = new Intent(this, view.User.UserActivity.class);
            intent.putExtra("ROLE_EXPECTED", "Player");
            startActivity(intent);
        });

        findViewById(R.id.buttonPlayer).setOnClickListener(v -> presenter.onPlayer());

    }

    @Override
    public void playerLogin(String username) {
        Intent intent = new Intent(this, view.Customer.CustomerActivity.class);
        intent.putExtra("customer_id", username);
        startActivity(intent);
    }

    @Override
    public void showError(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

}