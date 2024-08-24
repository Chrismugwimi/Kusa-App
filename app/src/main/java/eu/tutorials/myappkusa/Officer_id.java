package eu.tutorials.myappkusa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Officer_id extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<Image> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_id);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler);
        setSupportActionBar(toolbar);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);

        // Set the adapter for RecyclerView
        recyclerView.setAdapter(imageAdapter);

        // Handle item click events
        imageAdapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onClick(Image image) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(image.getUrl()), "image/*");
                startActivity(intent);
            }
        });

        // Fetch images from Firebase Storage
        loadImagesFromFirebase();
    }

    private void loadImagesFromFirebase() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                // Initialize a counter to keep track of the number of images loaded
                final int[] loadedImagesCount = {0};

                // Iterate over each item in the list
                for (StorageReference item : listResult.getItems()) {
                    // Create a new Image object
                    Image image = new Image();
                    image.setName(item.getName());

                    // Get the download URL for each image
                    item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // Set the URL and add the image to the list
                                image.setUrl(task.getResult().toString());
                                imageList.add(image);
                            } else {
                                Toast.makeText(Officer_id.this, "Failed to get URL: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                            // Increment the loaded images count
                            loadedImagesCount[0]++;

                            // Check if all images have been loaded
                            if (loadedImagesCount[0] == listResult.getItems().size()) {
                                // Notify the adapter only when all images have been loaded
                                imageAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Officer_id.this, "There was an error while getting IDs", Toast.LENGTH_SHORT).show();
            }
        });
    }}
