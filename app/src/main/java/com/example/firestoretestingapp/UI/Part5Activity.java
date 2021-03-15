package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_2;
import com.example.firestoretestingapp.R;
import com.example.firestoretestingapp.databinding.ActivityPart4Binding;
import com.example.firestoretestingapp.databinding.ActivityPart5Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Part5Activity extends AppCompatActivity {

    private ActivityPart5Binding activityPart5Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart5Binding = ActivityPart5Binding.inflate(getLayoutInflater());
        setContentView(activityPart5Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("DOCUMENT CHANGES");
        }


        activityPart5Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart5Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.e("Error!",error.toString());
                    return;
                }
                // ****************  Del Alll the nodes collection and document for this Practice *************
                // to tell about what , which and how many are change we use DocumentChange
                // ********** After Run The App Preass Load Without Add any thing then You see
                //*** First Time The Old Index wil be -1 And new Index will be 0
                

                for(DocumentChange documentChange : value.getDocumentChanges())
                {
                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
                    String DocumentID = documentSnapshot.getId();
                    int oldindex = documentChange.getOldIndex();
                    int newindex = documentChange.getNewIndex();

                    switch (documentChange.getType())
                    {
                        case ADDED:
                            activityPart5Binding.idTV1.append("\nADDED: "+DocumentID +"\nOld Index: "+oldindex+"\nNew Index: "+newindex );
                            break;
                        case REMOVED:
                            activityPart5Binding.idTV1.append("\nREMOVED: "+DocumentID +"\nOld Index: "+oldindex+"\nNew Index: "+newindex );
                            break;
                        case MODIFIED:
                            activityPart5Binding.idTV1.append("\nMODIFIED: "+DocumentID +"\nOld Index: "+oldindex+"\nNew Index: "+newindex );
                            break;
                    }
                }
            }
        });
    }

    private void savenote() {
        String Title = activityPart5Binding.idET1.getText().toString().trim();
        String Description = activityPart5Binding.idET2.getText().toString().trim();

        if (activityPart5Binding.idET3.length() == 0) {
            activityPart5Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart5Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title, Description, periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part5Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part5Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadNote(){

        Query query;
        if(lastResult == null) // this means we dont have retrive any data yet
        {
            query = notebookRef.orderBy(KEY_Perority)
                    .limit(3);// we want to show only 3 results on screen
        }else
        {
            query = notebookRef.orderBy(KEY_Perority)
                    .startAfter(lastResult)
                    .limit(3);// we want to show only 3 results on screen
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                String Data = "";
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    Note_2 note_2 = documentSnapshot.toObject(Note_2.class);
                    note_2.setDocumentID(documentSnapshot.getId());

                    String documentId = note_2.getDocumentID();
                    String title = note_2.getTitle();
                    String description = note_2.getDescription();
                    int perority = note_2.getPeriority();

                    Data += "Id = " + documentId + "\n" + KEY_Title + "= " + title + "\n" + KEY_Description + "= " + description + "\n" + KEY_Perority + "= " + perority + "\n\n";

                }
                if(queryDocumentSnapshots.size() > 0 ) // if queryDocumentSnapshots Size is Bigger then 0.Beacuase we execute the query if queryDocumentSnapshots is bigger the 0.
                {

                    Data += "____________________\n\n";
                    activityPart5Binding.idTV1.append(Data); // This Time We Use append Which means we add new data in it without delete perivious data

                    lastResult = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() -1 ); // we use to safe last Id of Document

                }
                else
                {
                    Toast.makeText(Part5Activity.this, "No More Data", Toast.LENGTH_SHORT).show();
                }

//                    if (queryDocumentSnapshots.size() == 1)
//                    {
//                        // Save last document
//                        lastResult = queryDocumentSnapshots.getDocuments()
//                                .get(0 ); // we use to safe last Id of Document
//                    }
//                    else if (queryDocumentSnapshots.size() == 0)
//                    {
//                        Toast.makeText(Part4Activity.this, "No More Data", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//                        // Save last document
//                        lastResult = queryDocumentSnapshots.getDocuments()
//                                .get(queryDocumentSnapshots.size() -1 ); // we use to safe last Id of Document
//                    }


            }
        });

    }

}




