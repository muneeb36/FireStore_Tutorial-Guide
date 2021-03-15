package com.example.firestoretestingapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.firestoretestingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ///// MainActivity XML //////////////
    private ActivityMainBinding activityMainBinding ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        activityMainBinding.idPart1BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part1Activity.class));

        });
        activityMainBinding.idPart2BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part2Activity.class));

        });

        activityMainBinding.idPart3BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part3Activity.class));

        });
        activityMainBinding.idPart4BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part4Activity.class));

        });

        activityMainBinding.idPart5BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part5Activity.class));

        });
        activityMainBinding.idPart6BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part6Activity.class));

        });
        activityMainBinding.idPart7BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part7Activity.class));

        });
        activityMainBinding.idPart8BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part8Activity.class));

        });
        activityMainBinding.idPart9BTN.setOnClickListener(v -> {

                    startActivity(new Intent(this, Part9Activity.class));

        });

        activityMainBinding.idPart10BTN.setOnClickListener(v -> {

            startActivity(new Intent(this, Part10Activity.class));

        });

    }


}