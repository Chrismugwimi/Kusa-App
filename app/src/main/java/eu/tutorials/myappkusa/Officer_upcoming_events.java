package eu.tutorials.myappkusa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Officer_upcoming_events extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDate;
    private EditText editTextDisciplines;
    private EditText editTextVenue;
    private Button buttonSubmit;

    private DatabaseReference databaseEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_upcoming_events);

        editTextName = findViewById(R.id.editTextEventName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextDisciplines = findViewById(R.id.editTextDisciplines);
        editTextVenue = findViewById(R.id.editTextVenue);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Initialize Firebase reference
        databaseEvents = FirebaseDatabase.getInstance().getReference("UpcomingEvents");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });
    }

    private void addEvent() {
        String name = editTextName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String disciplines = editTextDisciplines.getText().toString().trim();
        String venue = editTextVenue.getText().toString().trim();

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(disciplines) && !TextUtils.isEmpty(venue)) {
            // Check if an event with the same name already exists
            DatabaseReference eventRef = databaseEvents.child(name);
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Toast.makeText(this, "An event with this name already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create and save the event
                        Event event = new Event(name, date, disciplines, venue);
                        eventRef.setValue(event);
                        Toast.makeText(this, "Event added", Toast.LENGTH_SHORT).show();
                        editTextName.setText("");
                        editTextDate.setText("");
                        editTextDisciplines.setText("");
                        editTextVenue.setText("");
                    }
                } else {
                    Toast.makeText(this, "Failed to check if event exists", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

}
