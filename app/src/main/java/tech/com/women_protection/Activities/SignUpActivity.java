package tech.com.women_protection.Activities;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tech.com.women_protection.R;
import tech.com.women_protection.classes.User;

public class SignUpActivity extends AppCompatActivity {
    Button btn_signup;
    EditText edittext_input_first_name, edittext_input_last_name, editText_input_Email, edittext_input_password, edittext_valid_identity, edittext_valid_phone;
    DatabaseReference databaseUser;
    String first_name, last_name, fullname, email, password, identity, phone_number;
    String uid;
    RadioGroup radiogroup_input_gender;
    int selected_UserId;
    String gender;
    boolean valid = true;
    TextView link_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_signup);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        btn_signup = (Button) findViewById(R.id.btn_signup);
        link_login = (TextView) findViewById(R.id.link_login);
        edittext_input_first_name = (EditText) findViewById(R.id.edittext_input_first_name);
        edittext_input_last_name = (EditText) findViewById(R.id.edittext_input_last_name);
        editText_input_Email = (EditText) findViewById(R.id.editText_input_Email);
        edittext_input_password = (EditText) findViewById(R.id.edittext_input_password);
        edittext_valid_identity = (EditText) findViewById(R.id.edittext_valid_identity);
        edittext_valid_phone = (EditText) findViewById(R.id.edittext_valid_phone);
        radiogroup_input_gender = (RadioGroup) findViewById(R.id.radiogroup_input_gender);
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        radiogroup_input_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    if (rb.getText().equals("Male")) {
                        gender = "Male";
                    } else if (rb.getText().equals("Female")) {
                        gender = "Female";
                    }
                }

            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void signUp() {

        if (!validate()) {
            onSignupFailed();
            return;
        } else {
            first_name = edittext_input_first_name.getText().toString();
            last_name = edittext_input_last_name.getText().toString();
            fullname = first_name + " " + last_name;
            email = editText_input_Email.getText().toString();
            password = edittext_input_password.getText().toString();
            identity = edittext_valid_identity.getText().toString();
            phone_number = edittext_valid_phone.getText().toString();

            Date date = new Date();
            String strDateFormat = "hh:mm:ss a";
            DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
            String formattedDate = dateFormat.format(date);
            try {
                uid = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            String id = databaseUser.push().getKey();
            User user_data = new User();
            user_data = new User();
            user_data.setUser_id(id);
            user_data.setName(fullname);
            user_data.setEmail_id(email);
            user_data.setPassword(password);
            user_data.setIdentity(identity);
            user_data.setPhone_Number(phone_number);
            user_data.setRegister_date(formattedDate);
            user_data.setUser_isActive(true);
            user_data.setPerson_isActive(true);
            user_data.setUser_type("User");
            user_data.setDevice_id(uid);
            user_data.setGender(gender);
            databaseUser.child(id).setValue(user_data);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Your account has been activated successfully. You can now login.", Toast.LENGTH_LONG).show();
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        first_name = edittext_input_first_name.getText().toString();
        last_name = edittext_input_last_name.getText().toString();
        fullname = first_name + " " + last_name;
        email = editText_input_Email.getText().toString();
        password = edittext_input_password.getText().toString();
        identity = edittext_valid_identity.getText().toString();
        phone_number = edittext_valid_phone.getText().toString();
        selected_UserId = radiogroup_input_gender.getCheckedRadioButtonId();

        if (first_name != null) {
            if (first_name.isEmpty() || first_name.length() < 3) {
                edittext_input_first_name.setError("at least 3 characters");
                valid = false;
            } else {
                edittext_input_first_name.setError(null);
            }
        }

        if (last_name != null) {
            if (last_name.isEmpty() || last_name.length() < 3) {
                edittext_input_last_name.setError("at least 3 characters");
                valid = false;
            } else {
                edittext_input_last_name.setError(null);
            }
        }

        if (email != null) {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editText_input_Email.setError("enter a valid email address");
                valid = false;
            } else {
                editText_input_Email.setError(null);
            }
        }

        if (password != null) {
            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                edittext_input_password.setError("between 4 and 10 alphanumeric characters");
                valid = false;
            } else {
                edittext_input_password.setError(null);
            }
        }

        if (identity != null) {
            if (identity.isEmpty() || identity.length() < 4 || identity.length() > 15) {
                edittext_valid_identity.setError("between 4 and 15 alphanumeric characters");
                valid = false;
            } else {
                edittext_valid_identity.setError(null);
            }
        }

        if (phone_number != null) {
            if (phone_number.isEmpty() || !Patterns.PHONE.matcher(phone_number).matches()) {
                edittext_valid_phone.setError("Enter valid phone number");
                valid = false;
            } else {
                edittext_valid_phone.setError(null);
            }
        }

        if (selected_UserId != -1) {
            valid = true;
        } else {
            valid = false;
            Toast.makeText(getApplicationContext(), "Select Gender", Toast.LENGTH_LONG).show();
        }

        return valid;
    }
}
