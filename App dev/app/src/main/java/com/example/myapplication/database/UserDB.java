package com.example.myapplication.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.example.myapplication.model.UserModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UserDB extends SQLiteOpenHelper {
    public static final String DB_NAME = "campus_expenses";
    public static final String DB_TABLE = "users";
    public static final int DB_VERSION = 20;

    // Định nghĩa các cột trong bảng
    public static final String ID_COL = "id";
    public static final String USERNAME_COL = "username";
    public static final String PASSWORD_COL = "password";
    public static final String EMAIL_COL = "email";
    public static final String PHONE_COL = "phone";
    public static final String ROLE_ID_COL = "role_id";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String DELETED_AT = "deleted_at";

    public UserDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng và cơ sở dữ liệu
        String query = "CREATE TABLE " + DB_TABLE + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " VARCHAR(60) NOT NULL, "
                + PASSWORD_COL + " VARCHAR(230) NOT NULL, "
                + EMAIL_COL + " VARCHAR(65) NOT NULL, "
                + PHONE_COL + " VARCHAR(30) NOT NULL, "
                + ROLE_ID_COL + " INTEGER, "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME, "
                + DELETED_AT + " DATETIME )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public long insertUserToDatabase(String username, String password, String email, String phone) {
        // Lấy ngày giờ hiện tại
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        ZonedDateTime now = ZonedDateTime.now();
        String datenow = dtf.format(now);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);
        values.put(EMAIL_COL, email);
        values.put(PHONE_COL, phone);
        values.put(ROLE_ID_COL, 0);
        long insert = db.insert(DB_TABLE, null, values);
        db.close();
        return insert;
    }

    public boolean checkUsernameExists(String username){
        boolean checking = false;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = { ID_COL, USERNAME_COL, EMAIL_COL, PHONE_COL, ROLE_ID_COL };
            String condition = USERNAME_COL + " =? ";
            String[] params = { username };
            Cursor cursor = db.query(DB_TABLE, cols, condition, params, null, null, null);
            if(cursor.getCount() > 0) {
                checking = true;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return checking;
    }

    @SuppressLint("Range")
    public UserModel getInfoUser(String username, String data, int type) {
        UserModel user = new UserModel();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] cols = { ID_COL, USERNAME_COL, EMAIL_COL, PHONE_COL, ROLE_ID_COL };
            String condition = (type == 0) ? (USERNAME_COL + " =? AND " + PASSWORD_COL + " =? ") : (USERNAME_COL + " =? AND " + EMAIL_COL + " =? ");
            String[] params = { username, data };
            Cursor cursor = db.query(DB_TABLE, cols, condition, params, null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                user.setId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME_COL)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL_COL)));
                user.setPhone(cursor.getString(cursor.getColumnIndex(PHONE_COL)));
                user.setRoleID(cursor.getInt(cursor.getColumnIndex(ROLE_ID_COL)));
            } else {
                // Log lỗi nếu không tìm thấy người dùng
                Log.e("UserDB", "User not found for username: " + username);
            }
            cursor.close();
            db.close();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public int updateAccountPassword(int idAccount, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_COL, newPassword);
        String condition = ID_COL + " =? ";
        String id = String.valueOf(idAccount);
        String[] params = { id };
        int update = db.update(DB_TABLE, values, condition, params);
        db.close();
        return update;
    }
}
