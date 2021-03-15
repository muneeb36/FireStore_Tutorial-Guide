package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_4;
import com.example.firestoretestingapp.databinding.ActivityPart10Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Part10Activity extends AppCompatActivity {

    private ActivityPart10Binding activityPart10Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";
    private static String KEY_Tags = "tags";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;

    // ***** Note
    /// In thsi Part We use this mOdel to add Collection and Document in it and add collection inside the Document like sub category
    // This is like Real world app like To Do App
    // We Create MultiLevel Collection By This You Can Be Able To Crate Application Completely on FirStore DB.
    // Like :                                   Collection
    //                                  |-----------------------------------|
    //                                  |   Document1,Document2,Document3   |
    //                                  |-----------------------------------|
    //                                    /                               \
    //                             Collection                          Collection
    //           |-----------------------------------|               |-----------------------------------|
    //           |   Document1,Document2,Document3   |              |   Document1,Document2,Document3   |
    //           |-----------------------------------|              |-----------------------------------|

    //   This is the Structure Like We Create In This Practice


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart10Binding = ActivityPart10Binding.inflate(getLayoutInflater());
        setContentView(activityPart10Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("SUBCOLLECTIONS AND CONSIDERATIONS");
        }


        activityPart10Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart10Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });



    }



    private void savenote() {
        String Title = activityPart10Binding.idET1.getText().toString().trim();
        String Description = activityPart10Binding.idET2.getText().toString().trim();

        if (activityPart10Binding.idET3.length() == 0) {
            activityPart10Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart10Binding.idET3.getText().toString());

        String Taginput = activityPart10Binding.idETTags.getText().toString().trim();
        String[] tagArray = Taginput.split("\\s*,\\s*");// This Regular Expression Remove the Empty Spaces at the begining and end to string

        /// *** Note FireBase Does NotSupport Array Thats Why We Use HashMap to Add Remove Or Update the Nested Value agaist any key
        Map<String,Boolean> tags = new HashMap<>();

        /// This how we get the feild data
        // so this could achive the Array of string TagArray added as Value and the sub value is added as Bollean
        for(String tag:tagArray)
        {
            tags.put(tag,true);
        }


        Note_4 note = new Note_4(Title, Description, periority,tags);

        notebookRef.document("New Note")// on this Documentt we can add collection
                .collection("Child Note")// This is Child node of new Note
        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
       .add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part10Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part10Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void LoadNote(){

        notebookRef.document("New Note").collection("Child Note")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String Data = "";

                for (QueryDocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                {
                    Note_4 Note_4 = documentSnapshot.toObject(Note_4.class);
                    Note_4.setDocumentID(documentSnapshot.getId());

                    String documentId = Note_4.getDocumentID();

                    Data += "Id = " + documentId ;

                    for (String tags :Note_4.getTags().keySet())
                    {
                        Data +="\n -" + tags;
                    }

                    Data +="\n\n";
                }

                activityPart10Binding.idTV1.setText(Data);

            }
        });
    }
    
    
}