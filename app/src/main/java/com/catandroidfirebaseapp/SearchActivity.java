package com.catandroidfirebaseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.catandroidfirebaseapp.model.Cat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

/**
 * Created by mac on 27.02.17.
 */

public class SearchActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView mRecyclerView;
    private Button btnSearch;
    private EditText edtSearch;

    private DatabaseReference mDb;
    private Query qSearch;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinner = (Spinner) findViewById(R.id.spinner);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        edtSearch = (EditText) findViewById(R.id.edtSearch);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(SearchActivity.this, LoginActivity.class));
            finish();
        }

        mDb = FirebaseDatabase.getInstance().getReference().child("Cats");
        qSearch = mDb;

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = edtSearch.getText().toString();

                if(!data.equals("")) {
                    switch ((int) spinner.getSelectedItemId()) {
                        case 0:
                            SetAdapter(qSearch = mDb.orderByChild("name").equalTo(data));
                            break;
                        case 1:
                            try {
                                SetAdapter(mDb.orderByChild("age").equalTo(Long.valueOf(data)));
                            }catch (NumberFormatException e){
                                Toast.makeText(SearchActivity.this, "Age must be numerical", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 2:
                            SetAdapter(qSearch = mDb.orderByChild("breed").equalTo(data));
                            break;
                    }
                }
                else Toast.makeText(SearchActivity.this, "Please fill search field", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onStart() {
        super.onStart();

    }

    private void SetAdapter(Query qSearch){
        FirebaseRecyclerAdapter<Cat, MainActivity.PostViewHolder> fbAdapter = new FirebaseRecyclerAdapter<Cat, MainActivity.PostViewHolder>(Cat.class, R.layout.recycler_view, MainActivity.PostViewHolder.class, qSearch) {
            @Override
            protected void populateViewHolder(final MainActivity.PostViewHolder viewHolder, final Cat model, final int position) {
                viewHolder.setName(model.getName());
                viewHolder.setAge(model.getAge());
                viewHolder.setBreed(model.getBreed());
                viewHolder.setImgCat(model.getImage());
                viewHolder.setEditMode(model.getUid());

                viewHolder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.update(getRef(position));
                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getRef(position).removeValue();
                        FirebaseStorage.getInstance().getReference().child("CatImages").child(model.getImgName()).delete();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(fbAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View mView;
        final EditText pEditName;
        final EditText pEditAge;
        final EditText pEditBreed;

        final Button btnConfirm;

        final ImageButton btnEdit;
        final ImageButton btnDelete;

        final TextView pName;
        final TextView pAge;
        final TextView pBreed;
        final ImageView pImage;

        public PostViewHolder(View itemView){
            super(itemView);
            mView = itemView;

            pEditName = (EditText) mView.findViewById(R.id.edit_name);
            pEditAge = (EditText) mView.findViewById(R.id.edit_age);
            pEditBreed = (EditText) mView.findViewById(R.id.edit_breed);

            btnConfirm = (Button) mView.findViewById(R.id.btnConfirm);

            btnEdit = (ImageButton) mView.findViewById(R.id.btnEdit);
            btnDelete = (ImageButton) mView.findViewById(R.id.btnDelete);

            pName = (TextView) mView.findViewById(R.id.name);
            pAge = (TextView) mView.findViewById(R.id.age);
            pBreed = (TextView) mView.findViewById(R.id.breed);
            pImage = (ImageView) mView.findViewById(R.id.imgCat);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pEditName.setVisibility((pEditName.getVisibility() == View.VISIBLE)
                            ? View.GONE : View.VISIBLE);

                    pEditAge.setVisibility((pEditAge.getVisibility() == View.VISIBLE)
                            ? View.GONE : View.VISIBLE);

                    pEditBreed.setVisibility((pEditBreed.getVisibility() == View.VISIBLE)
                            ? View.GONE : View.VISIBLE);

                    btnConfirm.setVisibility((btnConfirm.getVisibility() == View.VISIBLE)
                            ? View.GONE : View.VISIBLE);
                }
            });
        }

        public void setName(String name){
            pName.setText("Name: " + name);
        }

        public void setAge(long age){
            pAge.setText("Age: " + String.valueOf(age));
        }

        public void setBreed(String breed){
            pBreed.setText("Breed: " + breed);
        }

        public void setImgCat(String image){
            Picasso.with(mView.getContext()).load(image).into(pImage);
        }

        public void setEditMode(String uid){
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if(auth.getCurrentUser().getUid().equals(uid)){
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }else{
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }
        }

        public void update(DatabaseReference post){
            String name = pEditName.getText().toString();
            String age = pEditAge.getText().toString();
            String breed = pEditBreed.getText().toString();

            if(!name.equals("")){
                post.child("name").setValue(name);
                pEditName.setText("");
            }

            if(!age.equals("")){
                post.child("age").setValue(Long.valueOf(age));
                pEditAge.setText("");
            }

            if(!breed.equals("")){
                post.child("breed").setValue(breed);
                pEditBreed.setText("");
            }

            pEditName.setVisibility((pEditName.getVisibility() == View.VISIBLE)
                    ? View.GONE : View.VISIBLE);

            pEditAge.setVisibility((pEditAge.getVisibility() == View.VISIBLE)
                    ? View.GONE : View.VISIBLE);

            pEditBreed.setVisibility((pEditBreed.getVisibility() == View.VISIBLE)
                    ? View.GONE : View.VISIBLE);

            btnConfirm.setVisibility((btnConfirm.getVisibility() == View.VISIBLE)
                    ? View.GONE : View.VISIBLE);
        }
    }
}
