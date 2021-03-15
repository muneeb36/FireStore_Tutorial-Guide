package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_2;
import com.example.firestoretestingapp.databinding.ActivityMain3Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Part2Activity extends AppCompatActivity {

    ///// MainActivity2 XML //////////////
    private ActivityMain3Binding activityMain3Binding ;


    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";

    //    private DocumentReference noteRef = db.document("MyNoteBook/My First Note"); ///// both are same this is shorter
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");
    private DocumentReference noteRef = db.collection("MyNoteBook").document("My First Note");///// both are same this is Longer



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMain3Binding = ActivityMain3Binding.inflate(getLayoutInflater());
        setContentView(activityMain3Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle("SETTING UP FIREBASE & FIRST DOCUMENT");
        }


        activityMain3Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityMain3Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });

    }

    private void savenote()
    {
        String Title  = activityMain3Binding.idET1.getText().toString().trim();
        String Description = activityMain3Binding.idET2.getText().toString().trim();

        if(activityMain3Binding.idET3.length() == 0)
        {
            activityMain3Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityMain3Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title,Description,periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part2Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part2Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadNote()
    {
        //// ****************** Queries ****************************/////////////
        // Part 1
        // Queries like = whereEqualTo("Feild Name",value) ,
        // whereGreaterThanOrEqualTo("Feild Name",value) <- this way is auto listed as AESENDING Order
        // if we want to make DESENDING Order use -> // whereGreaterThanOrEqualTo("Feild Name",value).orderBy(KEY_Perority, Query.Direction.DESCENDING)
        //// So You Want to get first 3 or as many results you want so use
        //  -> .limit(3) after the desired query.
        // Example = notebookRef.whereGreaterThanOrEqualTo(KEY_Perority,3).orderBy(KEY_Perority, Query.Direction.DESCENDING).limit(2)
        //                .get()
        // ******** NOTE ************* --> This Query is run Against Multiple Collections
        // --------------------------------------------------------------------------------------------------------------------------------------
        // Part 2: Now Create Multiple Queries which CAlled ( COMPOUND QUERIES ) to Filtrate the Results
        // like --> notebookRef.whereGreaterThanOrEqualTo(KEY_Perority,2)
        //                .whereEqualTo(KEY_Title,KEY_Title)
        //                .get()
        //---------------------------------------------------------------------------------------------------------------------------------------
        // **** IMP *** ==> Afetr this Compound query You can get error to get error use addOnFailureListener The Click on
        // Link to turn on Indexing or GOTO Console -> Cloud Firestore -> Index -> Create index
        // SO notebookRef.whereGreaterThanOrEqualTo(KEY_Perority,2)
        //                .whereEqualTo(KEY_Title,KEY_Title)
        //                .orderBy(KEY_Perority, Query.Direction.DESCENDING)
        //                .get()
        // Now We USe Oder by 2 times but as we setting periority of view Asending of TITLE And Periority to Decending Oder
        //---------------------------------------------------------------------------------------------------------------------------------------
        // **************** IMP NOTE **************************
        // * Single Feilds Are indexed by Default thats why its worked with single Query like = > .whereGreaterThanOrEqualTo(KEY_Perority,2)
        // When we use Custom Index? ANS= IF we Use Whereto Query it work without Custom indexes But
        //we needs only Custom Index if we combine range Opertors (OrderBy) with rare Where to oPerators
        // Or We Combine Multiple Order Bytes Like
        // ==> notebookRef.whereGreaterThanOrEqualTo(KEY_Perority,2)
        //                .orderBy(KEY_Perority)
        //                .orderBy(KEY_Title)
        //                .get()
        //---------------------------------------------------------------------------------------------------------------------------------------
        notebookRef.whereGreaterThanOrEqualTo(KEY_Perority,2)
                .orderBy(KEY_Perority)
                .orderBy(KEY_Title)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override //** QuerySnapshot is contain Single document snapshot.This is basically our whole collection
                        // this collection we have multiple document snapshot which represent One note object
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String Data = "";

                //        *****  NOTE ********     ////
                //QueryDocumentSnapshot is subclass of DocumentSnapshot.
                // QueryDocumentSnapshot is Guarenteed to exists.Which means QueryDocumentSnapshot is always Exists.
                // we use before if(value.exists()) so we sure this time value or document is always exists.
                for(QueryDocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                {
                    Note_2 note = documentSnapshot.toObject(Note_2.class);
                    /// this can store ID of Perticular document
                    note.setDocumentID(documentSnapshot.getId());

                    String documentId = note.getDocumentID();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int perority = note.getPeriority();

                    Data += "Id  "+documentId+"\n"+KEY_Title+"= "+title+"\n"+KEY_Description+"= "+description +"\n"+KEY_Perority+"= "+perority+"\n\n";
                    activityMain3Binding.idTV1.setText(Data);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part2Activity.this, "Eorr! to get Note", Toast.LENGTH_SHORT).show();
                Log.e("Error",e.toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ///////////////// Auto Retrive Data When Any this is change like We Load The data by pressing Load BTN
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    return;
                }

                String Data = "";
                for(QueryDocumentSnapshot documentSnapshot :value)
                {
                    Note_2 note = documentSnapshot.toObject(Note_2.class);
                    /// this can store ID of Perticular document
                    note.setDocumentID(documentSnapshot.getId());

                    String documentId = note.getDocumentID();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int perority = note.getPeriority();

                    Data += "Id = "+documentId+"\n"+KEY_Title+"= "+title+"\n"+KEY_Description+"= "+description +"\n"+KEY_Perority+"= "+perority+"\n\n";
                    activityMain3Binding.idTV1.setText(Data);


                }

            }
        });
    }
}