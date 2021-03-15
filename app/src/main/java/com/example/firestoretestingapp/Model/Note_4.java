package com.example.firestoretestingapp.Model;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.Map;

public class Note_4 {
    private String DocumentID;
    private String Title;
    private String Description;
    private int Periority;
    Map<String,Boolean> tags;


    public Note_4() {
    }

    public Note_4(String title, String description, int periority, Map<String,Boolean> tags) {
        Title = title;
        Description = description;
        Periority = periority;
        this.tags = tags;
    }



    public int getPeriority() {
        return Periority;
    }

    public void setPeriority(int periority) {
        Periority = periority;
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

    // **** IMPORTANT *** ///////
    // this getDocumentID we dont want to apear in our Document FireStore Database
    // Beacause this could Store Redundant Data
    // So we @Exclude , this way it Wont Show in Our Document & it store ID when We Retrive from data from our FireStore Databse

    @Exclude
    public String getDocumentID() {
        return DocumentID;
    }

    public void setDocumentID(String documentID) {
        DocumentID = documentID;
    }

    public Map<String,Boolean> getTags() {
        return tags;
    }

    public void setTags(Map<String,Boolean> tags) {
        this.tags = tags;
    }
}
