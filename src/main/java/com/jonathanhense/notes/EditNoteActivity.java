package com.jonathanhense.notes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private static final String TAG = "EditNoteActivityJH";
    private EditText noteTitle;
    private EditText noteBody;
    private int noteIndex;
    private boolean isCurrentNote;
    private String currentTitle = "";
    private String currentBody = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        noteTitle = findViewById(R.id.noteTitle);
        noteBody = findViewById(R.id.noteBody);
        noteBody.setMovementMethod(new ScrollingMovementMethod());
        noteBody.setTextIsSelectable(true);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IS_CURRENT_NOTE")) {

            isCurrentNote = intent.getBooleanExtra("IS_CURRENT_NOTE", false);
            if (isCurrentNote) {
                Note currentNote = (Note) intent.getSerializableExtra("CURRENT_NOTE");
                noteIndex = intent.getIntExtra("CURRENT_NOTE_INDEX", 0);
                if (currentNote != null) {

                    currentTitle = currentNote.getTitle();
                    currentBody = currentNote.getBody();
                    noteTitle.setText(currentTitle);
                    noteBody.setText(currentBody);
                }
            }
        }
        //noteTitle.setText(note.getTitle());
        //noteBody.setText(note.getBody());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()) {
            case R.id.saveNote:
                writeIf(false);
                //finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveNewNote() {
        Log.d(TAG, "saveNewNote: ");
        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();
        Intent intent = new Intent();
        Note note = new Note(title, body, new Date());
        intent.putExtra("NEW_NOTE", note);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveExistingNote() {
        Log.d(TAG, "saveExistingNote: ");
        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();
        Intent data = new Intent();
        Note note = new Note(title, body, new Date());
        data.putExtra("CURRENT_NOTE", note);
        data.putExtra("CURRENT_NOTE_INDEX", noteIndex);
        data.putExtra("NOTE_UPDATED", true);
        setResult(RESULT_OK, data);
        finish();
    }

    public void writeIf(boolean backPress) {
        Log.d(TAG, "writeIf: ");
        String title = noteTitle.getText().toString();
        String body = noteBody.getText().toString();
        if (isCurrentNote) {
            if (!title.isEmpty()) {
                boolean titleUpdated = !title.isEmpty() && !currentTitle.isEmpty()
                        && !title.equals(currentTitle);
                if (titleUpdated || !body.equals(currentBody)) {
                    if (backPress) {
                        buildDialogue(false);
                    } else {
                        saveExistingNote();
                    }
                } else {
                    finishAct();
                }
            } else {
                Toast.makeText(this, "Cannot save without a title",
                        Toast.LENGTH_SHORT).show();
                //put this code into a separate method
                finishAct();
            }
        }else{
            if(!title.isEmpty()){
                if(backPress){
                    buildDialogue(true);
                }else{
                    saveNewNote();
                    Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Cannot save without a title",
                        Toast.LENGTH_SHORT).show();
                finishAct();
            }
        }
    }

    public void finishAct(){
        Log.d(TAG, "finishAct: ");
        Intent intent = new Intent();
        intent.putExtra("NOTE_UPDATED", false);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void buildDialogue(final boolean isNewNote) {
       // final Intent intent = new Intent();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = noteTitle.getText().toString();
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (isNewNote) {
                    saveNewNote();
                } else {
                    saveExistingNote();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
               // intent.putExtra("NOTE_UPDATED", false);
                //setResult(RESULT_OK, intent);
                finishAct();
            }
        });
        builder.setMessage("Save Note '" + title + "'?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putInt("CURRENT_NOTE_INDEX", noteIndex);
        outState.putBoolean("IS_CURRENT_NOTE", isCurrentNote);
        outState.putString("CURRENT_NOTE_TITLE", currentTitle);
        outState.putString("CURRENT_NOTE_BODY", currentBody);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedState);
        noteIndex = savedState.getInt("CURRENT_NOTE_INDEX");
        isCurrentNote = savedState.getBoolean("IS_CURRENT_NOTE");
        currentTitle = savedState.getString("CURRENT_NOTE_TITLE");
        currentBody = savedState.getString("CURRENT_NOTE_BODY");

    }

    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
        writeIf(true);
    }
/*
    public void sansTitle() {
        title = noteTitle.getText().toString();
        if (title.length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Note cannot be saved without a title", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
    }*/


}