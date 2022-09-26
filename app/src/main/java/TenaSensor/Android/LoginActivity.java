package TenaSensor.Android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public static boolean loggedIn = false;
    public static String user = "no_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        final TextView register = (TextView) findViewById(R.id.Register);
        final TextView signIn = (TextView) findViewById(R.id.SignIn);
        final Button gRegister = (Button) findViewById(R.id.Register_Google);
        final Button lRegister = (Button) findViewById(R.id.Register_LinkedIn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent continueIntent = new Intent(LoginActivity.this, LinkedInLogin.class);
                LoginActivity.this.startActivity(continueIntent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent continueIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(continueIntent);
            }
        });

        gRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent registerIntent = new Intent(LoginActivity.this, GoogleLogin.class);
                //LoginActivity.this.startActivity(registerIntent);
                Intent continueIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(continueIntent);
            }
        });

        lRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent registerIntent = new Intent(LoginActivity.this, LinkedInLogin.class);
                //LoginActivity.this.startActivity(registerIntent);
                Intent continueIntent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(continueIntent);
            }
        });
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }
}