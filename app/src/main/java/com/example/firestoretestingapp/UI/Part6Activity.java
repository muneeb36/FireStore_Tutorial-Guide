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
import com.example.firestoretestingapp.databinding.ActivityPart5Binding;
import com.example.firestoretestingapp.databinding.ActivityPart6Binding;
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
import com.google.firebase.firestore.WriteBatch;

public class Part6Activity extends AppCompatActivity {

    private ActivityPart6Binding activityPart6Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;

    //*** To done Operationns like add Update and remove Like Part 5 on big scale at ONCE is Called *BATCHED WRITES*
    // This Is Faster And we dont need to done operation on each member Batch Done the operations Atomically(At once)
    //NOTE : If One Of the Operation Of Bath iS Failed Non of operation is applied to others
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart6Binding = ActivityPart6Binding.inflate(getLayoutInflater());
        setContentView(activityPart6Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("BATCHED WRITES");
        }
        // BATCHED WRITES is a set of  write operations on one or more documents.


        activityPart6Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart6Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });

        ExecuteBatchWrite();

    }


    private void savenote() {
        String Title = activityPart6Binding.idET1.getText().toString().trim();
        String Description = activityPart6Binding.idET2.getText().toString().trim();

        if (activityPart6Binding.idET3.length() == 0) {
            activityPart6Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart6Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title, Description, periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part6Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part6Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void ExecuteBatchWrite(){
        // BAtch Can Do 500 Operations At Once If we want to Do more Than 500 Then we need to Create multiple Batch
        //
        WriteBatch writeBatch = db.batch();
        DocumentReference documentReference1 = notebookRef.document("New Note"); //  This will Create New Note with name of New Note
        writeBatch.set(documentReference1,new Note_2("New note Tiltle","New note Tiltle",1)); // this will Create the feileds of Note Model

        // Update batch Mathod
        //Example # 1
        // This is for If the Document is not exist and we update. Just For Practice.if its give error see bellow in button press section.
        //DocumentReference documentReference2 = notebookRef.document("Not existing Document");
        //Example # 2
        //then we add valid document path from consol the try this.
        DocumentReference documentReference2 = notebookRef.document("WQJyYFJ9qTbAEWJSX3F7");
        writeBatch.update(documentReference2,KEY_Title,"Updated note");

        // Delete batch Mathod
        // Example # 1: If Document Id is not valid then it will not del any document and also not give any error
        // DocumentReference documentReference3 = notebookRef.document("Not existing Document");// enter the name of the Document Here
        //Example # 2 : If the Valid Document id id provided then it deletes
        DocumentReference documentReference3 = notebookRef.document("t7XQskGqwTEkf3sLG9fh");// enter the name of the Document Here
//        DocumentReference documentReference3 = notebookRef.document("New Note");// enter the name of the Document Here
        writeBatch.delete(documentReference3);
        // Now its deletes the document

        // add or set batch Mathod
        DocumentReference documentReference4 = notebookRef.document("New Note 2"); //  This will Create New Note with name of New Note
        writeBatch.set(documentReference4,new Note_2("New note Tiltle 2","New note Tiltle 2",2)); // this will Create the feileds of Note Model

        // ** We Use Only failuare Msg Because Batch is not completed if any of command or operation is not succes then all operation is failure
        // and it is only succes when all operation or batchs are done Successfully.
        writeBatch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                activityPart6Binding.idTV1.setText(e.toString());
                // If it gives Error Becuse of //Update batch Mathod.Why?
                // Beacuse it does not exists and when its upload then it gives erorr. When one batch is not completed then all others also not done
                // until all batches are cimpletes successfully.

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
                    activityPart6Binding.idTV1.append(Data); // This Time We Use append Which means we add new data in it without delete perivious data

                    lastResult = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() -1 ); // we use to safe last Id of Document

                }
                else
                {
                    Toast.makeText(Part6Activity.this, "No More Data", Toast.LENGTH_SHORT).show();
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