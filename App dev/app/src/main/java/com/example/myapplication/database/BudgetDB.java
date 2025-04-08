package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.Nullable;

import com.example.myapplication.model.BudgetModel;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetDB extends SQLiteOpenHelper {
    private static final String DB_NAME = "campus_expenses";
    private static final String DB_TABLE = "budgets";
    private static final int DB_VERSION = 20;

    // Cột trong database
    private static final String ID_COL = "id";
    private static final String NAME_BUDGET_COL = "name";
    private static final String MONEY_BUDGET_COL = "money";
    private static final String DATE_COL = "date";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";
    private static final String DELETED_AT = "deleted_at";

    public BudgetDB(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + DB_TABLE + " ( "
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_BUDGET_COL + " TEXT NOT NULL, "
                + MONEY_BUDGET_COL + " INTEGER NOT NULL, "
                + DATE_COL + " TEXT, "
                + CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, "
                + UPDATED_AT + " TEXT DEFAULT NULL, "
                + DELETED_AT + " TEXT DEFAULT NULL )";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
        onCreate(db);
    }

    // Phương thức lấy thời gian hiện tại dưới dạng String
    private String getCurrentTimestamp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return ZonedDateTime.now().format(dtf);
        }
        return null;
    }

    // Thêm ngân sách vào database
    public long insertBudget(String name, int money, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_BUDGET_COL, name);
        values.put(MONEY_BUDGET_COL, money);
        values.put(DATE_COL, date);
        values.put(CREATED_AT, getCurrentTimestamp());

        long result = db.insert(DB_TABLE, null, values);
        db.close();
        return result;
    }

    // Cập nhật ngân sách (theo các tham số riêng lẻ)
    public int updateBudget(int id, String name, int money, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_BUDGET_COL, name);
        values.put(MONEY_BUDGET_COL, money);
        values.put(DATE_COL, date);
        values.put(UPDATED_AT, getCurrentTimestamp());

        int rowsAffected = db.update(DB_TABLE, values, ID_COL + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // ✅ Cập nhật ngân sách (theo đối tượng BudgetModel)
    public int updateBudget(BudgetModel budget) {
        return updateBudget(budget.getId(), budget.getName(), budget.getMoney(), budget.getDate());
    }

    // Xóa ngân sách (gắn timestamp vào deleted_at)
    public int deleteBudget(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DELETED_AT, getCurrentTimestamp());

        int rowsAffected = db.update(DB_TABLE, values, ID_COL + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    // Lấy danh sách tất cả ngân sách chưa bị xóa
    public List<BudgetModel> getAllBudgets() {
        List<BudgetModel> budgetList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + ID_COL + ", " + NAME_BUDGET_COL + ", " + MONEY_BUDGET_COL + ", " + DATE_COL +
                " FROM " + DB_TABLE + " WHERE " + DELETED_AT + " IS NULL ORDER BY " + CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_BUDGET_COL));
                int money = cursor.getInt(cursor.getColumnIndexOrThrow(MONEY_BUDGET_COL));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DATE_COL));

                budgetList.add(new BudgetModel(id, name, money, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return budgetList;
    }

    // Xóa toàn bộ ngân sách khỏi bảng (hard delete)
    public void clearAllBudgets() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DB_TABLE);
        db.close();
    }

    // Lấy tổng số tiền ngân sách hiện tại
    public int getTotalBudget() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + MONEY_BUDGET_COL + ") FROM " + DB_TABLE + " WHERE " + DELETED_AT + " IS NULL";
        Cursor cursor = db.rawQuery(query, null);

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public double getTotalExpenses() {
        double totalExpenses = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM expenses", null);

        if (cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(0);
        }

        cursor.close();
        db.close();
        return totalExpenses;
    }
}
