package eu.tutorials.myappkusa;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Student_draws extends AppCompatActivity {

    private Spinner sportSpinner;
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<String> scheduleList = new ArrayList<>();
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_draws);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        // Initialize RecyclerView
        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        scheduleRecyclerView.setAdapter(scheduleAdapter);

        // Find views and set click listeners
        sportSpinner = findViewById(R.id.sportSpinner);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("draws");

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSport = parent.getItemAtPosition(position).toString();
                loadSchedulesFromFirebase(selectedSport);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadSchedulesFromFirebase(String sport) {
        databaseReference.child(sport).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                scheduleList.clear();
                for (DataSnapshot drawTypeSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot scheduleSnapshot : drawTypeSnapshot.getChildren()) {
                        scheduleList.add(scheduleSnapshot.getValue(String.class));
                    }
                }
                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Student_draws.this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
