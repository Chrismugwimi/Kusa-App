package eu.tutorials.myappkusa;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Volleyball_results extends AppCompatActivity {

    private TextView eventNameTextView;
    private TextView eventDateTextView;
    private TextView eventVenueTextView;
    private TextView sportTextView;
    private TableLayout resultsTable;
    private Spinner eventNameSpinner;
    Toolbar toolbar;

    private DatabaseReference firebaseDatabaseRef;
    private List<String> eventNames;
    private List<DataSnapshot> eventSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volleyball_results);

        eventNameTextView = findViewById(R.id.eventName);
        eventDateTextView = findViewById(R.id.eventDate);
        eventVenueTextView = findViewById(R.id.eventVenue);
        sportTextView = findViewById(R.id.sport);
        resultsTable = findViewById(R.id.resultsTable);
        eventNameSpinner = findViewById(R.id.eventNameSpinner);
        toolbar = findViewById(R.id.toolbar);

        // Initialize Firebase reference for basketball results
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("EventResults").child("Volleyball");

        eventNames = new ArrayList<>();
        eventSnapshots = new ArrayList<>();

        fetchEventNames();

        eventNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < eventSnapshots.size()) {
                    displayEventResults(eventSnapshots.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void fetchEventNames() {
        firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventNames.clear();
                eventSnapshots.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    String eventName = eventSnapshot.child("eventName").getValue(String.class);
                    if (eventName != null) {
                        eventNames.add(eventName);
                        eventSnapshots.add(eventSnapshot);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Volleyball_results.this,
                        R.layout.spinner_item, eventNames);
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                eventNameSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Volleyball_results.this, "Failed to load event names.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEventResults(DataSnapshot eventSnapshot) {
        String eventName = eventSnapshot.child("eventName").getValue(String.class);
        String eventDate = eventSnapshot.child("eventDate").getValue(String.class);
        String eventVenue = eventSnapshot.child("eventVenue").getValue(String.class);

        eventNameTextView.setText("Event Name: " + eventName);
        eventDateTextView.setText("Date: " + eventDate);
        eventVenueTextView.setText("Venue: " + eventVenue);
        sportTextView.setText("Sport: " + "Volleyball");
        eventDateTextView.setTextColor(Color.WHITE);
        eventNameTextView.setTextColor(Color.WHITE);
        eventVenueTextView.setTextColor(Color.WHITE);
        sportTextView.setTextColor(Color.WHITE);

        resultsTable.removeAllViews();
        addTableHeader();
        DataSnapshot resultsSnapshot = eventSnapshot.child("results");
        for (DataSnapshot resultSnapshot : resultsSnapshot.getChildren()) {
            String position = resultSnapshot.getKey();
            String universityName = resultSnapshot.child("universityName").getValue(String.class);
            String points = resultSnapshot.child("points").getValue(String.class);

            addResultToTable(position, universityName, points);
        }
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);

        TextView positionHeader = new TextView(this);
        positionHeader.setText("Position");
        positionHeader.setPadding(8, 8, 8, 8);
        positionHeader.setTextColor(Color.GREEN);
        positionHeader.setTypeface(null, Typeface.BOLD);
        headerRow.addView(positionHeader);

        TextView universityHeader = new TextView(this);
        universityHeader.setText("University");
        universityHeader.setPadding(8, 8, 8, 8);
        universityHeader.setTextColor(Color.GREEN);
        universityHeader.setTypeface(null, Typeface.BOLD);
        headerRow.addView(universityHeader);

        TextView pointsHeader = new TextView(this);
        pointsHeader.setText("Points");
        pointsHeader.setPadding(8, 8, 8, 8);
        pointsHeader.setTextColor(Color.GREEN);
        pointsHeader.setTypeface(null, Typeface.BOLD);
        headerRow.addView(pointsHeader);

        resultsTable.addView(headerRow);
    }

    private void addResultToTable(String position, String universityName, String points) {
        TableRow row = new TableRow(this);

        TextView positionTextView = new TextView(this);
        positionTextView.setText(position);
        positionTextView.setPadding(8, 8, 8, 8);
        positionTextView.setTextColor(Color.WHITE);
        row.addView(positionTextView);

        TextView universityNameTextView = new TextView(this);
        universityNameTextView.setText(universityName);
        universityNameTextView.setPadding(8, 8, 8, 8);
        universityNameTextView.setTextColor(Color.WHITE);
        row.addView(universityNameTextView);

        TextView pointsTextView = new TextView(this);
        pointsTextView.setText(points);
        pointsTextView.setPadding(8, 8, 8, 8);
        pointsTextView.setTextColor(Color.WHITE);
        row.addView(pointsTextView);

        resultsTable.addView(row);
    }
}
