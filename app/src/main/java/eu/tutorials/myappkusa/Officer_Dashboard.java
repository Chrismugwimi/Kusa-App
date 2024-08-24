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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Officer_Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.activity_officer_dashboard);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.textView_email);
        user = auth.getCurrentUser();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
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

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        ImageButton imageButton = findViewById(R.id.imageView3);
        ImageButton imgButton = findViewById(R.id.imageView5);
        ImageButton imageBtn = findViewById(R.id.imageView6);
        ImageButton imageBtnResults = findViewById(R.id.imageView4);
        ImageButton imageBtnTeams = findViewById(R.id.imageView7);
        ImageButton imageBtnProfile = findViewById(R.id.imageView8);

        // Set OnClickListener on the ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Officer_upcoming_events.class);
                startActivity(intent);
            }

        });

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Draws.class);
                startActivity(intent);
            }

        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Verify_Student.class);
                startActivity(intent);
            }

        });

        imageBtnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Event_Results.class);
                startActivity(intent);
            }

        });

        imageBtnTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Teams.class);
                startActivity(intent);
            }

        });

        imageBtnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Officer_Dashboard.this, Profile.class);
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
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}