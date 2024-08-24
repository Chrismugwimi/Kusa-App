package eu.tutorials.myappkusa;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Event_Results extends AppCompatActivity {

    private EditText eventNameInput;
    private EditText eventDateInput;
    private Spinner sportSpinner;
    private EditText eventVenueInput;
    private LinearLayout resultsContainer;

    private DatabaseReference firebaseDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_results);

        eventNameInput = findViewById(R.id.eventNameInput);
        eventDateInput = findViewById(R.id.eventDateInput);
        sportSpinner = findViewById(R.id.sportSpinner);
        eventVenueInput = findViewById(R.id.eventVenueInput);
        resultsContainer = findViewById(R.id.resultsContainer);

        Button addResultButton = findViewById(R.id.addResultButton);
        Button submitResultsButton = findViewById(R.id.submitResultsButton);

        // Initialize Firebase reference
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("EventResults");

        // Set up the Spinner with the sports array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sports_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner.setAdapter(adapter);

        addResultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addResultEntry();
            }
        });

        submitResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle results submission here
                submitResults();
            }
        });
    }

    private void addResultEntry() {
        LinearLayout resultEntry = new LinearLayout(this);
        resultEntry.setOrientation(LinearLayout.HORIZONTAL);
        resultEntry.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        resultEntry.setPadding(0, 0, 0, 8);

        EditText positionInput = new EditText(this);
        positionInput.setHint("Position");
        positionInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        resultEntry.addView(positionInput);
        positionInput.setTextColor(Color.GREEN);
        positionInput.setHintTextColor(Color.WHITE);

        EditText universityNameInput = new EditText(this);
        universityNameInput.setHint("University Name");
        universityNameInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        resultEntry.addView(universityNameInput);
        universityNameInput.setTextColor(Color.GREEN);
        universityNameInput.setHintTextColor(Color.WHITE);

        EditText pointsInput = new EditText(this);
        pointsInput.setHint("Points");
        pointsInput.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        resultEntry.addView(pointsInput);
        pointsInput.setTextColor(Color.GREEN);
        pointsInput.setHintTextColor(Color.WHITE);

        resultsContainer.addView(resultEntry);
    }

    private void submitResults() {
        String eventName = eventNameInput.getText().toString().trim();
        String eventDate = eventDateInput.getText().toString().trim();
        String eventVenue = eventVenueInput.getText().toString().trim();
        String selectedSport = sportSpinner.getSelectedItem().toString();

        if (eventName.isEmpty() || eventDate.isEmpty() || eventVenue.isEmpty()) {
            Toast.makeText(Event_Results.this, "Please enter all event details.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sanitize the event name to use as a Firebase key
        String sanitizedEventName = eventName.replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");

        // Create a map to hold the event details and results
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("eventName", eventName);
        eventDetails.put("eventDate", eventDate);
        eventDetails.put("eventVenue", eventVenue);

        // Create a map to hold the results
        Map<String, Object> results = new HashMap<>();
        int childCount = resultsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = resultsContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout resultEntry = (LinearLayout) child;
                EditText positionInput = (EditText) resultEntry.getChildAt(0);
                EditText universityNameInput = (EditText) resultEntry.getChildAt(1);
                EditText pointsInput = (EditText) resultEntry.getChildAt(2);

                String position = positionInput.getText().toString().trim();
                String universityName = universityNameInput.getText().toString().trim();
                String points = pointsInput.getText().toString().trim();

                if (position.isEmpty() || universityName.isEmpty() || points.isEmpty()) {
                    Toast.makeText(Event_Results.this, "Please enter all result details.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a map for the individual result
                Map<String, Object> result = new HashMap<>();
                result.put("universityName", universityName);
                result.put("points", points);

                results.put(position, result);
            }
        }

        // Combine event details and results into one map
        eventDetails.put("results", results);

        // Save to Firebase using the sanitized event name as the key
        firebaseDatabaseRef.child(selectedSport).child(sanitizedEventName).setValue(eventDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Event_Results.this, "Event results submitted successfully.", Toast.LENGTH_SHORT).show();
                clearInputs();
            } else {
                Toast.makeText(Event_Results.this, "Failed to submit event results.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearInputs() {
        eventNameInput.setText("");
        eventDateInput.setText("");
        eventVenueInput.setText("");
        resultsContainer.removeAllViews();
        sportSpinner.setSelection(0);
    }
}
