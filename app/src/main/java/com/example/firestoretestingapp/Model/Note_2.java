package com.example.firestoretestingapp.Model;

import com.google.firebase.firestore.Exclude;

public class Note_2 {
    private String DocumentID;
    private String Title;
    private String Description;
    private int Periority;


    public Note_2() {
    }

    public Note_2(String title, String description, int periority) {
        Title = title;
        Description = description;
        Periority = periority;
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
}
