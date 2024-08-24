package eu.tutorials.myappkusa;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileInstitution;
    private Button logoutButton;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize FirebaseAuth and DatabaseReference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("officers").child(auth.getCurrentUser().getUid());

        // Initialize UI elements
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);
        profileInstitution = findViewById(R.id.profile_institution);
        logoutButton = findViewById(R.id.logout_button);

        // Fetch user data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("Name").getValue(String.class);
                    String email = dataSnapshot.child("Email").getValue(String.class);
                    String institution = dataSnapshot.child("Institution").getValue(String.class);

                    // Update UI
                    profileName.setText(name);
                    profileEmail.setText(email);
                    profileInstitution.setText(institution);

                    // Optionally load the profile image if you have the URL
                    // Glide.with(ProfileActivity.this).load(profileImageUrl).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });

        // Handle logout button click
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            // Redirect to login activity
            // startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            // finish();
        });
    }
}
