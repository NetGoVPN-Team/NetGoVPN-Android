package com.netgovpn.freedom;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_DURATION_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA;
import static com.netgovpn.freedom.core.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AlertDialog;


import com.google.gson.Gson;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.netgovpn.freedom.core.V2rayController;
import com.netgovpn.freedom.core.utils.V2rayConfigs;
import com.netgovpn.freedom.core.utils.V2rayConstants;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_VPN = 1001;

    PingManager pingManager;
    private int ID_ABOUT;
    private int ID_HELP;
    private int ID_TERMS;
    private String PING_URL = "http://clients3.google.com/generate_204";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private View connection;
    private TextView connection_speed, connection_traffic, connection_time, connected_server_delay, connection_mode;
    private LinearLayout details;
    private ImageView donation;
    private ImageView on_off;
    private BroadcastReceiver v2rayBroadCastReceiver;
    private AlertDialog dialog;
    private SpeedDialView speedDialView;
    private MultiRequestManager manager;

    public String config_s = "";
    public String ping_s = "";
    public String yesOrN_o = "";

    private boolean trys = false;

    @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences2 = getDefaultSharedPreferences(MainActivity.this);
        boolean termsAccepted = sharedPreferences2.getBoolean("termsAccepted", false);
        if (!termsAccepted) {
            Intent intent = new Intent(MainActivity.this, TermsActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);


        ID_ABOUT = View.generateViewId();
        ID_HELP = View.generateViewId();
        ID_TERMS = View.generateViewId();

        if (savedInstanceState == null) {
            V2rayController.init(this, R.drawable.dot_net, "NetGoVPN");
            connection = findViewById(R.id.btn_connection);
            details = findViewById(R.id.details);
            donation = findViewById(R.id.donation);
            on_off = findViewById(R.id.on_off);
            connection_speed = findViewById(R.id.connection_speed);
            connection_time = findViewById(R.id.connection_duration);
            connection_traffic = findViewById(R.id.connection_traffic);
            connection_mode = findViewById(R.id.connection_mode);
            connected_server_delay = findViewById(R.id.connected_server_delay);
            speedDialView = findViewById(R.id.speedDial);
        }

        Intent intent = getIntent();

        config_s = intent.getStringExtra("config_s");
        ping_s = intent.getStringExtra("ping_s");
        yesOrN_o = intent.getStringExtra("yesOrN_o");

        speedDialView = findViewById(R.id.speedDial);

        ColorStateList myBlue = ColorStateList.valueOf(Color.parseColor("#2196F3"));
        ColorStateList white = ColorStateList.valueOf(Color.WHITE);

        if (speedDialView != null) {

            if (speedDialView.getMainFab() != null) {
                speedDialView.getMainFab().setBackgroundTintList(white);
                speedDialView.getMainFab().setImageTintList(myBlue);
            }

            speedDialView.setOnChangeListener(new SpeedDialView.OnChangeListener() {
                @Override
                public boolean onMainActionSelected() {
                    return false;
                }

                @Override
                public void onToggleChanged(boolean isOpen) {
                    if (speedDialView.getMainFab() != null) {
                        speedDialView.getMainFab().setBackgroundTintList(white);
                        speedDialView.getMainFab().setImageTintList(myBlue);
                    }
                }
            });

            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(ID_ABOUT, R.drawable.about_us)
                            .setLabel(getString(R.string.flat_about))
                            .setFabBackgroundColor(getResources().getColor(android.R.color.white))
                            .setFabImageTintColor(Color.parseColor("#2196F3"))
                            .create()
            );

            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(ID_HELP, R.drawable.hands_heart)
                            .setLabel(getString(R.string.flat_donate))
                            .setFabBackgroundColor(getResources().getColor(android.R.color.white))
                            .setFabImageTintColor(Color.parseColor("#2196F3"))
                            .create()
            );

            speedDialView.addActionItem(
                    new SpeedDialActionItem.Builder(ID_TERMS, R.drawable.termsofuse)
                            .setLabel(getString(R.string.flat_terms))
                            .setFabBackgroundColor(getResources().getColor(android.R.color.white))
                            .setFabImageTintColor(Color.parseColor("#2196F3"))
                            .create()
            );

            speedDialView.setOnActionSelectedListener(actionItem -> {
                int id = actionItem.getId();

                if (id == ID_ABOUT) {
                    startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                } else if (id == ID_HELP) {
                    startActivity(new Intent(MainActivity.this, DonateActivity.class));
                } else if (id == ID_TERMS) {
                    startActivity(new Intent(MainActivity.this, TermsActivity.class));
                }

                return false;
            });

        } else {
        }


        manager = new MultiRequestManager();

        connection.setOnClickListener(view -> {
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                    Intent vpnIntent = VpnService.prepare(this);
                    if (vpnIntent != null) {
                        startActivityForResult(vpnIntent, REQUEST_VPN);
                    } else {
                        if ("yes".equals(yesOrN_o) && !"".equals(ping_s) && !"".equals(config_s)) {

                            ArrayList<String> chromePackages = new ArrayList<>();
                            chromePackages.add("com.netgovpn.freedom");

                            V2rayController.startV2ray(MainActivity.this, "NetGoVPN",
                                    config_s,
                                    chromePackages);

                            connected_server_delay.setText(getText(R.string.server_ping) + " " + ping_s + " " + getText(R.string.server_ms));
                            ping_s = "";
                            config_s = "";
                            yesOrN_o = "";

                            SharedPreferences sharedPreferences = getDefaultSharedPreferences(MainActivity.this);
                            int myNumber = sharedPreferences.getInt("key_number", 0);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            myNumber++;
                            editor.putInt("key_number", myNumber);
                            editor.apply();
                            if (myNumber % sharedPreferences.getInt("donate", 10) == 0) {
                                ShowDialog();
                            }

                        } else {
                            trys = false;
                            Server_Request();
                        }
                    }

                } else {
                    Toast.makeText(this, getText(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            } else {
                V2rayController.stopV2ray(this);
            }
        });


        // Check the connection delay of connected config.
        connected_server_delay.setOnClickListener(view -> {
            connected_server_delay.setText(getText(R.string.measuring));
            pingOnce();
        });

        // Another way to check the connection delay of a config without connecting to it.
        donation.setOnClickListener(view -> {
            Intent intentdonates = new Intent(MainActivity.this, DonateActivity.class);
            startActivity(intentdonates);
        });

        connection_mode.setOnClickListener(view -> {
            V2rayController.toggleConnectionMode();
            connection_mode.setText("connection mode : " + V2rayConfigs.serviceMode.toString());
        });

        // Check connection state when activity launch
        switch (V2rayController.getConnectionState()) {
            case CONNECTED:
//                connection.setText("CONNECTED");
                details.setVisibility(View.VISIBLE);
                donation.setVisibility(View.VISIBLE);
                on_off.setImageResource(R.drawable.power_switch);
                // check  connection latency
                connected_server_delay.callOnClick();
                break;
            case DISCONNECTED:
//                connection.setText("DISCONNECTED");
                details.setVisibility(View.GONE);
                donation.setVisibility(View.GONE);
                on_off.setImageResource(R.drawable.power_switch_off);
                break;
            case CONNECTING:
//                connection.setText("CONNECTING");
                details.setVisibility(View.GONE);
                donation.setVisibility(View.GONE);
                on_off.setImageResource(R.drawable.power_switch_off);
                break;
            default:
                break;
        }


        v2rayBroadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> {
                    connection_time.setText("time : " + Objects.requireNonNull(intent.getExtras()).getString(SERVICE_DURATION_BROADCAST_EXTRA));
                    connection_speed.setText(getText(R.string.speed) + intent.getExtras().getString(SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA) + " | " + intent.getExtras().getString(SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA));
                    connection_traffic.setText(getText(R.string.traffic) + intent.getExtras().getString(SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA) + " | " + intent.getExtras().getString(SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA));
                    connection_mode.setText("mode : " + V2rayConfigs.serviceMode.toString());
                    switch ((V2rayConstants.CONNECTION_STATES) Objects.requireNonNull(intent.getExtras().getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA))) {
                        case CONNECTED:
//                            connection.setText("CONNECTED");
                            details.setVisibility(View.VISIBLE);
                            donation.setVisibility(View.VISIBLE);
                            on_off.setImageResource(R.drawable.power_switch);
                            break;
                        case DISCONNECTED:
//                            connection.setText("DISCONNECTED");
                            details.setVisibility(View.GONE);
                            donation.setVisibility(View.GONE);
                            on_off.setImageResource(R.drawable.power_switch_off);
                            connected_server_delay.setText("server ping : click here for ping");
                            break;
                        case CONNECTING:
//                            connection.setText("CONNECTING");
                            details.setVisibility(View.GONE);
                            donation.setVisibility(View.GONE);
                            on_off.setImageResource(R.drawable.power_switch_off);
                            break;
                        default:
                            break;
                    }
                });
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT), RECEIVER_EXPORTED);
        } else {
            registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
        if (v2rayBroadCastReceiver != null) {
            unregisterReceiver(v2rayBroadCastReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            manager.cancelAll();
            dialog.dismiss();
            if (pingManager != null) {
                pingManager.stop();
            }
        } catch (Exception e) {

        }
    }

    private void pingOnce() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String resultText;
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(PING_URL);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setInstanceFollowRedirects(false);

                    long start = System.nanoTime();
                    conn.connect();
                    int code = conn.getResponseCode();
                    long elapsedMs = (System.nanoTime() - start) / 1_000_000L;

                    resultText = "" + getText(R.string.server_ping) + " " + elapsedMs + " " + getText(R.string.server_ms);
                } catch (Exception e) {
                    resultText = getText(R.string.Error) + "";
                } finally {
                    if (conn != null) conn.disconnect();
                }


                final String finalText = resultText;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        connected_server_delay.setText(finalText);
                    }
                });
            }
        });
    }

    private void Server_Request() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_connecting, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        manager = new MultiRequestManager();

        manager.fetch("https://server.com/api", "https://server2.com/api", new MultiRequestManager.Listener() {
            @Override
            public void onSuccess(JSONObject json) {

                runOnUiThread(() -> {

                    try {

                        int sendata = json.getInt("sendata");
                        int donate = json.getInt("donate");

                        int version = json.getInt("version");
                        PING_URL = json.getString("pingurl");
                        String downloadUrl = json.getString("downloadurl");
                        if (version > BuildConfig.VERSION_CODE) {
                            Intent Upintent = new Intent(MainActivity.this, UpdateActivity.class);
                            Upintent.putExtra("downloadUrl", downloadUrl);
                            startActivity(Upintent);
                            finish();
                        } else {

                            JSONArray serArray = json.getJSONArray("ser");

                            String[] config = new String[serArray.length()];
                            for (int i = 0; i < serArray.length(); i++) {
                                config[i] = serArray.getString(i);
                            }

                            Gson gson = new Gson();
                            String jsonConfig = gson.toJson(config);

                            JSONObject walletsObject = json.optJSONObject("wallets");
                            String walletsJson = walletsObject != null ? walletsObject.toString() : "{}";

                            SharedPreferences prefs = getDefaultSharedPreferences(MainActivity.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("wallets_json", walletsJson);
                            editor.putInt("sendata", sendata);
                            editor.putInt("donate", donate);
                            editor.apply();

                            Check_servers(config);
                        }

                    } catch (JSONException e) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, getText(R.string.server_issue), Toast.LENGTH_LONG).show();

                        throw new RuntimeException(e);
                    }

                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, getText(R.string.server_issue), Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                });
            }
        });

    }

    private void Check_servers(String[] config) {


        pingManager = new PingManager(
                30,      // maxConcurrency
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

                ArrayList<String> chromePackages = new ArrayList<>();


                chromePackages.add("com.netgovpn.freedom");

                V2rayController.startV2ray(MainActivity.this, "NetGoVPN",
                        result.serverConfig, chromePackages);

                connected_server_delay.setText(getText(R.string.server_ping) + " " + result.delayMs + " " + getText(R.string.server_ms));
                dialog.dismiss();

                SharedPreferences sharedPreferences = getDefaultSharedPreferences(MainActivity.this);
                int myNumber = sharedPreferences.getInt("key_number", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                myNumber++;
                editor.putInt("key_number", myNumber);
                editor.apply();
                if (myNumber % sharedPreferences.getInt("donate", 10) == 0) {
                    ShowDialog();
                }
            }

            @Override
            public void onAllFailed(@NonNull List<PingManager.PingResult> results) {

                if (trys) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, getText(R.string.server_not_work), Toast.LENGTH_LONG).show();
                } else {
                    trys = true;
                    dialog.dismiss();
                    Server_Request();
                }
            }
        });
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.donate_us, null);
        builder.setView(view);

        dialog = builder.create();
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        Button btnAcpt = view.findViewById(R.id.btn_acpt);

        btnAcpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent donateIntent = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(donateIntent);

                dialog.dismiss();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (connection == null) {
            connection = findViewById(R.id.btn_connection);
        }

        if (details == null) {
            details = findViewById(R.id.details);
        }

        if (donation == null) {
            donation = findViewById(R.id.donation);
        }

        if (on_off == null) {
            on_off = findViewById(R.id.on_off);
        }

        if (connection_speed == null) {
            connection_speed = findViewById(R.id.connection_speed);
        }

        if (connection_time == null) {
            connection_time = findViewById(R.id.connection_duration);
        }

        if (connection_traffic == null) {
            connection_traffic = findViewById(R.id.connection_traffic);
        }

        if (connection_mode == null) {
            connection_mode = findViewById(R.id.connection_mode);
        }

        if (connected_server_delay == null) {
            connected_server_delay = findViewById(R.id.connected_server_delay);
        }

        if (speedDialView == null) {
            speedDialView = findViewById(R.id.speedDial);
        }

    }


}