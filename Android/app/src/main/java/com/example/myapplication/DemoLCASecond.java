package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DemoLCASecond extends AppCompatActivity {
    Button btnFirstActivity;
    TextView tvData, tvID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_life_cycle_second);
        btnFirstActivity = findViewById(R.id.btnFirstActivity);
        tvData = findViewById(R.id.tvData);
        tvID = findViewById(R.id.tvIDuser);

        //get dât from intent 1st activity
        Intent intentData = getIntent();
        Bundle bundleData = intentData.getExtras();
        if (bundleData != null){
            String data = bundleData.getString("MY_DATA", "");
            int id = bundleData.getInt("ID_USER", 0);
            boolean check = bundleData.getBoolean("CHECKING", false);
            tvData.setText(data);
            tvID.setText(String.valueOf(id));
            btnFirstActivity.setEnabled(check);
        }

        btnFirstActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoLCASecond.this, DemoLifeCycleActivity.class);
                startActivity(intent);
            }
        });
    }
}
