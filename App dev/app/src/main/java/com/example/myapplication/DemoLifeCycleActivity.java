package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DemoLifeCycleActivity extends AppCompatActivity {
    Button btnSecondActivity;
    EditText editData;
    private final String LifeCycleActivity = "LifeCycleActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load view giao dien
        //xu ly cac logic o day
        setContentView(R.layout.activity_demo_life_cycle_firt);
        Log.i(LifeCycleActivity, "*** onCreate is running ***");

        btnSecondActivity = findViewById(R.id.btnSecondActivity);
        editData = findViewById(R.id.editInputData);
        btnSecondActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getdata
                String data = editData.getText().toString().trim();
                if (TextUtils.isEmpty(data)){
                    Toast.makeText(DemoLifeCycleActivity.this, "Data is not empty", Toast.LENGTH_SHORT).show();

                }
                Intent intent = new Intent(DemoLifeCycleActivity.this, DemoLCASecond.class);
                Bundle bundle = new Bundle();
                bundle.putString("MY_DATA", data);
                bundle.putInt("ID_USER", 100);
                bundle.putBoolean("CHECKING", false);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LifeCycleActivity, "*** onRestart is running ***");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LifeCycleActivity, "*** onPause is running ***");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LifeCycleActivity, "*** onStop is running***");
    }

    @Override
    //duoc kich hoat ngay truoc khi ac hien thi tren man hinh
    protected void onStart(){
        super.onStart();
        Log.i(LifeCycleActivity, "*** onStart is running ***");
    }
    @Override
    //duoc goi ngay khi ac tuong tac duoc voi nguoi dung
    protected void onResume(){
        super.onResume();
        Log.i(LifeCycleActivity, "*** onResume is running ***");
    }
}
