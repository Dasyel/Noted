package com.dasyel.noted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;


public class NoteActivity extends ActionBarActivity {
    ArrayList<String> noteNames;
    SQLiteDatabase myDB;
    String noteName;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        sp = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        noteNames = intent.getStringArrayListExtra("noteNames");
        noteName = intent.getStringExtra("noteName");
        myDB = openOrCreateDatabase("NotedDB", MODE_PRIVATE, null);
        if (noteName != null) {
            Cursor resultSet = myDB.rawQuery(
                    "Select * from Notes WHERE Name = '" + noteName + "'", null);
            resultSet.moveToFirst();
            String noteBody = resultSet.getString(1);
            resultSet.close();

            EditText name = (EditText) findViewById(R.id.noteName);
            EditText body = (EditText) findViewById(R.id.noteBody);
            name.setText(noteName);
            body.setText(noteBody);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EditText name = (EditText) findViewById(R.id.noteName);
        EditText body = (EditText) findViewById(R.id.noteBody);
        String nameString = name.getText().toString();
        String bodyString = body.getText().toString();
        SharedPreferences.Editor spEditor = sp.edit();
        if (!sp.contains("nameCount")){
            spEditor.putInt("nameCount", 0);
            spEditor.apply();
        }
        if (nameString.equals("")){
            nameString = "Note " + Integer.toString(sp.getInt("nameCount", 0));
            spEditor.putInt("nameCount", sp.getInt("nameCount", 0) + 1);
            spEditor.apply();
            while (noteNames.contains(nameString)){
                nameString = "Note " + Integer.toString(sp.getInt("nameCount", 0));
                spEditor.putInt("nameCount", sp.getInt("nameCount", 0) + 1);
                spEditor.apply();
            }
        }
        if (noteName == null || !nameString.equals(noteName)){
            int i = 0;
            String newName = nameString;
            while (noteNames.contains(newName)){
                newName = nameString + Integer.toString(i);
                i++;
            }
            nameString = newName;
        }
        if (noteName == null) {
            myDB.execSQL("INSERT INTO Notes VALUES('" + nameString + "','" + bodyString + "');");
        } else {
            myDB.execSQL("UPDATE Notes SET Name = '"+nameString+"', " +
                            "Body= '"+bodyString+"' WHERE Name = '"+noteName+"';");
        }

        noteName = nameString;
        spEditor.putString("currentNote", nameString);
        spEditor.apply();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
