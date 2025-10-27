package com.netgovpn.freedom;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TermsActivity extends AppCompatActivity {

    /// /
    private Button btn_acpt;
    private TextView textView9;
    private static final int REQUEST_NOTIFICATION = 1002;
    private boolean notificationGranted = false;

    private final ActivityResultLauncher<Intent> requestVpnPermission =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                        } else {
                            Toast.makeText(this, getText(R.string.vpn_per), Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        btn_acpt = findViewById(R.id.btn_acpt);
        textView9 = findViewById(R.id.textView9);

        textView9.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://netgovpn.com"));
            startActivity(browserIntent);
        });

        btn_acpt.setOnClickListener(view -> {
            checknotif();
            Intent intent2 = VpnService.prepare(this);
            if (intent2 != null || !notificationGranted) {
                try {
                    requestVpnPermission.launch(intent2);
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_NOTIFICATION
                    );
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                    startActivity(intent);

                    Toast.makeText(this, getText(R.string.notif_per), Toast.LENGTH_SHORT).show();
                }
            } else {

                SharedPreferences prefs = getDefaultSharedPreferences(TermsActivity.this);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putBoolean("termsAccepted", true);
                editor.apply();

                Intent intent = new Intent(TermsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = VpnService.prepare(this);
        if (intent == null) {
        } else {
            requestVpnPermission.launch(intent);
        }


        // Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                try {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_NOTIFICATION
                    );
                } catch (Exception e) {

                }

            } else {
                notificationGranted = true;
            }
        } else {
            notificationGranted = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION) {
            notificationGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!notificationGranted) {
                Toast.makeText(this, getText(R.string.notif_per), Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void checknotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
            } else {
                notificationGranted = true;
            }
        } else {
            notificationGranted = true;
        }
    }

}


