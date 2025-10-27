package com.netgovpn.freedom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutUsActivity extends AppCompatActivity {

    ImageView telegram_click, github_click, x_click;
    TextView tv_telegram_contact_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        telegram_click = findViewById(R.id.telegram_click);
        github_click = findViewById(R.id.github_click);
        x_click = findViewById(R.id.x_click);
        tv_telegram_contact_note = findViewById(R.id.tv_telegram_contact_note);

        telegram_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://NetGoVPN.com"));
                startActivity(browserIntent);
            }
        });
        github_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://NetGoVPN.com"));
                startActivity(browserIntent);
            }
        });
        x_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://NetGoVPN.com"));
                startActivity(browserIntent);
            }
        });
        tv_telegram_contact_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://NetGoVPN.com"));
                startActivity(browserIntent);
            }
        });

    }
}