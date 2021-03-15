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
import com.example.firestoretestingapp.databinding.ActivityPart3Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class Part3Activity extends AppCompatActivity {

    private ActivityPart3Binding activityPart3Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";

    //    private DocumentReference noteRef = db.document("MyNoteBook/My First Note"); ///// both are same this is shorter
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");
    private DocumentReference noteRef = db.collection("MyNoteBook").document("My First Note");///// both are same this is Longer

    //***************************** NOTE ****************************************
    // FireStore Does Not Have an (OR) operation,
    // Also Firestore Does not have (Not Equals To) Operation
    // What We Have to do ? Know We Create Seperate Queries And Merge them Locally Thats What we Do In This PART
    // Lets Start!
    //___________________________________________________________________________

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart3Binding = ActivityPart3Binding.inflate(getLayoutInflater());
        setContentView(activityPart3Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle("RETRIEVE DATA FROM DOCUMENT");
        }

        activityPart3Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart3Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });


    }

    private void savenote() {
        String Title = activityPart3Binding.idET1.getText().toString().trim();
        String Description = activityPart3Binding.idET2.getText().toString().trim();

        if (activityPart3Binding.idET3.length() == 0) {
            activityPart3Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart3Binding.idET3.getText().toString());
        Note_2 note = new Note_2(Title, Description, periority);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part3Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part3Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void LoadNote() {
        // At This Way --> Task task1 = notebookRef.whereLessThan(KEY_Perority,2)
        //                .orderBy(KEY_Perority)
        //                .get(); // It Will Return Task<QuerySnapshot.
        //
        //     -->   Task task2 = notebookRef.whereGreaterThan(KEY_Perority,2)
        //                .orderBy(KEY_Perority)
        //                .get(); // It Will Return Task<QuerySnapshot.
        // We Can Add @ Independent Queries
        Task task1 = notebookRef.whereLessThan(KEY_Perority, 2)
                .orderBy(KEY_Perority)
                .get(); // It Will Return Task<QuerySnapshot.

        Task task2 = notebookRef.whereGreaterThan(KEY_Perority, 2)
                .orderBy(KEY_Perority)
                .get(); // It Will Return Task<QuerySnapshot.
        // Now We Combine Both Tasks Into ONE task
        // This (whenAllSuccess) give Results when All Tasks Done it works AS AND Operation
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String Data = "";
                for (QuerySnapshot value : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        Note_2 note = documentSnapshot.toObject(Note_2.class);
                        /// this can store ID of Perticular document
                        note.setDocumentID(documentSnapshot.getId());

                        String documentId = note.getDocumentID();
                        String title = note.getTitle();
                        String description = note.getDescription();
                        int perority = note.getPeriority();

                        Data += "Id = " + documentId + "\n" + KEY_Title + "= " + title + "\n" + KEY_Description + "= " + description + "\n" + KEY_Perority + "= " + perority + "\n\n";

                    }
                }
                activityPart3Binding.idTV1.setText(Data);

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
                if (error != null) {
                    return;
                }

                String Data = "";
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    Note_2 note = documentSnapshot.toObject(Note_2.class);
                    /// this can store ID of Perticular document
                    note.setDocumentID(documentSnapshot.getId());

                    String documentId = note.getDocumentID();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int perority = note.getPeriority();

                    Data += "Id = " + documentId + "\n" + KEY_Title + "= " + title + "\n" + KEY_Description + "= " + description + "\n" + KEY_Perority + "= " + perority + "\n\n";
                    activityPart3Binding.idTV1.setText(Data);


                }

            }
        });
    }
}