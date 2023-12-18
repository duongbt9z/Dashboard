package com.example.dashboard.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dashboard.Adapter.AddressAdapter;
import com.example.dashboard.Domain.AddressDomain;
import com.example.dashboard.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress{
    private Button addressBtn;
    private RecyclerView recyclerView;
    ArrayList<AddressDomain> listAddress;
    private AddressAdapter addressAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    private ImageView backBtn;
    String mAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        addressBtn = findViewById(R.id.addressBtn);
        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.addressView);

        initList();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, AddAdressActivity.class));
            }
        });
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listAddress = new ArrayList<>();
        addressAdapter = new AddressAdapter( listAddress, getApplicationContext(),this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("Users").document(auth.getCurrentUser().getUid())
                .collection("Address").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }

                        listAddress.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            AddressDomain addressDomain = doc.toObject(AddressDomain.class);
                            listAddress.add(addressDomain);
                        }
                        addressAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public void setAddress(String address) {
        mAddress = address;
    }
}