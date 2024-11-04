package com.example.aethoneventsapp;

import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EventInvitationActivity extends AppCompatActivity {
    private Button acceptButton;
    private Button declineButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        acceptButton = findViewById(R.id.accept_button);
        declineButton = findViewById(R.id.decline_button);
        // Todo add functionality to accept and decline invite
        acceptButton.setOnClickListener(v -> {

        });
        declineButton.setOnClickListener(v -> {});

    }
}
