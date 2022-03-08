package org.tools.testsecurityconfhistory;

import android.security.securityconfhistory.SecurityConfigurationEvent;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private final static String TAG = "org.tools.testsecurityconfhistory.Utils";
    
    public static String getApplicationNameFromPackageName(PackageManager pm, String packagename) {
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packagename, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            Log.d(TAG, "**** Erreur", e);
            ai = null;
        }
        
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        return applicationName;
    }
    
    // -------------------------------------------------------
    // sort the events list putting the most recent in first
    public static void sortEventListRecentFirst(List<SecurityConfigurationEvent> list) {
        list.sort(new Comparator<SecurityConfigurationEvent>() {
            @Override
            public int compare(SecurityConfigurationEvent securityConfigurationEvent, SecurityConfigurationEvent t1) {
                if (securityConfigurationEvent.getTime() > t1.getTime()) return -1;
                if (securityConfigurationEvent.getTime() < t1.getTime()) return +1;
                return 0;
            }
        });
    }

    // -------------------------------------------------------
    // returns the most recent SecurityConfigurationEvent
    public static SecurityConfigurationEvent getLastEvent(List<SecurityConfigurationEvent> list) {
        List<SecurityConfigurationEvent> work = new ArrayList<SecurityConfigurationEvent>(list);

        if (work.size() == 0)
            return null;

        work.sort(new Comparator<SecurityConfigurationEvent>() {
            @Override
            public int compare(SecurityConfigurationEvent securityConfigurationEvent, SecurityConfigurationEvent t1) {
                if (securityConfigurationEvent.getTime() > t1.getTime()) return -1;
                if (securityConfigurationEvent.getTime() < t1.getTime()) return +1;
                return 0;
            }
        });
        return work.get(0);
    }

    // -----------------------------------------------------
    // input :
    //  - eventList : a list of security events
    // returns a Map<packagename, list of security events>
    public static Map<String, List<SecurityConfigurationEvent>> getEventsPerPackage(List<SecurityConfigurationEvent> eventlist) {
        Map<String, List<SecurityConfigurationEvent>> result = new HashMap<String, List<SecurityConfigurationEvent>>();

        for (SecurityConfigurationEvent event : eventlist) {
            List<SecurityConfigurationEvent> item_list = result.get(event.getPackageName());

            if (item_list != null) {
                item_list.add(event);
            } else {
                List<SecurityConfigurationEvent> newtmp = new ArrayList<SecurityConfigurationEvent>();
                newtmp.add(event);
                result.put(event.getPackageName(), newtmp);
            }
        }
        return result;
    }

    // -----------------------------------------------------
    // input :
    //  - eventList : a list of security events
    // returns a Map<permission_name, list of security events>
    public static Map<String, List<SecurityConfigurationEvent>> getEventsPerPermission(List<SecurityConfigurationEvent> eventList) {
        Map<String, List<SecurityConfigurationEvent>> result = new HashMap<String, List<SecurityConfigurationEvent>>();

        for (SecurityConfigurationEvent event : eventList) {
            List<SecurityConfigurationEvent> item_list = result.get(event.getPermissionName());

            if (item_list != null) {
                item_list.add(event);
            } else {
                List<SecurityConfigurationEvent> newtmp = new ArrayList<SecurityConfigurationEvent>();
                newtmp.add(event);
                result.put(event.getPermissionName(), newtmp);
            }
        }
        return result;
    }    
}
