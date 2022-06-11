package TenaSensor.Android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * simple activity for creating a new t'ena account
 */
public class CreateAccountActivity extends AppCompatActivity {
    /**
     * minimum password length
     */
    private final int MIN_PW_LENGTH = 8;
    /**
     * for registering account
     */
    private Button registerButton;
    /**
     * hook for email edit text
     */
    private EditText emailEditText;
    /**
     * hook for phone edit text
     */
    private EditText phoneEditText;
    /**
     * hook for password edit text
     */
    private EditText pwEditText;
    /**
     * hook for verify pw edit text
     */
    private EditText verifyPwEditText;
    /**
     * hook for password warning text
     */
    private TextView pwWarningText;
    /**
     * hook for verify password warning text
     */
    private TextView verifyPwWarningText;
    /**
     * hook for email warning text
     */
    private TextView emailWarningText;
    /**
     * connection to firebase authentication
     */
    private FirebaseAuth firebaseAuth;
    /**
     * handle to firebase
     */
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance(); // initialize an instance of firebase auth
        mFirestore = FirebaseFirestore.getInstance();   // initialize connection to firebase
        setViews();     // grab hooks to all views
        initViews();    // initialize text views (mostly invisible)
        addEditTextListeners(); // add appropriate listeners for each edit text field
        setRegisterButtonListener();    // sets a listener for the register button
    }

    /**
     * set hooks to views
     */
    private void setViews() {
        /*registerButton = findViewById(R.id.register_button);
        emailEditText = findViewById(R.id.email_address_edittext);
        phoneEditText = findViewById(R.id.phone_edittext);
        pwEditText = findViewById(R.id.pw_edittext);
        verifyPwEditText = findViewById(R.id.verify_pw_edittext);
        pwWarningText = findViewById(R.id.password_warning_text);
        verifyPwWarningText = findViewById(R.id.verify_pw_warning_text);
        emailWarningText = findViewById(R.id.email_warning_text);*/
    }

    /**
     * initialize text views (mostly invisible)
     */
    private void initViews() {
        pwWarningText.setVisibility(View.INVISIBLE);
        verifyPwWarningText.setVisibility(View.INVISIBLE);
        emailWarningText.setVisibility(View.INVISIBLE);
    }

    /**
     * add appropriate listeners for each edit text field
     */
    private void addEditTextListeners() {
        // for password (must be >= 8 chars)
        pwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() < MIN_PW_LENGTH) {
                    pwWarningText.setVisibility(View.VISIBLE);
                } else {
                    pwWarningText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // for verify password (must match email)
        verifyPwEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().equals(pwEditText.getText().toString())) {
                    verifyPwWarningText.setVisibility(View.VISIBLE);
                } else {
                    verifyPwWarningText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // for email address (must be proper email string)
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*if(!Utils.isValidEmail(target.getText().toString())) {
                    emailWarningText.setVisibility(View.VISIBLE);
                } else {
                    emailWarningText.setVisibility(View.INVISIBLE);
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * adds an on click listener to register button that validates fields before passing them to
     * next module
     */
    private void setRegisterButtonListener() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String pw = pwEditText.getText().toString();
                String verifyPw = verifyPwEditText.getText().toString();
                final String email = emailEditText.getText().toString();
                final String phone = phoneEditText.getText().toString();

                if(/*Utils.isValidEmail(email) && */pw.length() >= MIN_PW_LENGTH && verifyPw.equals(pw) && !phone.equals("")) {
                    // send information to new module for creation
                    firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()) {
                                // send email verification
                                /*FirebaseHelper.firebaseUser = firebaseAuth.getCurrentUser();
                                FirebaseHelper.firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // toast patientUser that email has been sent
                                        Toast.makeText(CreateAccountActivity.this, "Verification email has been sent", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("MY_AUTH", "onFailure: email not sent " + e.getMessage());
                                        // toast patientUser that email has been sent
                                        Toast.makeText(CreateAccountActivity.this, "Failed to send email verification, is that a valid email address?", Toast.LENGTH_SHORT).show();
                                    }
                                });*/

                                // create a new physicianUser document and push to our firebase
                                createNewUser(email, phone, firebaseAuth.getCurrentUser().getUid());

                                // set firebase patientUser in FirebaseHelper
                                //FirebaseHelper.initFirebaseUser();

                                // go to waiting screen
                                startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to register. Do you already have an account with this email?", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please ensure all fields are filled correctly", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    /**
     * creates a new physicianUser and pushes to firebase
     * @param email
     * @param phone
     * @param id
     */
    private void createNewUser(final String email, final String phone, final String id) {
        //FirebaseHelper.patientUser = new PatientUser(id, email, phone);
        DocumentReference documentReference = mFirestore.collection("patients").document(id);
        /*documentReference.set(FirebaseHelper.patientUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("MY_AUTH", "onSuccess: physicianUser profile has been created for " + id);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MY_AUTH", "onFailure: " + e.toString());
            }
        });*/
    }


}