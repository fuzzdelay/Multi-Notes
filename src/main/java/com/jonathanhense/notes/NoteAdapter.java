package com.jonathanhense.notes;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteEntryViewHolder> {

    private List<Note> notesList;
    private MainActivity mainAct;

    NoteAdapter(List<Note> notes, MainActivity ma){
        this.notesList = notes;
        mainAct = ma;
    }

    @NonNull
    @Override
    public NoteEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_entry,
                parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NoteEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteEntryViewHolder holder, int position) {
        Note note = notesList.get(position);

        holder.title.setText(note.getTitle());
        holder.body.setText(note.getBody());
        holder.timestamp.setText(convertDateToString(note.getTimestamp()));

        String body = note.getBody();
        if(body!=null) {
            if (body.length() > 80) {
                String limit = body.substring(0, 80);
                String charLimit = limit + "...";
                holder.body.setText(charLimit);
            } else {
                holder.body.setText(note.getBody());
            }
        }else{
            holder.body.setText(" ");
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    private String convertDateToString(Date lastUpdatedTime) {
        try {

            if (lastUpdatedTime != null) {
                SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
                sdf.applyPattern("EEE MMM d, hh:mm aaa");
                return sdf.format(lastUpdatedTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
