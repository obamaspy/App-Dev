package com.example.myapplication;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DemoEvenActivity extends AppCompatActivity {
    Button btnClickMe, btnOpen, btnSubmit;
    EditText editData, edtText;
    CheckBox cbAgree;
    RadioGroup radGrGender;
    RadioButton radMale, radFemale;
    TextView tvCountData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_event);
        //tim phan tu ngoai view
        btnClickMe = findViewById(R.id.btnClickMe);
        editData = findViewById(R.id.editData);
        btnOpen = findViewById(R.id.btnOpen);
        btnSubmit = findViewById(R.id.btnSubmit);
        cbAgree = findViewById(R.id.cbAgree);
        radGrGender = findViewById(R.id.radGrGender);
        radMale = findViewById(R.id.radMale);
        radFemale = findViewById(R.id.radFemale);
        edtText = findViewById(R.id.edtText);
        tvCountData = findViewById(R.id.tvCountData);


        //block
        btnClickMe.setEnabled(false);
        editData.setEnabled(false);
        btnSubmit.setEnabled(false);

        editData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtText.setText(s);
                tvCountData.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedID = radGrGender.getCheckedRadioButtonId();
                RadioButton rad = (RadioButton) findViewById(selectedID);
                if (rad == null){
                    Toast.makeText(DemoEvenActivity.this, "Choose Gender", Toast.LENGTH_SHORT).show();
                    return;

                }
                String gender=rad.getText().toString().trim().toLowerCase();
                Toast.makeText(DemoEvenActivity.this, gender, Toast.LENGTH_SHORT).show();
            }
        });

        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    btnSubmit.setEnabled(true);
                }else {
                    btnSubmit.setEnabled(false);
                }
            }
        });

        //bat su kien cho button
        btnClickMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editData.getText().toString().trim();
                Toast.makeText(DemoEvenActivity.this, data,Toast.LENGTH_SHORT).show();
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData.setEnabled(true);
                btnClickMe.setEnabled(true);
            }
        });
    }
}
