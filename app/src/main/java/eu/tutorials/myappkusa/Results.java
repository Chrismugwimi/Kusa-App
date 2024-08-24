package eu.tutorials.myappkusa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Results extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button footballBtn, basketballBtn, handballBtn, hockeyBtn, netballBtn, athleticsBtn, volleyballBtn, tennisBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        drawerLayout = findViewById(R.id.drawer_layout2);
        navigationView = findViewById(R.id.nav_view2);
        toolbar = findViewById(R.id.toolbar);
        footballBtn = findViewById(R.id.btn_football);
        basketballBtn = findViewById(R.id.btn_basketball);
        handballBtn = findViewById(R.id.btn_handball);
        hockeyBtn = findViewById(R.id.btn_hockey);
        netballBtn = findViewById(R.id.btn_netball);
        athleticsBtn = findViewById(R.id.btn_athletics);
        volleyballBtn = findViewById(R.id.btn_volleyball);
        tennisBtn = findViewById(R.id.btn_tennis);

        setSupportActionBar(toolbar);

        footballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Football_results.class);
                startActivity(intent);
            }
        });

        basketballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Basketball_results.class);
                startActivity(intent);
            }
        });

        handballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Handball_results.class);
                startActivity(intent);
            }
        });

        hockeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Hockey_results.class);
                startActivity(intent);
            }
        });

        netballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Netball_results.class);
                startActivity(intent);
            }
        });

        athleticsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Athletics_results.class);
                startActivity(intent);
            }
        });

        volleyballBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Volleyball_results.class);
                startActivity(intent);
            }
        });

        tennisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Results.this, Tennis_results.class);
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(this);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return true;
    }
}
