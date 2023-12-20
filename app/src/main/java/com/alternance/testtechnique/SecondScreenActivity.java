package com.alternance.testtechnique;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This is the activity matching the second page of the application
 *
 * The OracleConnection() method and thee bloc about StrictMode are written thanks to the document of Saliha Yacoub et Denis Brunet
 * Source: http://salihayacoub.com/420Khg/Semaine%2012/Application%20JDBC.pdf
 */
public class SecondScreenActivity extends AppCompatActivity {

    public static Connection conn_ = null;

    /** The address of the database */
    public EditText address;

    /** The username to connect to the database */
    public EditText username;

    /** The password to connect to the database */
    public EditText password;

    /** The port to connect to the database */
    public EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_secondscreen);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog() //Sends a message to logcat
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath() // The app stops itself, works at the end of all allowed sanctions
                .build());

        /*
        This bloc enables the fullscreen mode
         */
        getWindow().setDecorFitsSystemWindows(false);
        WindowInsetsController controller = getWindow().getInsetsController();
        if (controller != null) {
            controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    /**
     * This method allows the connection to an Oracle database
     */
    public void OracleConnection(View view) {

        //This allows to match the address, username, password and port attributes to their respective field in the UI
        address = findViewById(R.id.address);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        port = findViewById(R.id.port);

        //This bloc allows to connect to an Oracle database
        Thread t = new Thread(() -> {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
            } catch (
                    ClassNotFoundException e
            )
            {
                Toast.makeText(SecondScreenActivity.this, "Driver is missing." + e.getMessage(), Toast.LENGTH_LONG).show(); // The message sent when the driver is missing (a driver is needed to connect to a database)
            }

            //The URL of the database is generated with the text typed in the fields "address" and "port"
            String jdbcURL = "jdbc:oracle:thin:@" + SecondScreenActivity.this.address.getText().toString() + ":" + SecondScreenActivity.this.port.getText().toString() + ":XE";

            //The username is get with the text typed in the field
            String user = SecondScreenActivity.this.username.getText().toString();

            //The password is get with the text typed in the field
            String password = SecondScreenActivity.this.password.getText().toString();

            try {
                conn_ = DriverManager.getConnection(jdbcURL,user,password);

                // This display a message if the connection is successfully established
                runOnUiThread(() -> Toast.makeText(SecondScreenActivity.this, "Connection established.", Toast.LENGTH_LONG).show());
            } catch (
                    SQLException se
            )
            {
                // This display a message if the connection failed + the reason of the error
                runOnUiThread(() -> Toast.makeText(SecondScreenActivity.this, "Connection failed: " + se.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
        t.start();
    }
}