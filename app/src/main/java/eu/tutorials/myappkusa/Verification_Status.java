package eu.tutorials.myappkusa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class Verification_Status extends AppCompatActivity {

    private TextView statusTextView;
    private TextView messageTextView;
    private Button refreshButton;
    private CardView statusCardView;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListenerRegistration profileListener;
    private DatabaseReference realtimeDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_status);

        // Initialize UI elements
        statusTextView = findViewById(R.id.statusTextView);
        messageTextView = findViewById(R.id.messageTextView);
        refreshButton = findViewById(R.id.refreshButton);
        statusCardView = findViewById(R.id.statusCardView);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Handle scenario where user is not authenticated
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStudentVerificationStatus();
            }
        });

        // Start listening to changes in user profile
        startProfileListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop listening to changes in user profile
        if (profileListener != null) {
            profileListener.remove();
        }
    }

    private void startProfileListener() {
        if (currentUser != null) {
            // Listen to changes in user profile
            profileListener = db.collection("users").document(currentUser.getUid())
                    .addSnapshotListener(this, (documentSnapshot, e) -> {
                        if (e != null) {
                            // Handle error
                            Toast.makeText(Verification_Status.this, "Error fetching profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            fetchStudentVerificationStatus();
                        } else {
                            Toast.makeText(Verification_Status.this, "User profile not found", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void fetchStudentVerificationStatus() {
        // Show progress to the user
        statusTextView.setText("Checking status...");
        messageTextView.setText("");

        // Fetch the student's profile from Firestore
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String sport = document.getString("SportPlayed");
                            String institutionName = document.getString("UniversityName");
                            String registrationNumber = document.getString("RegNumber");

                            if (sport != null && institutionName != null && registrationNumber != null) {
                                // Initialize Realtime Database reference to fetch verification status
                                realtimeDb = FirebaseDatabase.getInstance().getReference("Teams")
                                        .child(sport).child(institutionName).child(registrationNumber);

                                realtimeDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String studentName = dataSnapshot.child("Name").getValue(String.class);
                                            Boolean isVerified = dataSnapshot.child("verified").getValue(Boolean.class);

                                            if (studentName != null && isVerified != null) {
                                                statusTextView.setText("Student: " + studentName);
                                                if (isVerified) {
                                                    messageTextView.setText("You are verified to participate in " + sport);
                                                    messageTextView.setTextColor(getResources().getColor(R.color.green));
                                                } else {
                                                    messageTextView.setText("You are Not Verified");
                                                    messageTextView.setTextColor(getResources().getColor(R.color.red));
                                                }
                                            } else {
                                                statusTextView.setText("Error retrieving status");
                                                messageTextView.setText("Incomplete data");
                                                messageTextView.setTextColor(getResources().getColor(R.color.red));
                                            }
                                        } else {
                                            statusTextView.setText("Student ID not found");
                                            messageTextView.setText("Please check your ID and try again.");
                                            messageTextView.setTextColor(getResources().getColor(R.color.red));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        statusTextView.setText("Error retrieving status");
                                        messageTextView.setText("Database error: " + databaseError.getMessage());
                                        messageTextView.setTextColor(getResources().getColor(R.color.red));
                                        Toast.makeText(Verification_Status.this, "Failed to fetch data from database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                statusTextView.setText("Error retrieving profile");
                                messageTextView.setText("Profile data is incomplete");
                                messageTextView.setTextColor(getResources().getColor(R.color.red));
                            }
                        } else {
                            statusTextView.setText("Profile not found");
                            messageTextView.setText("Please complete your profile");
                            messageTextView.setTextColor(getResources().getColor(R.color.red));
                        }
                    } else {
                        statusTextView.setText("Error retrieving profile");
                        messageTextView.setText("Database error: " + task.getException().getMessage());
                        messageTextView.setTextColor(getResources().getColor(R.color.red));
                        Toast.makeText(Verification_Status.this, "Failed to fetch data from database", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
