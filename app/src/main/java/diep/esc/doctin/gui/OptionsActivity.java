package diep.esc.doctin.gui;

import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarContainer;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import diep.esc.doctin.R;
import diep.esc.doctin.gui.adapter.MyExpandableListAdapter;
import diep.esc.doctin.gui.adapter.RssSource;
import diep.esc.doctin.util.OptionsUtils;

/**
 * Created by Diep on 04/04/2016.
 * This Activity allow user to choose a RSS source or add a RSS source
 *
 * @author Diep
 * @see MyExpandableListAdapter
 */
public class OptionsActivity extends AppCompatActivity {
    //    private static final String TAG = "log_Options";
    private ExpandableListView mExpandableListView;
    private MyExpandableListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        mExpandableListView = (ExpandableListView) findViewById(R.id.e_list_view);
        mAdapter = new MyExpandableListAdapter(this, RssSource.loadDefault(this));
        mAdapter.setSelectedRss(OptionsUtils.getSelectedRss(this));
        mExpandableListView.setAdapter(mAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        String selectedRss = mAdapter.getSelectedRss();
        if (selectedRss != null) {
            OptionsUtils.saveSelectedRss(this, selectedRss);
        }
        super.onPause();
    }
}
