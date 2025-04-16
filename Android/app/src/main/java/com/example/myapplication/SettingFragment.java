package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.myapplication.database.UserDB;
import com.example.myapplication.model.UserModel;

public class SettingFragment extends Fragment {

    private UserDB userDB;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDB = new UserDB(getContext()); // Khởi tạo UserDB
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        Switch switchDarkMode = view.findViewById(R.id.switchDarkMode);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Lấy dữ liệu từ Intent
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();

        //lưu dữ liệu người dùng
        if (bundle != null) {
            String username = bundle.getString("USERNAME_ACCOUNT", "");
            String email = bundle.getString("USER_EMAIL", "");
            String phone = bundle.getString("USER_PHONE", "");

            Log.d("SettingFragment", "Username: " + username);
            Log.d("SettingFragment", "Email: " + email);
            Log.d("SettingFragment", "Phone: " + phone);

            if (!username.isEmpty()) {
                tvTitle.setText("Welcome: " + username);
                UserModel user = userDB.getInfoUser(username, "", 0);
                if (user != null) {
                    tvUsername.setText("Username: " + username);
                    tvEmail.setText("Email: " + email);
                    tvPhone.setText("Phone: " + phone);
                } else {
                    Log.e("SettingFragment", "User data is null");
                }
            } else {
                Log.e("SettingFragment", "Username is empty");
            }
        }

        // Load trạng thái Dark Mode từ SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isDarkMode = prefs.getBoolean("DARK_MODE", false);
        switchDarkMode.setChecked(isDarkMode);

        // Đặt chế độ ban đầu
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Xử lý bật/tắt chế độ tối
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DARK_MODE", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Xử lý sự kiện logout
        btnLogout.setOnClickListener(v -> {
            Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
            signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signInIntent);
            getActivity().finish();
        });

        return view;
    }
}
