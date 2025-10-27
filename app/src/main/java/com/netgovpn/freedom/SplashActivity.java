package com.netgovpn.freedom;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import com.netgovpn.freedom.core.V2rayController;
import com.netgovpn.freedom.core.utils.V2rayConstants;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    TextView splash_short_text;
    private static final int SPLASH_DELAY = 2000;
    public String config_s = "";
    public String ping_s = "";
    public String yesOrN_o = "";
    PingManager pingManager;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            StartAct();
        }
    };

    private void cancelTimer() {
        handler.removeCallbacks(runnable);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        splash_short_text = findViewById(R.id.splash_short_text);


        SharedPreferences sharedPreferences2 = getDefaultSharedPreferences(SplashActivity.this);
        boolean termsAccepted = sharedPreferences2.getBoolean("termsAccepted", false);
        splash_short_text.setText(sharedPreferences2.getString("note",""));
        if (!termsAccepted) {
            Intent intent = new Intent(SplashActivity.this, TermsActivity.class);
            startActivity(intent);
            finish();
        } else {
            yesOrN_o = "";
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                if (NetworkUtils.isNetworkAvailable(SplashActivity.this)) {
                    Intent vpnIntent = VpnService.prepare(this);
                    if (vpnIntent != null) {
                    }else {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        String jsonConfig = prefs.getString("config_array", null);

                        if (jsonConfig != null) {
                            Gson gson = new Gson();
                            String[] savedConfig = gson.fromJson(jsonConfig, String[].class);

                            Check_servers(savedConfig);
                        }
                    }
                }
            }

            handler.postDelayed(runnable, SPLASH_DELAY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    public void StartAct(){
        cancelTimer();
        Intent intentSplash = new Intent(SplashActivity.this, MainActivity.class);
        intentSplash.putExtra("config_s", config_s);
        intentSplash.putExtra("ping_s", ping_s);
        intentSplash.putExtra("yesOrN_o", yesOrN_o);
        if (pingManager != null) {
            pingManager.stop();
        }
        startActivity(intentSplash);
        finish();
    }

    private void Check_servers(String[] config) {


        pingManager = new PingManager(
                20,      // maxConcurrency
                2000,    // per-server timeout = 2000 ms
                6000     // overall timeout = 7000 ms
        );

        List<String> configs = Arrays.asList(config);

        pingManager.startPing(configs, serverConfig -> {

            return Math.toIntExact(V2rayController.getV2rayServerDelay(serverConfig));
        }, new PingManager.PingCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFirstSuccess(@NonNull PingManager.PingResult result) {
                ping_s = result.delayMs + "";
                config_s = result.serverConfig;
                yesOrN_o = "yes";
                StartAct();

            }

            @Override
            public void onAllFailed(@NonNull List<PingManager.PingResult> results) {
                yesOrN_o = "";
                config_s = "";
                ping_s = "";
            }
        });
    }

}
