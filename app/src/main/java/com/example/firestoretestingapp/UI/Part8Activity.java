package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note;
import com.example.firestoretestingapp.Model.Note_2;
import com.example.firestoretestingapp.Model.Note_3;
import com.example.firestoretestingapp.databinding.ActivityPart8Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.gson.internal.bind.DateTypeAdapter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class Part8Activity extends AppCompatActivity {

    private ActivityPart8Binding activityPart8Binding;

    private static String KEY_Title = "title";
    private static String KEY_Description = "description";
    private static String KEY_Perority = "periority";
    private static String KEY_Tags = "tags";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("MyNoteBook");

    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPart8Binding = ActivityPart8Binding.inflate(getLayoutInflater());
        setContentView(activityPart8Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("ARRAYS ");
        }

        activityPart8Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart8Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });

        // Update Feild is updated at once if the feild is added inside the araay it does not add again and again.
//        UpdateAddArray();

        // Update Feild is updated at once if the feild is remove inside the araay it does not add again and again.
        UpdateRemoveArray();


    }

    private void savenote() {
        String Title = activityPart8Binding.idET1.getText().toString().trim();
        String Description = activityPart8Binding.idET2.getText().toString().trim();

        if (activityPart8Binding.idET3.length() == 0) {
            activityPart8Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart8Binding.idET3.getText().toString());

        String Taginput = activityPart8Binding.idETTags.getText().toString().trim();
        String[] tagArray = Taginput.split("\\s*,\\s*");// This Regular Expression Remove the Empty Spaces at the begining and end to string

        /// *** Note FireBase Does NotSupport Array Thats Why We Use List
        List<String> tags = Arrays.asList(tagArray);

        Note_3 note = new Note_3(Title, Description, periority,tags);

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part8Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part8Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void UpdateAddArray(){// update query checks if the document is available then its update otherwise it ignores
        notebookRef.document("Q4IOa8cQsTTGJPUhtU95")
                .update(KEY_Tags, FieldValue.arrayUnion("new Tag"));//arrayUnion Adds the Feild insiide the array in specific document
    }

    private void UpdateRemoveArray(){// update query checks if the document is available then its update otherwise it ignores
        notebookRef.document("Q4IOa8cQsTTGJPUhtU95")
                // Note Remove the Element without the posiotion is not possible Because if multiple user  remove the same name of field then it make issue So thats Why we pass Value "New tag"
                .update(KEY_Tags,FieldValue.arrayRemove("new Tag"));
    }

    private void LoadNote(){
        notebookRef.whereArrayContains(KEY_Tags,"tag5").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String Data = "";

                for (QueryDocumentSnapshot documentSnapshot :queryDocumentSnapshots)
                {
                    Note_3 note_3 = documentSnapshot.toObject(Note_3.class);
                    note_3.setDocumentID(documentSnapshot.getId());

                    String documentId = note_3.getDocumentID();

                    Data += "Id = " + documentId ;

                    for (String tags :note_3.getTags())
                    {
                        Data +="\n -" + tags;
                    }

                    Data +="\n\n";
                }

                activityPart8Binding.idTV1.setText(Data);

            }
        });
    }
    
    
}