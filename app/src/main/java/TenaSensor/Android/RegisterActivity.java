package TenaSensor.Android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    Button btn2_signup;
    EditText user_name, pass_word;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linkedin_login);
        user_name=findViewById(R.id.username);
        pass_word=findViewById(R.id.password1);
        btn2_signup=findViewById(R.id.sign);
        mAuth=FirebaseAuth.getInstance();
        btn2_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = user_name.getText().toString().trim();
                String password= pass_word.getText().toString().trim();
                if(email.isEmpty())
                {
                    user_name.setError("Username is required");
                    user_name.requestFocus();
                    return;
                }
                /*if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    user_name.setError("Enter a valid email address");
                    user_name.requestFocus();
                    return;
                }*/
                if(password.isEmpty())
                {
                    pass_word.setError("Enter password");
                    pass_word.requestFocus();
                    return;
                }
                if(password.length()<6)
                {
                    pass_word.setError("Password must be at least 6 characters long");
                    pass_word.requestFocus();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email + "@tena.com",password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
                            LoginActivity.user = email;
                            Intent continueIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            RegisterActivity.this.startActivity(continueIntent);
                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"You are not Registered! Try again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}