package com.catandroidfirebaseapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.OnDisconnect;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

/**
 * Created by mac on 25.02.17.
 */

public class AddActivity extends AppCompatActivity {

    private ImageButton btnImgCat;

    private EditText edtName;
    private EditText edtAge;
    private EditText edtBreed;

    private Button btnAdd;
    private FirebaseAuth auth;

    private Uri selectedImage = null;

    private StorageReference mStorage;
    private DatabaseReference mDb;

    private ProgressDialog mPd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add);

        auth = FirebaseAuth.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference();
        mDb = FirebaseDatabase.getInstance().getReference().child("Cats");

        btnImgCat = (ImageButton) findViewById(R.id.imgButton);

        edtName = (EditText) findViewById(R.id.edt_name);
        edtAge = (EditText) findViewById(R.id.edt_age);
        edtBreed = (EditText) findViewById(R.id.edt_breed);

        btnAdd = (Button) findViewById(R.id.button_add);

        mPd = new ProgressDialog(this);

        btnImgCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
                intentGallery.setType("image/*");
                startActivityForResult(intentGallery, 0);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = edtName.getText().toString();
                final long age = Short.valueOf(edtAge.getText().toString());
                final String breed = edtBreed.getText().toString();

                if(!name.equals("") && !breed.equals("")){
                    mPd.setMessage("Downloading cat ...");
                    mPd.show();

                    if(selectedImage!=null){
                        final String imgName = random();
                        StorageReference filepath = mStorage.child("CatImages").child(imgName);
                        filepath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                DatabaseReference newCat = mDb.push();

                                newCat.child("name").setValue(name);
                                newCat.child("age").setValue(age);
                                newCat.child("breed").setValue(breed);
                                newCat.child("imgName").setValue(imgName);
                                newCat.child("image").setValue(downloadUrl.toString());
                                newCat.child("uid").setValue(auth.getCurrentUser().getUid());
                                mPd.dismiss();
                                finish();
                            }
                        });
                    }else{
                        DatabaseReference newCat = mDb.push();

                        newCat.child("name").setValue(name);
                        newCat.child("age").setValue(age);
                        newCat.child("breed").setValue(breed);
                        newCat.child("image").setValue("https://firebasestorage.googleapis.com/v0/b/catandroidfirebaseapp.appspot.com/o/CatImages%2Fdefault-cat.jpg?alt=media&token=9358c8b8-546e-441b-9162-6f6549288bc6");
                        newCat.child("uid").setValue(auth.getCurrentUser().getUid());

                        mPd.dismiss();
                        finish();
                    }

                }else{
                    Toast.makeText(AddActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    selectedImage = data.getData();
                    btnImgCat.setImageURI(selectedImage);
                }
                break;
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(100);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
