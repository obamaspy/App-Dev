package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.database.UserDB;
import com.example.myapplication.model.UserModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvUsername = view.findViewById(R.id.tvUsername);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhone);

        // Lấy username từ Intent
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            String username = bundle.getString("USERNAME_ACCOUNT", "");
            Log.d("SettingFragment", "Username: " + username);

            String email = bundle.getString("USER_EMAIL", "");
            Log.d("SettingFragment", "Email: " + email);

            String phone = bundle.getString("USER_PHONE", "");
            Log.d("SettingFragment", "Phone: " + phone);

            if (!username.isEmpty()) {
                tvTitle.setText("Welcome: " + username);

                // Lấy thông tin người dùng từ cơ sở dữ liệu
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

        // Xử lý sự kiện logout
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Khi logout, chuyển về màn hình SignInActivity
            Intent signInIntent = new Intent(getActivity(), SignInActivity.class);
            signInIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signInIntent);
            getActivity().finish(); // Đảm bảo không quay lại được màn hình này khi nhấn back
        });

        return view;
    }
}
