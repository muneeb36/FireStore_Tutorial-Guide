package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_2;
import com.example.firestoretestingapp.databinding.ActivityPart4Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Part4Activity extends AppCompatActivity {

    private ActivityPart4Binding activityPart4Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart4Binding = ActivityPart4Binding.inflate(getLayoutInflater());
        setContentView(activityPart4Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle("Pagination");
        }

        activityPart4Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart4Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });



    }

    private void savenote() {
        String Title = activityPart4Binding.idET1.getText().toString().trim();
        String Description = activityPart4Binding.idET2.getText().toString().trim();

        if (activityPart4Binding.idET3.length() == 0) {
            activityPart4Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart4Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title, Description, periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part4Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part4Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadNote(){
        // startAt is includes the value like 3
        // startAfter includes value after the 3 as example
        // Example #1
        // .orderBy(KEY_Perority).startAt(3).get --> Means You can get Data Filtrated Periory includes and after 3 Like GraeterThanEqaulsTo
        // Example #2
        //.orderBy(KEY_Perority).orderBy(KEY_Title).startAt(3,KEY_Title+" 2")
        // Means You can get Data Filtrated By Periority = 3 and Tilte 2 So This WIll Work Like Or Operator This Query Results of TITle 2 with Pariority 3 and greater then 3
        // Example #3: This Example we search the Document id and start after this Id to onward by This COde:
//        notebookRef.document("OkGXOLRdaRQgAENHkopq")
//                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                notebookRef.orderBy(KEY_Perority)
//                        .startAt(documentSnapshot)
//                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        String Data = "";
//                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
//                        {
//                            Note_2 note_2 = documentSnapshot.toObject(Note_2.class);
//                            note_2.setDocumentID(documentSnapshot.getId());
//
//                            String documentId = note_2.getDocumentID();
//                            String title = note_2.getTitle();
//                            String description = note_2.getDescription();
//                            int perority = note_2.getPeriority();
//
//                            Data += "Id = " + documentId + "\n" + KEY_Title + "= " + title + "\n" + KEY_Description + "= " + description + "\n" + KEY_Perority + "= " + perority + "\n\n";
//                        }
//                        activityPart4Binding.idTV1.setText(Data);
//                    }
//                });
//            }
//        });
// -----------------------------------------------------------------------------------------------------------------------------------
        // Exmple #4: Now We one Pagination in this Example
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
                    activityPart4Binding.idTV1.append(Data); // This Time We Use append Which means we add new data in it without delete perivious data

                    lastResult = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() -1 ); // we use to safe last Id of Document

                }
                else
                {
                    Toast.makeText(Part4Activity.this, "No More Data", Toast.LENGTH_SHORT).show();
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