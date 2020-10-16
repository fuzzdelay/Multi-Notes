package com.jonathanhense.notes;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable, Comparable<Note> {

    private String title;
    private String body;
    private Date timestamp;

    Note(String title, String body, Date timestamp){
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
    }

    public Note(){};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody(){
        return body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Note note) {
        if (note == null || getTimestamp() == null || note.getTimestamp() == null) {
            return 0;
        }
        return note.getTimestamp().compareTo(this.getTimestamp());    }
}
