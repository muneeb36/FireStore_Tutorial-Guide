package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_2;
import com.example.firestoretestingapp.databinding.ActivityPart7Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

public class Part7Activity extends AppCompatActivity {

    private ActivityPart7Binding activityPart7Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart7Binding = ActivityPart7Binding.inflate(getLayoutInflater());
        setContentView(activityPart7Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("TRANSACTIONS");
        }
        // TRANSACTIONS is a set of read and write operations on one or more documents.

        activityPart7Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart7Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });

        ExecuteTRANSACTIONS();

    }

    private void savenote() {
        String Title = activityPart7Binding.idET1.getText().toString().trim();
        String Description = activityPart7Binding.idET2.getText().toString().trim();

        if (activityPart7Binding.idET3.length() == 0) {
            activityPart7Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart7Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title, Description, periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part7Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part7Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void ExecuteTRANSACTIONS(){
        // TRANSACTIONS Can Do 500 Operations At Once If we want to Do more Than 500 Then we need to Create multiple Batch
//        db.runTransaction(new Transaction.Function<Void>() { // We use Void in Object or type ,Beacause We dont Return anything thats why we use Void.
//            @Nullable
//            @Override
//            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
//                // Now here we can multiple read andWrite Operations
//                // ******* Note :IMPORTANT: # We Can't Pull read operations After Write Operations.
//                /// First We DO All our Read Opertations First Then All Our Write Operations Otherwise we Get Errorr!
//                DocumentReference exampleNoteRef = notebookRef.document("New Note"); // This New Note Where We read the Value From
//                DocumentSnapshot exampleNoteSnapshot = transaction.get(exampleNoteRef);// This is our Read Operation
//                // to update the Periority Feild of New Note Document
//                long newpariority = exampleNoteSnapshot.getLong(KEY_Perority)+1;
//
//                // Write operations
//                transaction.update(exampleNoteRef,KEY_Perority,newpariority);
//
//                return null;
//            }
//        });

        db.runTransaction(new Transaction.Function<Long>() { // We use Long in Object or type ,Beacause We want to retrive the return value
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                // Now here we can multiple read andWrite Operations
                // ******* Note :IMPORTANT: # We Can't Pull read operations After Write Operations.
                /// First We DO All our Read Opertations First Then All Our Write Operations Otherwise we Get Errorr!
                DocumentReference exampleNoteRef = notebookRef.document("New Note"); // This New Note Where We read the Value From
                DocumentSnapshot exampleNoteSnapshot = transaction.get(exampleNoteRef);// This is our Read Operation
                // to update the Periority Feild of New Note Document
                long newpariority = exampleNoteSnapshot.getLong(KEY_Perority)+1;

                // Write operations
                transaction.update(exampleNoteRef,KEY_Perority,newpariority);

                return newpariority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(Part7Activity.this, "New Periority = "+ result, Toast.LENGTH_SHORT).show();
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
                    activityPart7Binding.idTV1.append(Data); // This Time We Use append Which means we add new data in it without delete perivious data

                    lastResult = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() -1 ); // we use to safe last Id of Document

                }
                else
                {
                    Toast.makeText(Part7Activity.this, "No More Data", Toast.LENGTH_SHORT).show();
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