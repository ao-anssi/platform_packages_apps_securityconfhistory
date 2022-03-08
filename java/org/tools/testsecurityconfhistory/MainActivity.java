package org.tools.testsecurityconfhistory;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ListView;
import android.util.Log;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.security.SecurityConfigurationHistoryManager;
import android.security.securityconfhistory.SecurityConfigurationEvent;

public class MainActivity extends AppCompatActivity {

    final private String TAG = "MainActivity";
    private RadioButton allEventsButton;
    private RadioButton latestEventsButton;    
    private SecConfEventServiceMultiListAdapter m_adapter; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final ListView listview = (ListView) findViewById(R.id.perm_list_view);
        final Button updateButton = (Button)  findViewById(R.id.button);
        allEventsButton = (RadioButton) findViewById(R.id.allEventsRadioButton);
        latestEventsButton = (RadioButton) findViewById(R.id.latestEventRadioButton);
        
        SecurityConfigurationHistoryManager sec_manager = (SecurityConfigurationHistoryManager) this.getSystemService(Context.SECURITY_CONF_HISTORY_READER_SERVICE);
        
        if (sec_manager == null) {
            Log.d(TAG, "**** SecurityConfigurationHistoryManager is null");
        } else {
            // List<SecurityConfigurationEvent> l = sec_manager.listEvents();
            Log.d(TAG, "**** SecurityConfigurationHistoryManager is ok");
        }

        m_adapter = new SecConfEventServiceMultiListAdapter(this);
        m_adapter.setMode(SecConfEventServiceMultiListAdapter.Mode.SHOW_LATEST_PERMISSION_EVENTS);
        listview.setAdapter(m_adapter);
        m_adapter.updateList();
        updateRadioButtons();
        
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SecurityConfigurationEvent event =(SecurityConfigurationEvent) m_adapter.getItem(position);
                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                
                Uri uri = Uri.fromParts("package",  event.getPackageName(), /* fragment= */ null);
                i.setData(uri);
                
                startActivity(i);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  
                m_adapter.updateList();
            }
        });
        
        allEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_adapter.setMode(SecConfEventServiceMultiListAdapter.Mode.SHOW_ALL_PERMISSION_EVENTS);
                m_adapter.updateList();
            }
        });

        latestEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_adapter.setMode(SecConfEventServiceMultiListAdapter.Mode.SHOW_LATEST_PERMISSION_EVENTS);
                m_adapter.updateList();
            }
        });        
    }
    
    private void updateRadioButtons() {
        switch (m_adapter.getMode()) {
            case SHOW_ALL_PERMISSION_EVENTS:
                allEventsButton.setChecked(true);
                latestEventsButton.setChecked(false);
                break;
            case SHOW_LATEST_PERMISSION_EVENTS:
                latestEventsButton.setChecked(true);
                allEventsButton.setChecked(false);
                break;
        }
    }    

    @Override
    protected void onResume() {
        super.onResume();
        m_adapter.updateList();
    }

    
    
    @Override
    protected void onStop() {
        super.onStop();
    }

}
