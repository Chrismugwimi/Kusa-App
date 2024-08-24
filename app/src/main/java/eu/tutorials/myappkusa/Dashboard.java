package eu.tutorials.myappkusa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize FirebaseAuth and UI components
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.textView_email);
        user = auth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Set up the toolbar
        setSupportActionBar(toolbar);

        // Set up the drawer layout and navigation view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);
        }

        // Check if the user is logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        // Set logout button click listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        // Set click listeners for the image buttons
        ImageButton imageButton = findViewById(R.id.imageView3);
        ImageButton imageBtn = findViewById(R.id.imageView6);
        ImageButton imageBtnResults = findViewById(R.id.imageView4);
        ImageButton imageBtnDraws = findViewById(R.id.imageView5);
        ImageButton imageBtnTeams = findViewById(R.id.imageView7);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, upcoming_events.class);
                startActivity(intent);
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Verification.class);
                startActivity(intent);
            }
        });

        imageBtnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Results.class);
                startActivity(intent);
            }
        });

        imageBtnDraws.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Student_draws.class);
                startActivity(intent);
            }
        });

        imageBtnTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Teams.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finishAffinity();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        // Use if-else statements instead of a switch-case
        if (id == R.id.nav_events) {
            Log.d("NAVIGATION", "Events selected");
            intent = new Intent(Dashboard.this, upcoming_events.class);
        } else if (id == R.id.nav_results) {
            Log.d("NAVIGATION", "Results selected");
            intent = new Intent(Dashboard.this, Results.class);
        } else if (id == R.id.nav_home) {
            Log.d("NAVIGATION", "Home selected");
            intent = new Intent(Dashboard.this, Draws.class);
        } else if (id == R.id.nav_verification) {
            Log.d("NAVIGATION", "Verification selected");
            intent = new Intent(Dashboard.this, submit_id.class);
        }

        // Start the new activity if an intent was set
        if (intent != null) {
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
