package eu.tutorials.myappkusa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Verify_Student extends AppCompatActivity {

    private AutoCompleteTextView institutionInput;
    private EditText studentRegNumber, studentSportInput;
    private String selectedSport;
    private Button checkStudentButton;
    private Button verifyButton;
    private Button declineButton;
    private Button loadPhotoButton;
    private TextView studentDetailsTextView;
    private ImageView studentIdPhoto, passportPhoto;

    private DatabaseReference firebaseDatabaseRef;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_student);

        // Initialize views
        institutionInput = findViewById(R.id.institutionInput);
        studentRegNumber = findViewById(R.id.regNumberInput);
        studentSportInput = findViewById(R.id.SportInput);
        checkStudentButton = findViewById(R.id.CheckStudentBtn);
        verifyButton = findViewById(R.id.verifyButton);
        declineButton = findViewById(R.id.declineButton);
        loadPhotoButton = findViewById(R.id.loadPhotoButton);
        studentDetailsTextView = findViewById(R.id.studentDetailsTextView);
        studentIdPhoto = findViewById(R.id.studentIdPhoto);
        passportPhoto = findViewById(R.id.passportPhoto);

        // Initialize Firebase references
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference("Teams");
        firebaseStorage = FirebaseStorage.getInstance();

        // Set click listener for Check Student button
        checkStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle check student action here
                String institutionName = institutionInput.getText().toString().trim();
                String studentRegNo = studentRegNumber.getText().toString().trim();
                selectedSport = studentSportInput.getText().toString().trim();

                if (!institutionName.isEmpty() && !studentRegNo.isEmpty()) {
                    // First, check student details from Firebase
                    checkStudentInExcel(institutionName, studentRegNo);
                } else {
                    Toast.makeText(Verify_Student.this, "Please enter institution name and student ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Load Photo button
        loadPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load student ID photo logic
                loadStudentIdPhoto();
            }
        });

        // Set click listener for Verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String institutionName = institutionInput.getText().toString().trim();
                String studentRegNo = studentRegNumber.getText().toString().trim();

                if (!studentRegNo.isEmpty()) {
                    updateVerificationStatus(institutionName, studentRegNo, true);
                } else {
                    Toast.makeText(Verify_Student.this, "Please enter a student ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for Decline button
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String institutionName = institutionInput.getText().toString().trim();
                String studentRegNo = studentRegNumber.getText().toString().trim();

                if (!studentRegNo.isEmpty()) {
                    updateVerificationStatus(institutionName, studentRegNo, false);
                } else {
                    Toast.makeText(Verify_Student.this, "Please enter a student ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkStudentInExcel(String institutionName, final String studentRegNumber) {
        // Adjust the path to point to the specific university's Excel sheet
        String excelPath = "University Database/" + institutionName + "/Students.xlsx";
        StorageReference excelRef = firebaseStorage.getReference().child(excelPath);

        excelRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    Workbook workbook = new XSSFWorkbook(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    Iterator<Row> rowIterator = sheet.iterator();
                    boolean studentFound = false;
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();
                        if (row.getCell(0).getStringCellValue().equalsIgnoreCase(studentRegNumber)) {
                            studentFound = true;
                            String studentRegNumber = row.getCell(0).getStringCellValue();
                            String studentName = row.getCell(1).getStringCellValue();
                            String studentCourse = row.getCell(2).getStringCellValue();
                            String studentYearOfStudy = String.valueOf(row.getCell(3).getNumericCellValue());

                            String studentDateOfBirth;
                            if (row.getCell(4).getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(row.getCell(4))) {
                                Date date = row.getCell(4).getDateCellValue();
                                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
                                studentDateOfBirth = dateFormatter.format(date);
                            } else {
                                studentDateOfBirth = row.getCell(4).getStringCellValue();
                            }

                            String detailsText = "RegNumber: " + studentRegNumber + "\n" + "Name: " + studentName + "\n" + "Course: " + studentCourse + "\n" +
                                    "Year of Study: " + studentYearOfStudy + "\n" +
                                    "Date of Birth: " + studentDateOfBirth;
                            studentDetailsTextView.setText(detailsText);
                            studentDetailsTextView.setVisibility(View.VISIBLE); // Make the TextView visible

                            loadPassportPhoto(studentRegNumber, institutionName);

                            break;
                        }
                    }

                    if (!studentFound) {
                        studentDetailsTextView.setText("Student ID: " + studentRegNumber + " is not found in " + institutionName + " database.");
                        studentDetailsTextView.setTextColor(getResources().getColor(R.color.red));
                        studentDetailsTextView.setVisibility(View.VISIBLE); // Make the TextView visible
                    }

                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Verify_Student.this, "Error reading Excel file.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Verify_Student.this, "Failed to load Excel sheet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPassportPhoto(String studentRegNumber, String institutionName) {
        // Load passport photo
        String passportPhotoPath = "University Database/" + institutionName + "/Passport photos/" + studentRegNumber + ".jpeg";
        StorageReference passportPhotoRef = firebaseStorage.getReference().child(passportPhotoPath);

        passportPhotoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                passportPhoto.setImageBitmap(bitmap);
                passportPhoto.setVisibility(View.VISIBLE); // Make the passport photo ImageView visible
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Verify_Student.this, "Failed to load student passport photo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load student ID photo
    private void loadStudentIdPhoto() {
        String institutionName = institutionInput.getText().toString().trim();
        String studentId = studentRegNumber.getText().toString().trim();

        if (!institutionName.isEmpty() && !studentId.isEmpty()) {
            // Adjust the path to point to the specific student's photo
            String photoPath = "University Database/" + institutionName + "/ID cards/" + studentId;
            StorageReference photoRef = firebaseStorage.getReference().child(photoPath);

            photoRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    studentIdPhoto.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(Verify_Student.this, "Failed to load student photo.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Verify_Student.this, "Please enter institution name and student ID.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the verification status
    private void updateVerificationStatus(String institutionName, String studentRegNo, boolean isVerified) {
        if (selectedSport != null && !selectedSport.isEmpty()) {
            // Extract the student's name from the details TextView
            String studentDetails = studentDetailsTextView.getText().toString();
            String studentName = extractStudentNameFromDetails(studentDetails);

            if (studentName != null && !studentName.isEmpty()) {
                DatabaseReference studentRef = firebaseDatabaseRef.child(selectedSport).child(institutionName).child(studentRegNo);

                // Update the verified status and name in the database
                studentRef.child("verified").setValue(isVerified);
                studentRef.child("Name").setValue(studentName).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String message = isVerified ? "Student ID: " + studentRegNo + " has been verified."
                                : "Student ID: " + studentRegNo + " has been declined.";
                        int color = isVerified ? getResources().getColor(R.color.green)
                                : getResources().getColor(R.color.red);
                        studentDetailsTextView.setText(message);
                        studentDetailsTextView.setTextColor(color);
                        studentDetailsTextView.setVisibility(View.VISIBLE); // Ensure the TextView is visible
                    } else {
                        Toast.makeText(Verify_Student.this, "Failed to update verification status.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(Verify_Student.this, "Student name not found. Please check the student details.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Verify_Student.this, "Sport not specified. Please check the student details first.", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to extract the student's name from the details TextView
    private String extractStudentNameFromDetails(String details) {
        String[] lines = details.split("\n");
        for (String line : lines) {
            if (line.startsWith("Name: ")) {
                return line.substring(6); // Remove the "Name: " prefix
            }
        }
        return null; // Return null if the name is not found
    }

}
