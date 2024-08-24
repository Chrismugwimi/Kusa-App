package eu.tutorials.myappkusa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class submit_id extends AppCompatActivity {

    private static final String TAG = "SubmitIDActivity";
    Uri imageUri;
    ProgressDialog progressDialog;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_id);

        drawerLayout = findViewById(R.id.drawer_layout3);
        navigationView = findViewById(R.id.nav_view3);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Handle navigation view item clicks here.
                    int id = item.getItemId();
                    // Handle your navigation logic here
                    return true;
                }
            });
        }

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Handle navigation view item clicks here.
                    int id = item.getItemId();
                    // Handle your navigation logic here
                    return true;
                }
            });

        findViewById(R.id.selectImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        findViewById(R.id.uploadImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        }



    private void uploadImage() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file...");
        progressDialog.show();

        // Get the currently signed-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            Log.d(TAG, "User ID: " + userId); // Debug log for user ID

            // Reference to the user's document in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String regNumber = documentSnapshot.getString("RegNumber");
                                String universityName = documentSnapshot.getString("UniversityName");

                                Log.d(TAG, "RegNumber: " + regNumber); // Debug log for RegNumber
                                Log.d(TAG, "UniversityName: " + universityName); // Debug log for UniversityName

                                if (regNumber != null && universityName != null) {
                                    // Construct the path for the file
                                    String path = "University Database/" + universityName + "/ID cards/" + regNumber;
                                    Log.d(TAG, "Upload path: " + path);

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);

                                    storageReference.putFile(imageUri)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Toast.makeText(submit_id.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(submit_id.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    Log.e(TAG, "Upload failed", e);
                                                }
                                            });
                                } else {
                                    Toast.makeText(submit_id.this, "RegNumber or UniversityName not found", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Log.e(TAG, "RegNumber or UniversityName is null");
                                }
                            } else {
                                Toast.makeText(submit_id.this, "User not found in database", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Log.e(TAG, "User not found in database");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(submit_id.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Log.e(TAG, "Database error: " + e.getMessage());
                        }
                    });
        } else {
            // User is not signed in, handle this case
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            Log.e(TAG, "User not signed in");
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Set selected image URI to ImageView for preview
            ImageView imageView = findViewById(R.id.imageView10); // Replace "imageView" with your actual ImageView ID
            imageView.setImageURI(imageUri);
        }
    }
}
