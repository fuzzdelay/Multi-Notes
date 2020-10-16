package com.jonathanhense.notes;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NoteEntryViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView body;
    TextView timestamp;

    NoteEntryViewHolder(View itemView){
        super(itemView);

        title = itemView.findViewById(R.id.title);
        body = itemView.findViewById(R.id.body);
        timestamp = itemView.findViewById(R.id.timestamp);
    }
}
