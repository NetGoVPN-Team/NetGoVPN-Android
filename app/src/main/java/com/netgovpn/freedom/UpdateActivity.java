package com.netgovpn.freedom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    private String directDownloadUrl;
    private String googlePlayUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Button btnDirect = findViewById(R.id.btn_direct_download);
        Button btnPlay = findViewById(R.id.btn_google_play);

        Intent intent = getIntent();
        directDownloadUrl = intent.getStringExtra("downloadurl");
        googlePlayUrl = intent.getStringExtra("play_url");

        if (directDownloadUrl == null || directDownloadUrl.isEmpty()) {
            directDownloadUrl = "https://example.com/app.apk";
        }
        if (googlePlayUrl == null || googlePlayUrl.isEmpty()) {
            googlePlayUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
        }

        btnDirect.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(directDownloadUrl));
                startActivity(browserIntent);
            } catch (Exception e) {
            }
        });

        btnPlay.setOnClickListener(v -> {
            try {
                Intent playIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
                playIntent.setPackage("com.android.vending");
                startActivity(playIntent);
            } catch (Exception e) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUrl));
                startActivity(browserIntent);
            }
        });
    }
}