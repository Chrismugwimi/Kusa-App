package eu.tutorials.myappkusa;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Officer_SignUp extends AppCompatActivity {

    // Variables
    TextInputLayout regName, regInstitution, regRole, regPhoneNumber, regEmail, regPassword;
    Button regBtn, regToLoginBtn, regStudentBtn;

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_sign_up);

        // Hooks
        regName = findViewById(R.id.reg_name);
        regInstitution = findViewById(R.id.reg_institution);
        regRole = findViewById(R.id.reg_role);
        regPhoneNumber = findViewById(R.id.reg_phone_number);
        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        regStudentBtn = findViewById(R.id.reg_student_btn);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
            finish();
        }

        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the login page
                Intent intent = new Intent(Officer_SignUp.this, Login.class);
                startActivity(intent);
                finish(); // Finish the SignUp activity to go back to the login page
            }
        });

        regStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the student sign-up page
                Intent intent = new Intent(Officer_SignUp.this, SignUp.class);
                startActivity(intent);
                finish(); // Finish the SignUp activity to go back to the login page
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = regEmail.getEditText().getText().toString().trim();
                String password = regPassword.getEditText().getText().toString().trim();
                String name = regName.getEditText().getText().toString();
                String institution = regInstitution.getEditText().getText().toString();
                String role = regRole.getEditText().getText().toString();
                String phoneNumber = regPhoneNumber.getEditText().getText().toString();

                if (TextUtils.isEmpty(email)) {
                    regEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    regPassword.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    regPassword.setError("Password must be at least 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register the user in Firebase
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Officer_SignUp.this, "Account created", Toast.LENGTH_SHORT).show();
                            userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("officers").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Name", name);
                            user.put("Email", email);
                            user.put("Institution", institution);
                            user.put("Role", role);
                            user.put("PhoneNumber", phoneNumber);
                            user.put("userType", "officer"); // Add userType field

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), Officer_Dashboard.class));

                        } else {
                            Toast.makeText(Officer_SignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
