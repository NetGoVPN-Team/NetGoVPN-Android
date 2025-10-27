package com.netgovpn.freedom;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {
    private static final int REQ_VPN = 100;
    private static final int REQ_NOTIF = 200;

    private final Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }


    public boolean checkPermissions() {
        boolean vpnOk = (VpnService.prepare(activity) == null);
        boolean notifOk = areNotificationsEnabled();

        if (vpnOk && notifOk) return true;

        if (!vpnOk) requestVpnPermission();
        if (!notifOk) requestNotificationPermission();

        return false;
    }

    // ---------------- VPN ----------------
    private void requestVpnPermission() {
        Intent intent = VpnService.prepare(activity);
        if (intent != null) {
            activity.startActivityForResult(intent, REQ_VPN);
        } else {
        }
    }

    // ---------------- Notification ----------------
    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(activity).areNotificationsEnabled();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity,
                    android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        android.Manifest.permission.POST_NOTIFICATIONS)) {
                    
				
                    goToAppSettings();
                    Toast.makeText(activity,
                            activity.getText(R.string.notif_per_setting),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        REQ_NOTIF);
                return;
            }
        }

        
		
        if (!areNotificationsEnabled()) {
            goToAppSettings();
            Toast.makeText(activity,
                    activity.getText(R.string.notif_per_setting),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void goToAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(android.net.Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    // ---------------- Callbacks ----------------
    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQ_VPN) {
            boolean vpnOk = (VpnService.prepare(activity) == null);
            if (!vpnOk) {
                Toast.makeText(activity, activity.getText(R.string.vpn_per), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == REQ_NOTIF) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(activity, activity.getText(R.string.notif_per), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
