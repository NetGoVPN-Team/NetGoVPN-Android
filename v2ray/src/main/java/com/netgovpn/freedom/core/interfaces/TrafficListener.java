package com.netgovpn.freedom.core.interfaces;

public interface TrafficListener {
    void onTrafficChanged(long uploadSpeed, long downloadSpeed, long uploadedTraffic, long downloadedTraffic);
}
