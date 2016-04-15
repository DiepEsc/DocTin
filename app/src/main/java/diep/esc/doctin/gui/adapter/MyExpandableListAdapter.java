package diep.esc.doctin.gui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import diep.esc.doctin.R;
import diep.esc.doctin.util.OptionsUtils;

/**
 * The adapter for ExpandableListView. That show the list of RSS as groups. And let user choose
 * a RSS, or input a RSS. <br/>
 * Created by Diep on 04/04/2016.
 *
 * @author Diep
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    public static final int TYPE_DEFAULT_RSS = 0;
    public static final int TYPE_CUSTOM_RSS = 1;
    public static final int TYPE_ADDING_RSS = 2;
//    private static final String TAG = "log_MyExpandAdapter";

    /**
     * Current context
     */
    private Context mContext;

    /**
     * A list of rss provider's name
     */
    private List<String> mRssProviders;

    /**
     * A list of custom RSS link, with are user input
     */
    private List<String> mCustomRssList;

    /**
     * A hash map witch keys are rss provider name and values are RssSource lists
     * (A RssSource is contain RSS title and, RSS link)
     */
    private HashMap<String, List<RssSource>> mapListRssSource;

    /**
     * Selected RSS which is selected by user in GUI (RadioButton inside ExpandableListView)
     */
    private String selectedRss;

    /**
     * @param context          Current context
     * @param rssProviders     A list of rss provider's name
     * @param mapListRssSource A hash map which keys are rss provider name and values are
     *                         RssSource lists (A RssSource is contain RSS title and, RSS link)
     */
    public MyExpandableListAdapter(Context context, List<String> rssProviders, HashMap<String,
            List<RssSource>> mapListRssSource) {
        this.mContext = context;
        this.mRssProviders = rssProviders;
        this.mapListRssSource = mapListRssSource;
        mCustomRssList = OptionsUtils.getCustomRssList(context);
    }

    /**
     * @param context          Current context
     * @param mapListRssSource A hash map witch keys are rss provider name and values are
     *                         RssSource lists (A RssSource is contain RSS title and, RSS link)
     */
    public MyExpandableListAdapter(Context context, HashMap<String,
            List<RssSource>> mapListRssSource) {
        this(context, extractKeyList(mapListRssSource), mapListRssSource);
    }

    /**
     * Automatically generate list of RSS provider's name from RSSSource map
     *
     * @param map A hash map which keys are rss provider name and values are RssSource lists
     * @return A list of rss provider's name
     */
    @NonNull
    private static ArrayList<String> extractKeyList(
            @NonNull HashMap<String, List<RssSource>> map) {
        ArrayList<String> list = new ArrayList<String>(map.size());
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * The groups are RSS providers and a custom rss list
     */
    @Override
    public int getGroupCount() {
        return mapListRssSource.size() + 1;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < mRssProviders.size()) {
            return mapListRssSource.get(mRssProviders.get(groupPosition)).size();
        }
        return mCustomRssList.size() + 1;
    }

    /**
     * @return 3. They are {@link #TYPE_DEFAULT_RSS}, {@link #TYPE_CUSTOM_RSS}
     * and {@link #TYPE_ADDING_RSS}
     */
    @Override
    public int getChildTypeCount() {
        return 3;
    }

    /**
     * @param groupPosition The position of the group which contains child item
     * @param childPosition the position of child item in the group
     * @return Child item type. {@link #TYPE_DEFAULT_RSS} or {@link #TYPE_CUSTOM_RSS}
     * or {@link #TYPE_ADDING_RSS}
     */
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if (groupPosition < mRssProviders.size()) return TYPE_DEFAULT_RSS;
        if (childPosition < mCustomRssList.size()) return TYPE_CUSTOM_RSS;
        return TYPE_ADDING_RSS;
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition < mRssProviders.size()) {
            return mRssProviders.get(groupPosition);
        }
        return "Others";
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (getChildType(groupPosition, childPosition)) {
            case TYPE_DEFAULT_RSS:
                return mapListRssSource.get(mRssProviders.get(groupPosition)).get(childPosition);
            case TYPE_CUSTOM_RSS:
                return mCustomRssList.get(childPosition);
            default:
                return "adding";
        }
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_rss_group_list, null);
        }
        TextView txt = (TextView) convertView.findViewById(R.id.txt_rss_group);
        if (groupPosition < mRssProviders.size()) {
            txt.setText(mRssProviders.get(groupPosition));
        } else {
            txt.setText("Others");
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {
        switch (getChildType(groupPosition, childPosition)) {
            case TYPE_DEFAULT_RSS:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_rss_list, null);
                }
                RadioButton rad = (RadioButton) convertView.findViewById(R.id.radio_rss_item);
                RssSource source = mapListRssSource.get(mRssProviders.get(groupPosition))
                        .get(childPosition);
                rad.setText(source.title);
                rad.setTag(source);
                rad.setChecked(source.url.equals(selectedRss));
                rad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectedRss = ((RssSource) buttonView.getTag()).url;
                            notifyDataSetChanged();
                        }
                    }
                });
                break;
            case TYPE_CUSTOM_RSS:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_custom_rss_list, null);
                }
                rad = (RadioButton) convertView.findViewById(R.id.radio_custom);
                ImageButton button = (ImageButton) convertView.findViewById(R.id.button_del);
                rad.setText(mCustomRssList.get(childPosition));
                rad.setChecked(mCustomRssList.get(childPosition).equals(selectedRss));
                rad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectedRss = buttonView.getText().toString();
                            notifyDataSetChanged();
                        }
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCustomRssList.remove(childPosition);
                        OptionsUtils.removeRssAt(mContext, childPosition);
                        notifyDataSetChanged();
                    }
                });
                break;
            default:
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.item_last_rss_list, null);
                }
                button = (ImageButton) convertView.findViewById(R.id.button_add);
                final EditText editText = (EditText) convertView.findViewById(R.id.edit_add_rss);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = editText.getText().toString().trim();
                        Matcher matcher = Pattern.compile("^" + Patterns.WEB_URL.pattern() + "$")
                                .matcher(url);
                        if (url.length() > 0 && matcher.find()) {
                            selectedRss = url;
                            mCustomRssList.add(url);
                            notifyDataSetChanged();
                            OptionsUtils.addRss(mContext, url);
                            editText.setText("");
                        } else {
                            Toast.makeText(mContext, "invalid Rss link", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        return convertView;
    }

    /**
     * @return false. Prevent user select the item. User must select a view inside the item instead.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    /**
     * @return the selected RSS
     */
    public String getSelectedRss() {
        return selectedRss;
    }

    /**
     * @param selectedRss set the value to this.selectedRss property
     */
    public void setSelectedRss(String selectedRss) {
        this.selectedRss = selectedRss;
        notifyDataSetChanged();
    }
}
