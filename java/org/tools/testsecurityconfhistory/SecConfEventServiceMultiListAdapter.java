package org.tools.testsecurityconfhistory;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.tools.testsecurityconfhistory.R;

import android.security.SecurityConfigurationHistoryManager;
import android.security.securityconfhistory.SecurityConfigurationEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class SecConfEventServiceMultiListAdapter extends BaseAdapter {
    private Context context;
    private PackageManager pm;
    private List<SecurityConfigurationEvent> listdb;
    private SecurityConfigurationHistoryManager sec_manager;
    private final String TAG = "SecConfEventServiceAdapter";
    private Mode display_mode;
    
    public enum Mode {
        SHOW_ALL_PERMISSION_EVENTS,
        SHOW_LATEST_PERMISSION_EVENTS
    };    
    
    public SecConfEventServiceMultiListAdapter(Context incontext) {
        context = incontext;
        pm = context.getPackageManager();
        sec_manager = (SecurityConfigurationHistoryManager) context.getSystemService(Context.SECURITY_CONF_HISTORY_READER_SERVICE);
        listdb = sec_manager.listEvents();
        display_mode = Mode.SHOW_ALL_PERMISSION_EVENTS;
    }   
    
    public void updateList() {
        switch(display_mode) {
            case SHOW_ALL_PERMISSION_EVENTS:
                updateListWithAllPermissions();
                break;
            case SHOW_LATEST_PERMISSION_EVENTS:
                updateListWithLeftGrantedPermissions();
                break;
            default:
                updateListWithAllPermissions();
                break;
        }
        Log.d(TAG, "**** updateList list size : "+ String.valueOf(listdb.size()));        
        notifyDataSetChanged();
    }

    public void setMode(Mode mode) {
        Mode oldstate = display_mode;
        display_mode = mode;
        if (display_mode == oldstate) return;

    }

    public Mode getMode() {
        return display_mode;
    }

    private void updateListWithAllPermissions() {
        listdb = sec_manager.listEvents();
    }

    private void updateListWithLeftGrantedPermissions() {
        List<SecurityConfigurationEvent> res = new ArrayList<SecurityConfigurationEvent>();
        Map<String,List<SecurityConfigurationEvent>> package_permissions = Utils.getEventsPerPackage(sec_manager.listEvents());

        for(Map.Entry<String, List<SecurityConfigurationEvent>> package_entry : package_permissions.entrySet()) {
            Map<String,List<SecurityConfigurationEvent>> permission_events = Utils.getEventsPerPermission(package_entry.getValue());
                for(Map.Entry<String,List<SecurityConfigurationEvent>> permission_entry : permission_events.entrySet()) {
                    SecurityConfigurationEvent lastEvent = Utils.getLastEvent(permission_entry.getValue());
                    if (lastEvent != null)
                        res.add(lastEvent);
                }
        }

        Utils.sortEventListRecentFirst(res);
        listdb = res;
    }    

    @Override
    public int getCount() {
        return listdb.size();
    }

    @Override
    public Object getItem(int i) {
        return listdb.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.secconf_event_view, parent, false);
        }

        // get current item to be displayed
        SecurityConfigurationEvent currentItem = (SecurityConfigurationEvent) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.SecConfEvent);
        TextView textViewItemDescription = (TextView)
                convertView.findViewById(R.id.EventDescription);
        
        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.getEventName());
        textViewItemDescription.setText(currentItem.getDescription(pm));
        
        textViewItemName.setBackgroundColor(getBackgroundColorForEventType(currentItem.getType()));
        textViewItemDescription.setBackgroundColor(getBackgroundColorForEventType(currentItem.getType()));
        // returns the view for the current row
        return convertView;
    }

    private int getBackgroundColorForEventType(int type){
        switch(type) {
            case SecurityConfigurationEvent.GRANT_RUNTIME_CONFIGURATION_EVENT:
                return 0xfff496ad;
            case SecurityConfigurationEvent.REVOKE_RUNTIME_CONFIGURATION_EVENT:
                return 0xffadd0f7;
        }
        return 0xfff496ad;
    }
}
