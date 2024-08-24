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

public class Verification extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button submitBtn, checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        drawerLayout = findViewById(R.id.drawer_layout2);
        submitBtn = findViewById(R.id.btn_submit_ID);
        checkBtn = findViewById(R.id.btn_check_status);
        navigationView = findViewById(R.id.nav_view2);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login page
                Intent intent = new Intent(Verification.this, submit_id.class);
                startActivity(intent);
                finish(); // Finish the SignUp activity to go back to the login page
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login page
                Intent intent = new Intent(Verification.this, Verification_Status.class);
                startActivity(intent);
                finish(); // Finish the SignUp activity to go back to the login page
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
