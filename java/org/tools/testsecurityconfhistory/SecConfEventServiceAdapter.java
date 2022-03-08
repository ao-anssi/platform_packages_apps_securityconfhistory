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

public class SecConfEventServiceAdapter extends BaseAdapter {
    private Context context;
    private PackageManager pm;
    private List<SecurityConfigurationEvent> listdb;
    private SecurityConfigurationHistoryManager sec_manager;
    private final String TAG = "SecConfEventServiceAdapter";

    public SecConfEventServiceAdapter(Context incontext) {
        context = incontext;
        pm = context.getPackageManager();
        sec_manager = (SecurityConfigurationHistoryManager) context.getSystemService(Context.SECURITY_CONF_HISTORY_READER_SERVICE);
        listdb = sec_manager.listEvents();
    }

    public void updateList() {
        listdb = sec_manager.listEvents();
        Log.d(TAG, "**** updateList list size : "+ String.valueOf(listdb.size()));
        notifyDataSetChanged();
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
