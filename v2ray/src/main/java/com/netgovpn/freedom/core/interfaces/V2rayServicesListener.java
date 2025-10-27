package com.netgovpn.freedom.core.interfaces;

import android.app.Service;

public interface V2rayServicesListener {
    boolean onProtect(final int socket);
    Service getService();
    void startService();
    void stopService();
}
