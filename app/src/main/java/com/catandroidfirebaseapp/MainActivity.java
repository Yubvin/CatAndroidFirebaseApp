package com.catandroidfirebaseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.catandroidfirebaseapp.model.Cat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mDb;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDb = FirebaseDatabase.getInstance().getReference().child("Cats");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Cat, PostViewHolder> fbAdapter = new FirebaseRecyclerAdapter<Cat, PostViewHolder>(Cat.class, R.layout.recycler_view, PostViewHolder.class, mDb) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Cat model, final int position) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_add:
                intent = new Intent(this, AddActivity.class);
                startActivityForResult(intent, 0);
                return true;


            case R.id.action_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

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

                    pEditName.setText("");
                    pEditAge.setText("");
                    pEditBreed.setText("");

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
