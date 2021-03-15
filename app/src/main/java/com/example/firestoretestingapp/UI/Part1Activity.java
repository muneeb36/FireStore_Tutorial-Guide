package com.example.firestoretestingapp.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.firestoretestingapp.Model.Note;
import com.example.firestoretestingapp.databinding.ActivityMain2Binding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Part1Activity extends AppCompatActivity {

    ///// MainActivity2 XML //////////////
    private ActivityMain2Binding activityMain2Binding ;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static String KEY_Title = "Title";
    private static String KEY_Description = "Description";

    //    private DocumentReference noteRef = db.document("MyNoteBook/My First Note"); ///// both are same this is shorter
    private DocumentReference noteRef = db.collection("MyNoteBook").document("My First Note");///// both are same this is Longer


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMain2Binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(activityMain2Binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setTitle("WHAT IS CLOUD FIRESTORE?");
        }


        activityMain2Binding.idBTN.setOnClickListener(v -> {
            savenote();

        });

        activityMain2Binding.idLoadBTN.setOnClickListener(v -> {
            LoadNote();

        });
        activityMain2Binding.idUpdateDescriptionBTN.setOnClickListener(v -> {
            updateDescription();

        });

        activityMain2Binding.idDeleteDescriptionBTN.setOnClickListener(v -> {
            deleteDescription();

        });
        activityMain2Binding.idDeleteNoteBTN.setOnClickListener(v -> {
            // To Delete the Whole Note
            // After the MyBook -> Which is Collection
            deleteNote(); // This is Delete the Main Documnet and All the sub Documents and Collections


        });

    }

    ////// IMPORTANT /////////////////
    /////////////////////// This is use for automatically fetch data when data uplaods and gets without load mathos calls
    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this , new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null)
                {
                    Toast.makeText(Part1Activity.this, "Error aWHile Loading ", Toast.LENGTH_SHORT).show();
                    Log.e("Value",error.toString());
                    return;
                }
                if(value.exists())
                {
//                    ///// 1) Without Model Get Data
//                    //Getting Each feild
//                    String Title = value.getString("Title");
//                    String Description = value.getString("Description");
//                    activityMainBinding.idTV1.setText("Title : " + Title + "\n"+"Description : " + Description );

                    ////// 2) Get Data With DataModel like Note.class
                    // this way get all the Model Data from Document instaed of getting each feild
                    Note note = value.toObject(Note.class);
                    activityMain2Binding.idTV1.setText("Title : " + note.getTitle() + "\n"+"Description : " + note.getDescription() );
                }
                else
                {
                    activityMain2Binding.idTV1.setText("");
                }
            }
        });
    }
    ///////////////////////

    private void savenote()
    {
        String Title  = activityMain2Binding.idET1.getText().toString().trim();
        String Description = activityMain2Binding.idET2.getText().toString().trim();

        ////// To Put the Data  is available on Console
        // 1) Key and its Value
//        Map<String , Object> note  = new HashMap<>();
//        note.put(KEY_Title,Title );
//        note.put(KEY_Description ,Description );

        // 2) Also add Using Model Like Note.class
        Note note = new Note(Title,Description);

        /////////////// This is shorter path
        ///////////////// This way also created MynoteBook As Collection And gets / after Document as My First Note
//        db.document("MyNoteBook/My First Note");
//        db.collection("MyNoteBook").document("My First Note").set(note)
        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Part1Activity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Part1Activity.this, "Note Not Saved", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    public void LoadNote()
    {

        ////// To get load the Data  is available on Console againsts Key and its Value
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    ///// 1) Without Model Get Data
                    //Getting Each feild
//                    String Title = documentSnapshot.getString("Title");
//                    String Description = documentSnapshot.getString("Description");
////                    Map<String , Object> note  = documentSnapshot.getData();

                    ////// 2) Get Data With DataModel like Note.class
                    // this way get all the Model Data from Document instaed of getting each feild
                    Note note = documentSnapshot.toObject(Note.class);

                    activityMain2Binding.idTV1.setText("Title : " + note.getTitle() + "\n"+"Description : " + note.getDescription() );
                }
                else
                {
                    Toast.makeText(Part1Activity.this, "Document Not Exists", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part1Activity.this, "Error!" ,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateDescription ()
    {
        String Description = activityMain2Binding.idET2.getText().toString().trim();

        //// 1  **** NOTE ////////////////////////////
        /////////// At this Way The Only Description Added and The Title is Removed from Document
        // So We Do note USe this to Update because this is merged the data and replaced it ////
        /// so we don't use this at this time for update
//        Map<String , Object> note = new HashMap<>();
//        note.put(KEY_Description , Description);
//        noteRef.set(note);
        ///////////////////////////////////////////////////////////////////////////////////////

        /// 2   so we use for  update is merge
        // if All data is deleted from consol and this comamnd run it will create on Description and put value in Description
        // so this is also not good for use beacuse it is merging
//        Map<String , Object> note = new HashMap<>();
//        note.put(KEY_Description , Description);
//        noteRef.set(note, SetOptions.merge()); // this will only update the given value means on update Description and not del Tilte

        /// 3 So This will Onlt Update the GIven Key Like Description And
        // if Document Deleted from Consol it wil not created the Key and value
        // it is only updateds if KEy is exists
        noteRef.update(KEY_Description,Description);

    }

    private void deleteDescription ()
    {
        //// 1 This Way we del the Feild And then Update the Feild in Document
//        Map<String , Object> note = new HashMap<>();
//        note.put(KEY_Description, FieldValue.delete());
//        noteRef.update(KEY_Description,Description);

        //// 2 This Way we  also del the Feild And then Update the Feild in Document
        noteRef.update(KEY_Description, FieldValue.delete()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }
        });



    }

    // To Delete the Whole Note
    private void deleteNote ()
    {
        noteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Part1Activity.this, "Success", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Part1Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}