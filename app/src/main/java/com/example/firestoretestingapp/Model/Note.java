package com.example.firestoretestingapp.Model;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String Title;
    private String Description;

    public Note() {
    }

    public Note( String title, String description) {
        Title = title;
        Description = description;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
