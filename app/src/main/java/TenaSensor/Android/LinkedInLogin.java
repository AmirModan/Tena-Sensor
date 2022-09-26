package TenaSensor.Android;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class LinkedInLogin extends AppCompatActivity {
    private EditText user_name, pass_word;
    private Button btn_login, btn_sign;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_firebase);
        user_name= (EditText)findViewById(R.id.username);
        pass_word= (EditText)findViewById(R.id.passwordLogin);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_sign = (Button)findViewById(R.id.btn_signup);
        mAuth=FirebaseAuth.getInstance();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= user_name.getText().toString().trim();
                String password=pass_word.getText().toString().trim();
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
                    pass_word.setError("Password is empty");
                    pass_word.requestFocus();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email + "@tena.com",password).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        LoginActivity.user = email;
                        startActivity(new Intent(LinkedInLogin.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(LinkedInLogin.this,
                                "Please Check Your login credentials",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
        btn_sign.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LinkedInLogin.this, RegisterActivity.class));
            }
        });
    }

}