package com.jonathanhense.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivityJH: ";

    private int NEW_CODE = 0;
    private int EDIT_CODE = 1;
    private final String TITLEBAR = "Multi-Notes";

    private final List<Note> notes = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    Note note = new Note();


    private boolean notesUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);

        noteAdapter = new NoteAdapter(notes, this);
        recyclerView.setAdapter(noteAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        note = loadFile();
        updateTitleNoteCount();
    }
    private void updateTitleNoteCount() {
        setTitle(TITLEBAR + " (" + notes.size() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.notes_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected: " + item.getTitle());
        switch (item.getItemId()) {
            case R.id.newNote:

                int requestCode = NEW_CODE;
                Intent intent = new Intent(this, EditNoteActivity.class);
                intent.putExtra("IS_CURRENT_NOTE", false);
                if (false) {
                    requestCode = EDIT_CODE;
                    if (note != null) {
                        intent.putExtra("CURRENT_NOTE", note);
                        intent.putExtra("CURRENT_NOTE_INDEX", 0);
                        startActivityForResult(intent, requestCode);
                    }
                } else {
                    startActivityForResult(intent, requestCode);
                }
                return true;
            case R.id.about:
                Intent intent2 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        saveFile();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putBoolean("NOTES_UPDATED", notesUpdated);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedState);
        notesUpdated = savedState.getBoolean("NOTES_UPDATED");
    }

    private void EditNoteActivity(boolean currentNote, int noteIndex, Note note) {
        Log.d(TAG, "EditNoteActivity: ");
        int requestCode = NEW_CODE;
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra("IS_CURRENT_NOTE", currentNote);
        if (currentNote) {
            requestCode = EDIT_CODE;
            if (note != null) {
                intent.putExtra("CURRENT_NOTE", note);
                intent.putExtra("CURRENT_NOTE_INDEX", noteIndex);
                startActivityForResult(intent, requestCode);
            }
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

/*
move this
    private String getTimeStamp() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, hh:mm aa");
        String dateTime = sdf.format(calendar.getTime());
        return dateTime;
    }
 */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == NEW_CODE) {
            if (resultCode == RESULT_OK){
                Note note = (Note) intent.getSerializableExtra("NEW_NOTE");
                if (note != null) {
                    notes.add(note);
                    //put this code in a method instead
                    updateRecycler();
                }
            }else{
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else if (requestCode == EDIT_CODE) {
            if (resultCode == RESULT_OK) {
                boolean noteUpdated = intent.getBooleanExtra("NOTE_UPDATED", false);
                if (noteUpdated) {
                    Note thisNote = (Note) intent.getSerializableExtra("CURRENT_NOTE");
                    int noteIndex = intent.getIntExtra("CURRENT_NOTE_INDEX", 0);
                    if (thisNote != null) {
                        notes.set(noteIndex, thisNote);
                        updateRecycler();
                    }
                }
            }else{
                Log.d(TAG, "onActivityResult: Current Note Edited: " + resultCode);

            }
        }

    }

    private void updateRecycler(){
        Log.d(TAG, "updateRecycler: ");
        notesUpdated = true;
        Collections.sort(notes);
        setTitle(TITLEBAR + " (" + notes.size() + ")");
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
        int pos = recyclerView.getChildLayoutPosition(v);

        Note note = notes.get(pos);
        EditNoteActivity(true, pos, note);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick: ");
        final int pos = recyclerView.getChildLayoutPosition(v);

        String title = "";
        Note note = notes.get(pos);
        if (note != null) {
            title = note.getTitle();
        }
        DeleteNote(pos, title);
        return true;
    }


    public void saveFile() {
        Log.d(TAG, "saveFile: ");
        try {

            Log.d(TAG, "saveNotes: ");


            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
            writer.setIndent("  ");

            writer.beginObject();
            writer.name("notes");
            writer.beginArray();

            if (notes.size() > 0) {
                for (Note note : notes) {
                    if (note != null) {
                        writer.beginObject();
                        writer.name("title").value(note.getTitle());
                        writer.name("body").value(note.getBody());
                        writer.name("timestamp").value(convertDateToString(note.getTimestamp()));
                        writer.endObject();
                    }
                }
            }
            writer.endArray();
            writer.endObject();
            writer.close();
            notesUpdated = false;

        }
        catch (Exception e)
        {
            e.getStackTrace();
        }
    }

    private Note loadFile(){
        Log.d(TAG, "loadFile: ");
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObj = new JSONObject(stringBuilder.toString());
            JSONArray jsonNoteArray = jsonObj.getJSONArray("notes");
            if (jsonNoteArray != null && jsonNoteArray.length() > 0) {
                for (int i = 0; i < jsonNoteArray.length(); i++) {
                    JSONObject notesJson = jsonNoteArray.getJSONObject(i);
                    if (notesJson != null) {
                        notes.add(new Note(notesJson.getString("title"), notesJson.getString("body"),
                                convertStringToDate(notesJson.getString("timestamp"))));
                    }
                }
            }
            if (notes.size() > 0) {
                Collections.sort(notes);
            }
        }

        catch (FileNotFoundException e)
        {
            //Toast.makeText(this, getString(R.string.no_file_found), Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return note;
    }

    public void DeleteNote(final int noteIndex, final String noteTitle){
        Log.d(TAG, "DeleteNote: ");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                notes.remove(noteIndex);
                notesUpdated = true;
                Collections.sort(notes);
                setTitle(TITLEBAR + " (" + notes.size() + ")");
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Note '" + noteTitle + "' deleted ",
                        Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setMessage("Delete Note '" + noteTitle + "'?");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Date convertStringToDate(String timeStamp){
        Log.d(TAG, "convertStringToDate: ");
        try{
            if(timeStamp != null){
                SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                sdf.applyPattern("EEE MMM d, HH:mm a");
                return sdf.parse(timeStamp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String convertDateToString(Date timeStamp){
        Log.d(TAG, "convertDateToString: ");
        try{
            if(timeStamp != null){
                SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                sdf.applyPattern("EEE MMM d, HH:mm a");
                return sdf.format(timeStamp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}