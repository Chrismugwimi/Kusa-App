package eu.tutorials.myappkusa;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Teams extends AppCompatActivity {

    private Spinner sportSpinner, universitySpinner;
    private Button loadTeamsButton;
    private ListView teamListView;

    private DatabaseReference databaseReference;
    private ArrayAdapter<String> teamListAdapter;
    private List<String> teamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        // Initialize views
        sportSpinner = findViewById(R.id.sportSpinner);
        universitySpinner = findViewById(R.id.universitySpinner);
        loadTeamsButton = findViewById(R.id.loadTeamsButton);
        teamListView = findViewById(R.id.teamListView);

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Teams");

        // Initialize team list and adapter
        teamList = new ArrayList<>();
        teamListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teamList);
        teamListView.setAdapter(teamListAdapter);

        // Load data into spinners
        loadSpinnerData();

        // Set click listener for Load Teams button
        loadTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTeamList();
            }
        });
    }

    private void loadSpinnerData() {
        // Load data into sportSpinner and universitySpinner
        List<String> sports = new ArrayList<>();
        sports.add("Basketball");
        sports.add("Football");
        sports.add("Volleyball");
        sports.add("Chess");
        sports.add("Basketball");

        ArrayAdapter<String> sportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sports);
        sportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(sportAdapter);

        List<String> universities = new ArrayList<>();
        universities.add("Dedan Kimathi University");
        universities.add("Chuka University");
        universities.add("Embu University");

        ArrayAdapter<String> universityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, universities);
        universityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        universitySpinner.setAdapter(universityAdapter);
    }

    private void loadTeamList() {
        String selectedSport = sportSpinner.getSelectedItem().toString();
        String selectedUniversity = universitySpinner.getSelectedItem().toString();

        if (selectedSport.isEmpty() || selectedUniversity.isEmpty()) {
            Toast.makeText(Teams.this, "Please select both sport and university.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference sportRef = databaseReference.child(selectedSport).child(selectedUniversity);

        sportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamList.clear(); // Clear existing list

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String regNumber = studentSnapshot.getKey();
                    String studentName = studentSnapshot.child("Name").getValue(String.class);
                    Boolean verificationStatus = studentSnapshot.child("verified").getValue(Boolean.class);

                    // Format the data to be displayed with YES/NO for verification status
                    String verificationText = (verificationStatus != null && verificationStatus) ? "YES" : "NO";
                    String displayText = String.format("Reg No: %s, Name: %s, Verified: %s", regNumber, studentName, verificationText);

                    teamList.add(displayText);
                }

                teamListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Teams.this, "Failed to load team list.", Toast.LENGTH_SHORT).show();
            }
        });
    }



}
