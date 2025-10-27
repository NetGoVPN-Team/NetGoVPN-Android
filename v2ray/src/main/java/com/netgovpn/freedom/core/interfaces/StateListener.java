package com.netgovpn.freedom.core.interfaces;


import com.netgovpn.freedom.core.utils.V2rayConstants;

public interface StateListener {
    V2rayConstants.CONNECTION_STATES getConnectionState();

    V2rayConstants.CORE_STATES getCoreState();

    long getDownloadSpeed();

    long getUploadSpeed();

}
