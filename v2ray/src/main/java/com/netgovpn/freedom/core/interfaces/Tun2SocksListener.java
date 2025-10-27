package com.netgovpn.freedom.core.interfaces;


import com.netgovpn.freedom.core.utils.V2rayConstants;

public interface Tun2SocksListener {
    void OnTun2SocksHasMassage(V2rayConstants.CORE_STATES tun2SocksState, String newMessage);
}
