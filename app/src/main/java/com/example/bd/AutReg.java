package com.example.bd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class AutReg extends AppCompatActivity implements View.OnClickListener {
    Button dbAdd,  dbClear;
    EditText dbName, dbSurname, dbPost, dbCity;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aut_reg);

        dbAdd = findViewById(R.id.dbAdd);
        dbAdd.setOnClickListener(this);
        dbClear = findViewById(R.id.dbClear);
        dbClear.setOnClickListener(this);

        dbName = findViewById(R.id.dbName);
        dbSurname = findViewById(R.id.dbSurname);
        dbPost = findViewById(R.id.dbPost);
        dbCity = findViewById(R.id.dbCity);

        dbHelper = new DBHelper(this);
        database =  dbHelper.getWritableDatabase();
        dbName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                dbName.setHint("");
            else
                dbName.setHint("Имя");
        });
        dbSurname.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                dbSurname.setHint("");
            else
                dbSurname.setHint("Фамилия");
        });
        dbPost.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                dbPost.setHint("");
            else
                dbPost.setHint("Должность");
        });
        dbCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                dbCity.setHint("");
            else
                dbCity.setHint("Город");
        });
        UpdateTable();
    }
    public  void  UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int surnameIndex = cursor.getColumnIndex(DBHelper.KEY_SURNAME);
            int postIndex = cursor.getColumnIndex(DBHelper.KEY_POST);
            int cityIndex = cursor.getColumnIndex(DBHelper.KEY_CITY);
            TableLayout dbOutput = findViewById(R.id.dbOutput);
            dbOutput.removeAllViews();
            do {
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight= 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);

                TextView outputNAme = new TextView(this);
                params.weight= 3.0f;
                outputNAme.setLayoutParams(params);
                outputNAme.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputNAme);

                TextView outputSurname= new TextView(this);
                params.weight= 3.0f;
                outputSurname.setLayoutParams(params);
                outputSurname.setText(cursor.getString(surnameIndex));
                dbOutputRow.addView(outputSurname);

                TextView outputPost= new TextView(this);
                params.weight= 3.0f;
                outputPost.setLayoutParams(params);
                outputPost.setText(cursor.getString(postIndex));
                dbOutputRow.addView(outputPost);

                TextView outputCity= new TextView(this);
                params.weight= 3.0f;
                outputCity.setLayoutParams(params);
                outputCity.setText(cursor.getString(cityIndex));
                dbOutputRow.addView(outputCity);


                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight= 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(deleteBtn);

                dbOutput.addView(dbOutputRow);

            } while (cursor.moveToNext());

        }
        cursor.close();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dbAdd:
                String name = dbName.getText().toString();
                String surname = dbSurname.getText().toString();
                String post = dbPost.getText().toString();
                String city = dbCity.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_SURNAME, surname);
                contentValues.put(DBHelper.KEY_POST, post);
                contentValues.put(DBHelper.KEY_CITY, city);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                UpdateTable();
                dbName.setText("");
                dbSurname.setText("");
                dbPost.setText("");
                dbCity.setText("");
                break;

            case R.id.dbClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbOutput);
                dbOutput.removeAllViews();
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDb = (ViewGroup)  outputDBRow.getParent();
                outputDb.removeView(outputDBRow);
                outputDb.invalidate();

                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID +" = ?", new String[]{String.valueOf(v.getId())});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int surnameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_SURNAME);
                    int postIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_POST);
                    int cityIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_CITY);
                    int realID=1;
                    do {
                        if (cursorUpdater.getInt(idIndex)>realID)
                        {
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_SURNAME, cursorUpdater.getString(surnameIndex));
                            contentValues.put(DBHelper.KEY_POST, cursorUpdater.getString(postIndex));
                            contentValues.put(DBHelper.KEY_CITY, cursorUpdater.getString(cityIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    } while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast()){
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                cursorUpdater.close();
                break;
        }
    }
}