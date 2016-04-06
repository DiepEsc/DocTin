package diep.esc.doctin.gui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.adapter.MyExpandableListAdapter;
import diep.esc.doctin.gui.adapter.RssSource;
import diep.esc.doctin.util.OptionsUtils;

/**
 * Created by Diep on 04/04/2016.
 */
public class OptionsActivity extends Activity {
    private static final String TAG = "log_Options";
    private ExpandableListView expandableListView;
    //    private String selectedRss;
    private MyExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        expandableListView = (ExpandableListView) findViewById(R.id.e_list_view);
        adapter = new MyExpandableListAdapter(this, RssSource.loadDefault(this));
        adapter.setSelectedRss(OptionsUtils.getSelectedRss(this));
        expandableListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        String selectedRss=adapter.getSelectedRss();
        Log.d(TAG, "onPause rss="+selectedRss);
        if(selectedRss!=null) {
            OptionsUtils.saveSelectedRss(this, selectedRss);
        }
        super.onPause();
    }
}
