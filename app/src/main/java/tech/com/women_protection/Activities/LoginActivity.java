package tech.com.women_protection.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tech.com.women_protection.R;
import tech.com.women_protection.classes.LocationClass;
import tech.com.women_protection.classes.User;

public class LoginActivity extends AppCompatActivity {
    private int ACCESS_LOCATION_PERMISSION = 1;
    EditText editText_Email, editText_Password;
    Button button_Login;
    TextView button_signup;
    String email = "", password = "";
    DatabaseReference databaseUser, database_complaints, database_location;
    User user_current;
    String database_email = "", database_password = "", database_user_id = "";
    DataSnapshot forLogin_snapshot;
    RadioGroup radiogroup_UserType;
    int selected_UserId;
    String User_Type = "", database_user_type = "", database_user_name = "";
    boolean database_user_isActive = false;
    AlertDialog dialog;
    DataSnapshot forLocation_snapshot;
    List<LocationClass> list = new ArrayList<>();
    LocationClass locationClass;
    int requestcode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        editText_Email = (EditText) findViewById(R.id.editText_Email);
        editText_Password = (EditText) findViewById(R.id.editText_Password);
        button_Login = (Button) findViewById(R.id.button_Login);
        button_signup=(TextView) findViewById(R.id.button_signup);
        radiogroup_UserType = (RadioGroup) findViewById(R.id.radiogroup_UserType);
        databaseUser = FirebaseDatabase.getInstance().getReference("User");
        database_location = FirebaseDatabase.getInstance().getReference("Location");
        SharedPreferences preference = getSharedPreferences("Login", MODE_PRIVATE);
        String shared_user_type = preference.getString("user_type", "");//"No name defined" is the default value.
        String shared_user_name = preference.getString("user_name", "");
        if (shared_user_name != null && !shared_user_name.equalsIgnoreCase("") && shared_user_type != null && !shared_user_type.equalsIgnoreCase("")) {
            gotoMainActivity(shared_user_name, shared_user_type);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoginActivity.this, "You have Permission", Toast.LENGTH_LONG).show();
            } else {
                requestLocationPermission();
            }
        }
        radiogroup_UserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (rb != null) {
                    if (rb.getText().equals("Admin")) {
                        User_Type = "Admin";
                    } else if (rb.getText().equals("Public")) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);
                        navigateToScreens(mView);
                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.setCancelable(true);
                        dialog.show();
                        //User_Type = "Victim";
                    } else {
                        User_Type = "Nothing Selected";
                    }
                }

            }
        });
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressLogin();
            }
                /*String id = databaseUser.push().getKey();
                user_data = new User();
                user_data.setUser_Id(id);
                user_data.setEmail(email);
                user_data.setPassword(password);
                user_data.setName("Aditi");
                user.child(id).setValue(user_data);*/

        });

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user_type", User_Type);
                intent.putExtra("user_name", database_user_name);
                startActivity(intent);
            }
        });
    }

    public void navigateToScreens(View mView) {
        Button button_yes = (Button) mView.findViewById(R.id.button_yes);
        Button button_no = (Button) mView.findViewById(R.id.button_no);
        button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Type = "Witness";
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Click Login Button", Toast.LENGTH_LONG).show();
            }
        });
        button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Type = "User";
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Click Login Button", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void pressLogin() {
        {
            email = editText_Email.getText().toString().trim();
            password = editText_Password.getText().toString().trim();
            selected_UserId = radiogroup_UserType.getCheckedRadioButtonId();
            int enter = 0;
            if (email != null && !email.equalsIgnoreCase("")) {
                if (password != null && !password.equalsIgnoreCase("")) {
                    if (selected_UserId != -1) {
                        for (DataSnapshot Usersnapshot : forLogin_snapshot.getChildren()) {
                            user_current = Usersnapshot.getValue(User.class);
                            database_user_id = user_current.getUser_id();
                            database_email = user_current.getEmail_id();
                            database_password = user_current.getPassword();
                            database_user_type = user_current.getUser_type();
                            database_user_name = user_current.getName();
                            database_user_isActive = user_current.getUser_isActive();
                            if (database_user_isActive == true) {
                                if (database_email.equalsIgnoreCase(email) && database_password.equalsIgnoreCase(password)) {
                                    if ((User_Type.equalsIgnoreCase("Admin") && database_user_type.equalsIgnoreCase(User_Type)) || User_Type.equalsIgnoreCase("User") || User_Type.equalsIgnoreCase("Witness")) {
                                        SharedPreferences preference = getSharedPreferences("Login", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preference.edit();
                                        editor.putString("user_type", User_Type);
                                        editor.putString("user_name", database_user_name);
                                        editor.commit();
                                        enter = 1;
                                        gotoMainActivity(database_user_name, User_Type);

                                    }
                                }
                            } else {
                                enter = 2;
                                Toast.makeText(getApplicationContext(), "You are not an Active User", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        enter = 2;
                        Toast.makeText(getApplicationContext(), "Please select a User Type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    enter = 2;
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_SHORT).show();
                }
            } else {
                enter = 2;
                Toast.makeText(getApplicationContext(), "Please Enter an Email Address", Toast.LENGTH_SHORT).show();
            }
            if (enter == 0) {
                Toast.makeText(getApplicationContext(), "Please Enter Valid Email and Password for " + User_Type + " Login", Toast.LENGTH_LONG).show();
            }
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLogin_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getAllDatabaseValues();

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this).
                    setMessage("Permission Needed")
                    .setMessage("Women Protection wants to access the Location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void gotoMainActivity(String database_user_name, String User_Type) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user_type", User_Type);
        intent.putExtra("user_name", database_user_name);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "You are logged in", Toast.LENGTH_SHORT).show();
    }

    public void getAllDatabaseValues() {
        database_location.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                forLocation_snapshot = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
