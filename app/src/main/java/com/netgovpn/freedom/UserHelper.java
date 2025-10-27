package com.netgovpn.freedom;

import java.util.TimeZone;
import android.os.Build;
import android.content.Context;

public class UserHelper {

    public static boolean isLikelyWealthyRegion() {
       
        TimeZone tz = TimeZone.getDefault();
        String tzId = tz.getID();


        String[] wealthyTimeZones = {
                
                "Europe/London", "Europe/Dublin", "Europe/Paris", "Europe/Berlin",
                "Europe/Madrid", "Europe/Rome", "Europe/Zurich", "Europe/Vienna",
                "Europe/Oslo", "Europe/Stockholm", "Europe/Copenhagen",
                "Europe/Amsterdam", "Europe/Brussels", "Europe/Luxembourg",
                "Europe/Helsinki", "Europe/Reykjavik", "Europe/Monaco",
                "Europe/San_Marino", "Europe/Liege", "Europe/Andorra",

                
                "Europe/Lisbon", "Europe/Malta", "Europe/Athens", "Europe/Valletta",
                "Europe/Podgorica", "Europe/Belgrade", "Europe/Bratislava",
                "Europe/Ljubljana", "Europe/Vaduz",

                
                "America/New_York", "America/Detroit", "America/Chicago", "America/Denver",
                "America/Los_Angeles", "America/Toronto", "America/Vancouver", "America/Montreal",
                "America/Winnipeg", "America/Edmonton", "America/Calgary", "America/Halifax",
                "America/Anchorage", "America/Phoenix", "America/Adak",

                
                "Australia/Sydney", "Australia/Melbourne", "Australia/Brisbane",
                "Australia/Perth", "Australia/Adelaide", "Australia/Hobart",
                "Pacific/Auckland", "Pacific/Chatham",

                
                "Asia/Tokyo", "Asia/Seoul", "Asia/Singapore", "Asia/Hong_Kong",
                "Asia/Taipei", "Asia/Shanghai", "Asia/Osaka",

                
                "Asia/Dubai", "Asia/Abu_Dhabi", "Asia/Qatar", "Asia/Bahrain",
                "Asia/Kuwait", "Asia/Riyadh", "Asia/Muscat",

                
                "Pacific/Guam", "Pacific/Saipan", "America/Santiago", "America/Montevideo",
                "America/Caracas", "America/Paramaribo", "America/Puerto_Rico",
                "Pacific/Fiji", "Pacific/Tongatapu", "Pacific/Palau", "Pacific/Nauru"
        };


        for (String wealthyTz : wealthyTimeZones) {
            if (tzId.equals(wealthyTz)) {
                return true;
            }
        }
        return false;
    }
}
