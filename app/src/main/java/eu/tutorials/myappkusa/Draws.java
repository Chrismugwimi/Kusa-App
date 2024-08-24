package eu.tutorials.myappkusa;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Draws extends AppCompatActivity {

    private List<String> teamsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TeamAdapter teamAdapter;
    private TextView scheduleTextView;
    private Spinner sportSpinner;
    private EditText poolSizeEditText, poolNumberEditText;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draws);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the status bar color for Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.teamRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        teamAdapter = new TeamAdapter(teamsList);
        recyclerView.setAdapter(teamAdapter);

        // Find views and set click listeners
        EditText teamNameEditText = findViewById(R.id.teamNameEditText);
        Button addTeamButton = findViewById(R.id.addTeamButton);
        Button generateScheduleButton = findViewById(R.id.generateScheduleButton);
        Button clearButton = findViewById(R.id.clearButton);
        poolSizeEditText = findViewById(R.id.poolSizeEditText);
        poolNumberEditText = findViewById(R.id.poolNumberEditText);
        Button generatePoolingDrawButton = findViewById(R.id.generatePoolButton);
        sportSpinner = findViewById(R.id.sportSpinner);

        scheduleTextView = findViewById(R.id.scheduleTextView);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        addTeamButton.setOnClickListener(v -> {
            String teamName = teamNameEditText.getText().toString().trim();
            if (!teamName.isEmpty()) {
                addTeam(teamName);
                teamNameEditText.setText(""); // Clear input field
            } else {
                Toast.makeText(Draws.this, "Please enter a team name", Toast.LENGTH_SHORT).show();
            }
        });

        generateScheduleButton.setOnClickListener(v -> generateRoundRobinScheduleAndDisplay());

        generatePoolingDrawButton.setOnClickListener(v -> {
            String poolSizeStr = poolSizeEditText.getText().toString().trim();
            String poolNumberStr = poolNumberEditText.getText().toString().trim();
            if (!poolSizeStr.isEmpty() && !poolNumberStr.isEmpty()) {
                int poolSize = Integer.parseInt(poolSizeStr);
                int numPools = Integer.parseInt(poolNumberStr);
                generatePoolingDrawAndDisplay(poolSize, numPools);
            } else {
                Toast.makeText(Draws.this, "Please enter pool size and number of pools", Toast.LENGTH_SHORT).show();
            }
        });

        clearButton.setOnClickListener(v -> {
            clearTeamsAndSchedule();
        });
    }

    private void addTeam(String teamName) {
        teamsList.add(teamName);
        teamAdapter.notifyDataSetChanged(); // Notify RecyclerView adapter
    }

    private void generateRoundRobinScheduleAndDisplay() {
        if (teamsList.size() < 2) {
            Toast.makeText(this, "Add at least two teams to generate schedule", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> schedule = generateRoundRobinSchedule(teamsList);

        // Display the schedule in a TextView or log it
        scheduleTextView.setText(Html.fromHtml(buildScheduleString(schedule)));

        // Save schedule to Firebase
        saveScheduleToFirebase("RoundRobin", schedule);
    }

    private String buildScheduleString(List<String> schedule) {
        StringBuilder scheduleStringBuilder = new StringBuilder();
        int matchNumber = 1;
        for (String item : schedule) {
            scheduleStringBuilder.append(matchNumber++).append(". ").append(item).append("<br>");
        }
        return scheduleStringBuilder.toString();
    }

    private List<String> generateRoundRobinSchedule(List<String> teams) {
        int numTeams = teams.size();
        List<String> schedule = new ArrayList<>();

        // Ensure even number of teams for round-robin
        if (numTeams % 2 != 0) {
            teams.add("Bye"); // Add a dummy team for odd number of teams
            numTeams++;
        }

        int halfSize = numTeams / 2;
        List<String> teamsCopy = new ArrayList<>(teams);

        for (int round = 0; round < numTeams - 1; round++) {
            for (int idx = 0; idx < halfSize; idx++) {
                String match = teamsCopy.get(idx) + " vs " + teamsCopy.get(numTeams - 1 - idx);
                schedule.add(match);
            }
            Collections.rotate(teamsCopy.subList(1, teamsCopy.size()), 1);
        }

        return schedule;
    }

    private void generatePoolingDrawAndDisplay(int poolSize, int numPools) {
        if (teamsList.size() < poolSize * numPools) {
            Toast.makeText(this, "Add more teams or adjust pool size/number of pools", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> poolingDraw = generatePoolingDraw(teamsList, poolSize, numPools);

        // Display the schedule in a TextView
        scheduleTextView.setText(Html.fromHtml(buildPoolingDrawString(poolingDraw)));

        // Save pooling draw to Firebase
        saveScheduleToFirebase("PoolingDraw", poolingDraw);
    }

    private String buildPoolingDrawString(List<String> poolingDraw) {
        StringBuilder poolingDrawStringBuilder = new StringBuilder();
        for (String line : poolingDraw) {
            if (line.startsWith("Pool ")) {
                poolingDrawStringBuilder.append("<b>").append(line).append("</b>").append("<br>");
            } else {
                poolingDrawStringBuilder.append(line).append("<br>");
            }
        }
        return poolingDrawStringBuilder.toString();
    }

    private List<String> generatePoolingDraw(List<String> teams, int poolSize, int numPools) {
        List<String> schedule = new ArrayList<>();
        int numTeams = teams.size();

        List<List<String>> pools = new ArrayList<>();
        for (int i = 0; i < numPools; i++) {
            pools.add(new ArrayList<>());
        }

        int poolIndex = 0;
        for (String team : teams) {
            pools.get(poolIndex).add(team);
            poolIndex = (poolIndex + 1) % numPools;
        }

        // Build schedule string
        char poolName = 'A';
        for (List<String> pool : pools) {
            schedule.add("Pool " + poolName++);
            int teamNumber = 1;
            for (String team : pool) {
                schedule.add(teamNumber++ + ". " + team);
            }
        }

        return schedule;
    }

    private void clearTeamsAndSchedule() {
        teamsList.clear();
        teamAdapter.notifyDataSetChanged();
        scheduleTextView.setText("");
    }

    private void saveScheduleToFirebase(String type, List<String> schedule) {
        String selectedSport = sportSpinner.getSelectedItem().toString();
        databaseReference.child("draws").child(selectedSport).child(type).setValue(schedule)
                .addOnSuccessListener(aVoid -> Toast.makeText(Draws.this, "Schedule saved to Firebase", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Draws.this, "Failed to save schedule", Toast.LENGTH_SHORT).show());
    }
}
