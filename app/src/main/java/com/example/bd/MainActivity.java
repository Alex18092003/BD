package com.example.bd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button dbAdd, dbRead, dbClear;
    EditText dbName, dbMail;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dbName = findViewById(R.id.dbName);
        dbMail = findViewById(R.id.dbMail);
        dbAdd = findViewById(R.id.dbAdd);
        dbAdd.setOnClickListener(this);
        dbClear = findViewById(R.id.dbClear);
        dbClear.setOnClickListener(this);
        dbRead = findViewById(R.id.dbRead);
        dbRead.setOnClickListener(this);


        dbHelper = new DBHelper(this);
        database =  dbHelper.getWritableDatabase();
        UpdateTable();
    }
    public  void  UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int mailIndex = cursor.getColumnIndex(DBHelper.KEY_MAIL);
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

                TextView outputMail= new TextView(this);
                params.weight= 3.0f;
                outputMail.setLayoutParams(params);
                outputMail.setText(cursor.getString(mailIndex));
                dbOutputRow.addView(outputMail);


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
                String email = dbMail.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_MAIL, email);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                UpdateTable();
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
                    int mailIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_MAIL);
                    int realID=1;
                    do {
                        if (cursorUpdater.getInt(idIndex)>realID)
                        {
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_MAIL, cursorUpdater.getString(mailIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    } while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast()){
                        database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                }
                break;
        }
    }
}