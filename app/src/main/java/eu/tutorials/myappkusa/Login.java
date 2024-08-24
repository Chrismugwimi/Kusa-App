package eu.tutorials.myappkusa;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            checkUserTypeAndRedirect(mAuth.getCurrentUser().getUid());
            return;
        }

        Button callSignUp = findViewById(R.id.signup_screen);
        ImageView image = findViewById(R.id.logo_image);
        TextView logoText = findViewById(R.id.logo_name);
        TextView sloganText = findViewById(R.id.slogan_name);
        TextInputLayout regEmail = findViewById(R.id.regEmail);
        TextInputLayout regPassword = findViewById(R.id.regPassword);
        Button login_btn = findViewById(R.id.btn_login);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getEditText().getText().toString().trim();
                String password = regPassword.getEditText().getText().toString().trim();

                if (validateInput(regEmail, regPassword, email, password)) {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            checkUserTypeAndRedirect(user.getUid());
                                        }
                                    } else {
                                        Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<>(image, "logo_image");
                pairs[1] = new Pair<>(logoText, "logo_text");
                pairs[2] = new Pair<>(sloganText, "logo_desc");
                pairs[3] = new Pair<>(regEmail, "username_tran");
                pairs[4] = new Pair<>(regPassword, "password_tran");
                pairs[5] = new Pair<>(login_btn, "button_tran");
                pairs[6] = new Pair<>(callSignUp, "signup_screen");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void checkUserTypeAndRedirect(String uid) {
        db.collection("officers").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userType = task.getResult().getString("userType");
                    if ("officer".equals(userType)) {
                        startActivity(new Intent(Login.this, Officer_Dashboard.class));
                    } else {
                        startActivity(new Intent(Login.this, Dashboard.class));
                    }
                    finish();
                } else {
                    Toast.makeText(Login.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private boolean validateInput(TextInputLayout regEmail, TextInputLayout regPassword, String email, String password) {
        if (TextUtils.isEmpty(email)) {
            regEmail.setError("Email is required");
            return false;
        } else {
            regEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            regPassword.setError("Password is required");
            return false;
        } else {
            regPassword.setError(null);
        }

        if (password.length() < 6) {
            regPassword.setError("Password must be at least 6 characters");
            return false;
        } else {
            regPassword.setError(null);
        }

        return true;
    }
}
