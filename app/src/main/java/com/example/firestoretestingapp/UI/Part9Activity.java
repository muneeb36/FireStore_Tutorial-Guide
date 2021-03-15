package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note_4;
import com.example.firestoretestingapp.databinding.ActivityPart7Binding;
import com.example.firestoretestingapp.databinding.ActivityPart9Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Part9Activity extends AppCompatActivity {

    private ActivityPart9Binding activityPart9Binding;

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
        activityPart9Binding = ActivityPart9Binding.inflate(getLayoutInflater());
        setContentView(activityPart9Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("NESTED OBJECTS");
        }

        activityPart9Binding.idBTN.setOnClickListener(v -> {
            // * So When We Enter Data it will save again document name  with random id.
            savenote();

        });

        activityPart9Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });

        // Update Feild is updated at once if the feild is added inside the araay it does not add again and again.
//        UpdateAddNestedValue();


//        // Update Feild is updated at once if the feild is remove inside the araay it does not add again and again.
        UpdateDeleteNestedValue();



    }

    private void savenote() {
        String Title = activityPart9Binding.idET1.getText().toString().trim();
        String Description = activityPart9Binding.idET2.getText().toString().trim();

        if (activityPart9Binding.idET3.length() == 0) {
            activityPart9Binding.idET3.setText("0");
        }

        int periority = Integer.parseInt(activityPart9Binding.idET3.getText().toString());

        String Taginput = activityPart9Binding.idETTags.getText().toString().trim();
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

        //// For add Multiple Refrence Or Node
        // by passing Node Model To Add on Firestore
        notebookRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Part9Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part9Activity.this, "Note is Not Saved", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void UpdateAddNestedValue(){// update query checks if the document is available then its update otherwise it ignores
        notebookRef.document("IpWOjXtIgGJ2giKF4VTp")
//                .update(KEY_Tags+".tag1", false);

        // ****** For Practice If we have nested over nested values likke:
        // tags-> tag1-> nested_tag-> nested_tag2 :true we write like
        .update("tags.tag1.nested_tag.nested_tag2", true);

    }

    private void UpdateDeleteNestedValue(){// update query checks if the document is available then its update otherwise it ignores
        notebookRef.document("IpWOjXtIgGJ2giKF4VTp")
                // Note Remove the Element without the posiotion is not possible Because if multiple user  remove the same name of field then it make issue So thats Why we pass Value "New tag"
                .update(KEY_Tags+".tag1", FieldValue.delete());// Now this key tag1 which is inside the tags is deleted key and its value

    }

    private void LoadNote(){
        // Dot notation is use to get the nested or sub value
        // This .whereEqualTo("tags.tag1","true") query chaecks the Value tag1 which is inside the tags , the tag1 is true or not
        notebookRef.whereEqualTo("tags.tag1",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

                activityPart9Binding.idTV1.setText(Data);

            }
        });
    }


}