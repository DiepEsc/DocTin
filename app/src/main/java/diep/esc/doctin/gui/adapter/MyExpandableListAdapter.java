package diep.esc.doctin.gui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
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
 * Created by Diep on 04/04/2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    public static final int TYPE_DEFAULT_RSS = 0;
    public static final int TYPE_CUSTOM_RSS = 1;
    public static final int TYPE_ADDING_RSS = 2;


    private static final String TAG = "log_MyExpandAdapter";
    private Context context;
    private List<String> listRssProvider, listCustomRss;
    private HashMap<String, List<RssSource>> mapListRssSource;
//    private View.OnClickListener onRadioClickListener;

    //private int selectedGroup,selectedChild;
    private String selectedRss;

    public MyExpandableListAdapter(Context context, List<String> listRssProvider, HashMap<String,
            List<RssSource>> mapListRssSource) {
        this.context = context;
        this.listRssProvider = listRssProvider;
        this.mapListRssSource = mapListRssSource;
        listCustomRss=OptionsUtils.getCustomRssList(context);
    }

    public MyExpandableListAdapter(Context context, HashMap<String,
            List<RssSource>> mapListRssSource) {
        this(context, extractKeyList(mapListRssSource), mapListRssSource);
    }

    private static final ArrayList<String> extractKeyList(HashMap<String, List<RssSource>> map) {
        ArrayList<String> list = new ArrayList(map.size());
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    @Override
    public int getGroupCount() {
        return mapListRssSource.size() + 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition < listRssProvider.size()) {
            return mapListRssSource.get(listRssProvider.get(groupPosition)).size();
        }
        return listCustomRss.size() + 1;
    }

    @Override
    public int getChildTypeCount() {
        return 3;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        if (groupPosition < listRssProvider.size()) return TYPE_DEFAULT_RSS;
        if (childPosition < listCustomRss.size()) return TYPE_CUSTOM_RSS;
        return TYPE_ADDING_RSS;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (groupPosition < listRssProvider.size()) {
            return listRssProvider.get(groupPosition);
        }
        return "Others";
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (getChildType(groupPosition, childPosition)) {
            case TYPE_DEFAULT_RSS:
                return mapListRssSource.get(listRssProvider.get(groupPosition)).get(childPosition);
            case TYPE_CUSTOM_RSS:
                return listCustomRss.get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_rss_group_list, null);
        }
        TextView txt = (TextView) convertView.findViewById(R.id.txt_rss_group);
        if (groupPosition < listRssProvider.size()) {
            txt.setText(listRssProvider.get(groupPosition));
        } else {
            txt.setText("Others");
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        switch (getChildType(groupPosition, childPosition)) {
            case TYPE_DEFAULT_RSS:
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_rss_list, null);
                }
                RadioButton rad = (RadioButton) convertView.findViewById(R.id.radio_rss_item);
                RssSource source = mapListRssSource.get(listRssProvider.get(groupPosition)).get(childPosition);
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
//                rad.setOnClickListener(onRadioClickListener);
                break;
            case TYPE_CUSTOM_RSS:
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_custom_rss_list, null);
                }
                rad = (RadioButton) convertView.findViewById(R.id.radio_custom);
                rad.setText(listCustomRss.get(childPosition));
                rad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            selectedRss = buttonView.getText().toString();
                            notifyDataSetChanged();
                        }
                    }
                });
                ImageButton button= (ImageButton) convertView.findViewById(R.id.button_del);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listCustomRss.remove(childPosition);
                        OptionsUtils.removeRssAt(context,childPosition);
                        notifyDataSetChanged();
                    }
                });
                break;
            default:
                if (convertView == null) {
                    convertView = View.inflate(context, R.layout.item_last_rss_list, null);
                }
                button= (ImageButton) convertView.findViewById(R.id.button_add);
                final EditText editText= (EditText) convertView.findViewById(R.id.edit_add_rss);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url=editText.getText().toString().trim();
                        Matcher matcher=Pattern.compile("^" + Patterns.WEB_URL.pattern() + "$")
                                .matcher(url);
                        if(url.length()>0&&matcher.find()){
                            selectedRss=url;
                            listCustomRss.add(url);
                            notifyDataSetChanged();
                            OptionsUtils.addRss(context,url);
                            editText.setText("");
                        }
                        else{
                            Toast.makeText(context,"invalid Rss link",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public String getSelectedRss() {
        return selectedRss;
    }

    public void setSelectedRss(String selectedRss) {
        this.selectedRss = selectedRss;
    }
}
